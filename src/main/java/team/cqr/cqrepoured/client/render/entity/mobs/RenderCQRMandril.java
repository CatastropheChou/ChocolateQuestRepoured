package team.cqr.cqrepoured.client.render.entity.mobs;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.inventory.EquipmentSlotType;
import org.lwjgl.opengl.GL11;
import team.cqr.cqrepoured.client.model.entity.mobs.ModelCQRMandril;
import team.cqr.cqrepoured.client.render.entity.RenderCQREntity;
import team.cqr.cqrepoured.entity.mobs.EntityCQRMandril;

public class RenderCQRMandril extends RenderCQREntity<EntityCQRMandril> {

	public RenderCQRMandril(EntityRendererManager rendermanagerIn) {
		super(rendermanagerIn, new ModelCQRMandril(), 0.5F, "mob/mandril", 1.0D, 1.0D);
	}

	@Override
	protected void renderModel(EntityCQRMandril entitylivingbaseIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor) {
		if (entitylivingbaseIn.isSitting()) {
			GL11.glTranslatef(0, 0, 0.25F);
		} else {
			GL11.glTranslatef(0, 0, 0.15F);
		}
		super.renderModel(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
	}

	@Override
	public void setupHeadOffsets(ModelRenderer modelRenderer, EquipmentSlotType slot) {
		this.applyRotations(modelRenderer);
		GlStateManager.translate(0.0D, 0.5D, 0.0D);
		this.resetRotations(modelRenderer);
	}

	@Override
	public void setupBodyOffsets(ModelRenderer modelRenderer, EquipmentSlotType slot) {
		this.applyRotations(modelRenderer);
		GlStateManager.translate(0.0D, 0.0D, -0.28125D);
		this.resetRotations(modelRenderer);
	}

	@Override
	public void setupRightArmOffsets(ModelRenderer modelRenderer, EquipmentSlotType slot) {
		this.applyRotations(modelRenderer);
		GlStateManager.translate(0.0D, 0.125D, 0.0D);
		this.resetRotations(modelRenderer);
	}

	@Override
	public void setupLeftArmOffsets(ModelRenderer modelRenderer, EquipmentSlotType slot) {
		this.applyRotations(modelRenderer);
		GlStateManager.translate(0.0D, 0.125D, 0.0D);
		this.resetRotations(modelRenderer);
	}

}
