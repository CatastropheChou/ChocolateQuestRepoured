package team.cqr.cqrepoured.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;

public class RenderSpriteBase<T extends Entity> extends EntityRenderer<T>
{
	protected final ResourceLocation TEXTURE;

	protected RenderSpriteBase(EntityRendererManager manager, ResourceLocation sprite)
	{
		super(manager);
		this.TEXTURE = sprite;
	}

	@Override
	protected int getBlockLightLevel(T entity, BlockPos pos)
	{
		return 15;
	}

	@Override
	public void render(T entity, float entityYaw, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight) {
		matrixStack.pushPose();
		//this.bindEntityTexture(entity); RenderSystem.bindTexture()???
		//matrixStack.translate((float) x, (float) y, (float) z);
		//RenderSystem.enableRescaleNormal();
		matrixStack.scale(.75F, .75F, .75F);
		//Tessellator tessellator = Tessellator.getInstance();
		//BufferBuilder bufferbuilder = tessellator.getBuffer();
		matrixStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
		matrixStack.mulPose(Vector3f.YP.rotationDegrees(180.0F));
		//GlStateManager.rotate((this.renderManager.options.thirdPersonView == 2 ? -1 : 1) * -this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);

		IVertexBuilder bufferBuilder = buffer.getBuffer(RenderType.entityTranslucent(this.getTextureLocation(entity)));
		MatrixStack.Entry entry = matrixStack.last();
		Matrix4f matrix4f = entry.pose();
		Matrix3f matrix3f = entry.normal();

		//if (this.renderOutlines) {
		//	GlStateManager.enableColorMaterial();
		//	GlStateManager.enableOutlineMode(this.getTeamColor(entity));
		//}

		//bufferBuilder.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);
		bufferBuilder.vertex(matrix4f, -0.5F, -0.25F, 0.0F)
				.color(255, 255, 255, 255)
				.uv(0.0F, 1.0F)
				.overlayCoords(OverlayTexture.NO_OVERLAY)
				.uv2(packedLight)
				.normal(matrix3f, 0.0F, 1.0F, 0.0F)
				.endVertex();

		bufferBuilder.vertex(matrix4f, 0.5F, -0.25F, 0.0F)
				.color(255, 255, 255, 255)
				.uv(1.0F, 1.0F)
				.overlayCoords(OverlayTexture.NO_OVERLAY)
				.uv2(packedLight)
				.normal(matrix3f, 0.0F, 1.0F, 0.0F)
				.endVertex();

		bufferBuilder.vertex(matrix4f, 0.5F, 0.75F, 0.0F)
				.color(255, 255, 255, 255)
				.uv(1.0F, 0.0F)
				.overlayCoords(OverlayTexture.NO_OVERLAY)
				.uv2(packedLight)
				.normal(matrix3f, 0.0F, 1.0F, 0.0F)
				.endVertex();

		bufferBuilder.vertex(matrix4f, -0.5F, 0.75F, 0.0F)
				.color(255, 255, 255, 255)
				.uv(0.0F, 0.0F)
				.overlayCoords(OverlayTexture.NO_OVERLAY)
				.uv2(packedLight)
				.normal(matrix3f, 0.0F, 1.0F, 0.0F)
				.endVertex();

		//tessellator.draw();

		//if (this.renderOutlines) {
		//	GlStateManager.disableOutlineMode();
		//	GlStateManager.disableColorMaterial();
		//}

		//RenderSystem.disableRescaleNormal();
		matrixStack.popPose();
		//GlStateManager.disableRescaleNormal();
		//GlStateManager.popMatrix();
		super.render(entity, entityYaw, partialTicks, matrixStack, buffer, packedLight);
	}

	@Override
	public ResourceLocation getTextureLocation(T pEntity)
	{
		return TEXTURE;
	}
}
