package team.cqr.cqrepoured.client.render.entity.boss;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ResourceLocation;
import team.cqr.cqrepoured.CQRMain;
import team.cqr.cqrepoured.client.model.entity.boss.ModelNetherDragonBodyParts;
import team.cqr.cqrepoured.client.render.entity.RenderMultiPartPart;
import team.cqr.cqrepoured.entity.boss.netherdragon.SubEntityNetherDragonSegment;

public class RenderCQRNetherDragonSegment extends RenderMultiPartPart<SubEntityNetherDragonSegment> {

	public static final ResourceLocation TEXTURES_NORMAL = new ResourceLocation(CQRMain.MODID, "textures/entity/boss/nether_dragon.png");
	public static final ResourceLocation TEXTURES_SKELETAL = new ResourceLocation(CQRMain.MODID, "textures/entity/boss/nether_dragon_skeletal.png");

	private final ModelBase modelNormal;
	private final ModelBase modelSkeletal;
	private final ModelBase modelTail;
	private final ModelBase modelTailTip;

	public RenderCQRNetherDragonSegment(EntityRendererManager manager) {
		super(manager);
		this.modelNormal = new ModelNetherDragonBodyParts.ModelNetherDragonBodyPart();
		this.modelSkeletal = new ModelNetherDragonBodyParts.ModelNetherDragonBodyPartSkeletal();
		this.modelTail = new ModelNetherDragonBodyParts.ModelNetherDragonBodyTailStart();
		this.modelTailTip = new ModelNetherDragonBodyParts.ModelNetherDragonBodyTailTip();
	}

	@Override
	public void doRender(SubEntityNetherDragonSegment entity, double x, double y, double z, float entityYaw, float partialTicks) {
		GlStateManager.pushMatrix();
		GlStateManager.translate((float) x, (float) y, (float) z);

		ModelBase model = null;
		if (entity.isSkeletal()) {
			model = this.modelSkeletal;

			if (entity.ticksExisted % 5 == 0) {
				// Flames
				ClientWorld world = Minecraft.getMinecraft().world;
				double dx = entity.posX + (-0.25 + (0.5 * world.rand.nextDouble()));
				double dy = 0.5 + entity.posY + (-0.25 + (0.5 * world.rand.nextDouble()));
				double dz = entity.posZ + (-0.25 + (0.5 * world.rand.nextDouble()));
				world.spawnParticle(ParticleTypes.FLAME, dx, dy, dz, 0, 0, 0);
			}
		} else {
			model = this.modelNormal;
			if (entity.getPartIndex() <= 1) {
				if (entity.getPartIndex() <= 0) {
					model = this.modelTailTip;
				} else {
					model = this.modelTail;
				}
			}
		}
		GlStateManager.scale(-1.0F, -1.0F, 1.0F);
		// GL11.glScalef(1.0f, 1.0f, 1.0f);

		float yawDiff = entity.rotationYaw - entity.prevRotationYaw;
		if (yawDiff > 180) {
			yawDiff -= 360;
		} else if (yawDiff < -180) {
			yawDiff += 360;
		}
		float yaw = entity.prevRotationYaw + yawDiff * partialTicks;

		GlStateManager.rotate(yaw, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotate(entity.rotationPitch, 1.0F, 0.0F, 0.0F);
		this.bindTexture(this.getEntityTexture(entity));

		/*
		 * if(entity.getParent() != null) { if(entity.getParent().deathTicks > 600) { GlStateManager.color(new Float(0.3F *
		 * (0.25 * Math.sin(0.75 * entity.ticksExisted)
		 * + 0.5)),0,0, 1F); } }
		 */
		/*
		 * if(entity.isSkeletal() && entity.getHealthPercentage() > 0) { GlStateManager.color(entity.getHealthPercentage(), 0F,
		 * 0F, 0.5F); }
		 */
		if (entity.getParent() != null && entity.getParent().deathTime > 0 && entity.getParent().deathTime % 5 == 0) {
			float f = (entity.getParent().getRNG().nextFloat() - 0.5F) * 8.0F;
			float f1 = (entity.getParent().getRNG().nextFloat() - 0.5F) * 4.0F;
			float f2 = (entity.getParent().getRNG().nextFloat() - 0.5F) * 8.0F;
			Minecraft.getMinecraft().world.spawnParticle(entity.getParent().getDeathAnimParticles(), entity.posX + f, entity.posY + 2.0D + f1, entity.posZ + f2, 0.0D, 0.0D, 0.0D);
		}

		if (!entity.isInvisible()) {
			model.render(entity, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
		}
		GlStateManager.popMatrix();
	}

	@Override
	public boolean shouldRender(SubEntityNetherDragonSegment livingEntity, ICamera camera, double camX, double camY, double camZ) {
		return this.superShouldRender(livingEntity, camera, camX, camY, camZ) && !livingEntity.isDead;
	}

	@Override
	protected ResourceLocation getEntityTexture(SubEntityNetherDragonSegment entity) {
		if (!entity.isSkeletal()) {
			// Custom texture start
			if (entity.getParent() != null && entity.getParent().hasTextureOverride()) {
				return entity.getParent().getTextureOverride();
			}
			// Custom texture end
			return TEXTURES_NORMAL;
		}
		return TEXTURES_SKELETAL;
	}

}
