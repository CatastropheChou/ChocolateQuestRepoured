package team.cqr.cqrepoured.client.render.entity.layer.equipment;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;
import team.cqr.cqrepoured.client.model.entity.ModelCQRBiped;
import team.cqr.cqrepoured.client.render.entity.RenderCQREntity;
import team.cqr.cqrepoured.client.render.entity.layer.AbstractLayerCQR;
import team.cqr.cqrepoured.client.render.texture.InvisibilityTexture;
import team.cqr.cqrepoured.entity.bases.AbstractEntityCQR;

public class LayerCQREntityCape extends AbstractLayerCQR {

	public LayerCQREntityCape(RenderCQREntity<?> renderCQREntity) {
		super(renderCQREntity);
	}

	@Override
	public void doRenderLayer(AbstractEntityCQR entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
		if (!(this.entityRenderer.getMainModel() instanceof ModelCQRBiped)) {
			return;
		}

		if (/* entitylivingbaseIn.hasPlayerInfo() && */ !entitylivingbaseIn.isInvisible() && entitylivingbaseIn.hasCape() && entitylivingbaseIn.getResourceLocationOfCape() != null) {
			ItemStack itemstack = entitylivingbaseIn.getItemStackFromSlot(EquipmentSlotType.CHEST);

			if (itemstack.getItem() != Items.ELYTRA) {
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				GlStateManager.pushMatrix();
				GlStateManager.translate(0.0F, 0.0F, 0.125F);
				// ChasingPos: Positions of the cape
				double d0 = entitylivingbaseIn.prevPosX + (entitylivingbaseIn.posX - entitylivingbaseIn.prevPosX) * partialTicks - (entitylivingbaseIn.prevPosX + (entitylivingbaseIn.posX - entitylivingbaseIn.prevPosX) * partialTicks);
				double d1 = entitylivingbaseIn.prevPosY + (entitylivingbaseIn.posY - entitylivingbaseIn.prevPosY) * partialTicks - (entitylivingbaseIn.prevPosY + (entitylivingbaseIn.posY - entitylivingbaseIn.prevPosY) * partialTicks);
				double d2 = entitylivingbaseIn.prevPosZ + (entitylivingbaseIn.posZ - entitylivingbaseIn.prevPosZ) * partialTicks - (entitylivingbaseIn.prevPosZ + (entitylivingbaseIn.posZ - entitylivingbaseIn.prevPosZ) * partialTicks);
				float f = entitylivingbaseIn.prevRenderYawOffset + (entitylivingbaseIn.renderYawOffset - entitylivingbaseIn.prevRenderYawOffset) * partialTicks;
				double d3 = MathHelper.sin(f * 0.017453292F);
				double d4 = (-MathHelper.cos(f * 0.017453292F));
				float f1 = (float) d1 * 10.0F;
				f1 = MathHelper.clamp(f1, -6.0F, 32.0F);
				float f2 = (float) (d0 * d3 + d2 * d4) * 100.0F;
				float f3 = (float) (d0 * d4 - d2 * d3) * 100.0F;

				if (f2 < 0.0F) {
					f2 = 0.0F;
				}

				float f4 = entitylivingbaseIn.prevRotationYaw + (entitylivingbaseIn.rotationYaw - entitylivingbaseIn.prevRotationPitch) * partialTicks;
				f1 = f1 + MathHelper.sin((entitylivingbaseIn.prevDistanceWalkedModified + (entitylivingbaseIn.distanceWalkedModified - entitylivingbaseIn.prevDistanceWalkedModified) * partialTicks) * 6.0F) * 32.0F * f4;

				if (entitylivingbaseIn.isSneaking()) {
					f1 += 25.0F;
				}

				GlStateManager.rotate(6.0F + f2 / 2.0F + f1, 1.0F, 0.0F, 0.0F);
				GlStateManager.rotate(f3 / 2.0F, 0.0F, 0.0F, 1.0F);
				GlStateManager.rotate(-f3 / 2.0F, 0.0F, 1.0F, 0.0F);
				GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
				// DONE: Base model for Human shaped entities for capes
				this.renderModel(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale);

				GlStateManager.popMatrix();
			}
		}
	}

	private void renderModel(AbstractEntityCQR entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
		boolean flag = entitylivingbaseIn.getInvisibility() > 0.0F;
		if (flag) {
			GlStateManager.alphaFunc(GL11.GL_GREATER, entitylivingbaseIn.getInvisibility());
			this.entityRenderer.bindTexture(InvisibilityTexture.get(entitylivingbaseIn.getResourceLocationOfCape()));
			((ModelCQRBiped) this.entityRenderer.getMainModel()).renderCape(0.0625F);
			GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
			GlStateManager.depthFunc(GL11.GL_EQUAL);
		}
		// Standard texture
		else {
			this.entityRenderer.bindTexture(entitylivingbaseIn.getResourceLocationOfCape());
		}
		((ModelCQRBiped) this.entityRenderer.getMainModel()).renderCape(0.0625F);
		if (flag) {
			GlStateManager.depthFunc(GL11.GL_LEQUAL);
		}
	}

	@Override
	public boolean shouldCombineTextures() {
		return false;
	}
}
