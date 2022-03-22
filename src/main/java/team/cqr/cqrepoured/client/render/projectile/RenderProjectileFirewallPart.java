package team.cqr.cqrepoured.client.render.projectile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import team.cqr.cqrepoured.entity.projectiles.ProjectileFireWallPart;

public class RenderProjectileFirewallPart extends EntityRenderer<ProjectileFireWallPart> {

	public RenderProjectileFirewallPart(EntityRendererManager renderManager) {
		super(renderManager);
	}

	@Override
	public void doRender(ProjectileFireWallPart entity, double x, double y, double z, float entityYaw, float partialTicks) {
		super.doRender(entity, x, y, z, entityYaw, partialTicks);
		GlStateManager.disableLighting();
		AtlasTexture texturemap = Minecraft.getMinecraft().getTextureMapBlocks();
		TextureAtlasSprite textureatlassprite = texturemap.getAtlasSprite("minecraft:blocks/fire_layer_0");
		GlStateManager.pushMatrix();
		GlStateManager.translate((float) x, (float) y, (float) z);

		float f = Math.min(entity.width, entity.height) * 1.8F;
		GlStateManager.scale(f, f, f);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		float f1 = 0.5F;
		float f4 = (float) (entity.posY - entity.getEntityBoundingBox().minY);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		float f5 = 0.0F;
		float f6 = textureatlassprite.getMaxU();
		float f7 = textureatlassprite.getMinV();
		float f8 = textureatlassprite.getMinU();
		float f9 = textureatlassprite.getMaxV();

		Minecraft.getMinecraft().renderEngine.bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);

		int iterations = 8;
		float rot = (360F / iterations);
		for (int i = 0; i < iterations; i++) {
			bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			bufferbuilder.pos((f1 - 0.0F), (0.0F - f4), f5).tex(f8, f9).endVertex();
			bufferbuilder.pos((-f1 - 0.0F), (0.0F - f4), f5).tex(f6, f9).endVertex();
			bufferbuilder.pos((-f1 - 0.0F), (1.4F - f4), f5).tex(f6, f7).endVertex();
			bufferbuilder.pos((f1 - 0.0F), (1.4F - f4), f5).tex(f8, f7).endVertex();
			tessellator.draw();
			GlStateManager.rotate(rot, 0F, 1F, 0F);
		}

		GlStateManager.popMatrix();
		GlStateManager.enableLighting();
	}

	@Override
	protected ResourceLocation getEntityTexture(ProjectileFireWallPart entity) {
		return null;
	}

}
