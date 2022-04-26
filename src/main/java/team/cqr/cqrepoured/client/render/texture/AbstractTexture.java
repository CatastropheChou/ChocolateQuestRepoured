package team.cqr.cqrepoured.client.render.texture;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.function.BiFunction;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.Texture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.data.TextureMetadataSection;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

public abstract class AbstractTexture extends Texture {

	protected static final Logger LOGGER = LogManager.getLogger();
	protected final ResourceLocation originalLocation;
	protected final ResourceLocation location;

	protected AbstractTexture(ResourceLocation originalLocation, ResourceLocation location) {
		this.originalLocation = originalLocation;
		this.location = location;
	}

	protected static ResourceLocation get(ResourceLocation originalLocation, String appendix, BiFunction<ResourceLocation, ResourceLocation, Texture> constructor) {
		if (!RenderSystem.isOnRenderThreadOrInit()) {
			throw new IllegalThreadStateException();
		}
		ResourceLocation location = appendBeforeEnding(originalLocation, appendix);
		TextureManager texManager = Minecraft.getInstance().getTextureManager();
		if (texManager.getTexture(location) == null) {
			texManager.register(location, constructor.apply(originalLocation, location));
		}
		return location;
	}

	protected static ResourceLocation appendBeforeEnding(ResourceLocation location, String suffix) {
		String path = location.getPath();
		int i = path.lastIndexOf('.');
		return new ResourceLocation(location.getNamespace(), path.substring(0, i) + suffix + path.substring(i));
	}

	@Override
	public void load(IResourceManager resourceManager) throws IOException {
		Minecraft mc = Minecraft.getInstance();
		TextureManager textureManager = mc.getTextureManager();
		Texture originalTexture;
		try {
			originalTexture = mc.submit(() -> {
				Texture texture = textureManager.getTexture(this.originalLocation);
				if (texture == null) {
					texture = new SimpleTexture(this.originalLocation);
					textureManager.register(this.originalLocation, texture);
				}
				return texture;
			}).get();
		} catch (InterruptedException | ExecutionException e) {
			throw new IOException("Failed loading original texture: " + this.originalLocation, e);
		}

		NativeImage originalImage;
		TextureMetadataSection textureMetadata = null;
		NativeImage newImage;
		boolean updateOriginal;
		try (IResource iresource = resourceManager.getResource(location)) {
			originalImage = originalTexture instanceof DynamicTexture ? ((DynamicTexture) originalTexture).getPixels() : NativeImage.read(iresource.getInputStream());
			newImage = new NativeImage(originalImage.getWidth(), originalImage.getHeight(), false);

			try {
				textureMetadata = iresource.getMetadata(TextureMetadataSection.SERIALIZER);
			} catch (RuntimeException e) {
				LOGGER.warn("Failed reading metadata of: {}", location, e);
			}

			updateOriginal = this.onLoadTexture(iresource, originalImage, newImage);
		}

		boolean blur = textureMetadata != null && textureMetadata.isBlur();
		boolean clamp = textureMetadata != null && textureMetadata.isClamp();

		if (!RenderSystem.isOnRenderThreadOrInit()) {
			RenderSystem.recordRenderCall(() -> {
				uploadSimple(this.getId(), newImage, blur, clamp);

				if (updateOriginal) {
					if (originalTexture instanceof DynamicTexture) {
						((DynamicTexture) originalTexture).upload();
					} else {
						uploadSimple(originalTexture.getId(), originalImage, blur, clamp);
					}
				}
			});
		} else {
			uploadSimple(this.getId(), newImage, blur, clamp);

			if (updateOriginal) {
				if (originalTexture instanceof DynamicTexture) {
					((DynamicTexture) originalTexture).upload();
				} else {
					uploadSimple(originalTexture.getId(), originalImage, blur, clamp);
				}
			}
		}
	}

	/**
	 * @return true to indicate that the original texture was changed and should be updated.
	 */
	protected abstract boolean onLoadTexture(IResource resource, NativeImage originalImage, NativeImage newImage);

	private static void uploadSimple(int texture, NativeImage image, boolean blur, boolean clamp) {
		TextureUtil.prepareImage(texture, 0, image.getWidth(), image.getHeight());
		image.upload(0, 0, 0, 0, 0, image.getWidth(), image.getHeight(), blur, clamp, false, true);
	}

}
