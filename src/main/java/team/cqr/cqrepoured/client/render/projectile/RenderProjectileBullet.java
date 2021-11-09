package team.cqr.cqrepoured.client.render.projectile;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import team.cqr.cqrepoured.client.render.RenderSpriteBase;
import team.cqr.cqrepoured.objects.entity.projectiles.ProjectileBullet;
import team.cqr.cqrepoured.util.Reference;

public class RenderProjectileBullet extends RenderSpriteBase<ProjectileBullet> {

	public static final ResourceLocation IRON = new ResourceLocation(Reference.MODID, "textures/entity/bullet_iron_single.png");
	public static final ResourceLocation GOLD = new ResourceLocation(Reference.MODID, "textures/entity/bullet_gold_single.png");
	public static final ResourceLocation DIAMOND = new ResourceLocation(Reference.MODID, "textures/entity/bullet_diamond_single.png");
	public static final ResourceLocation FIRE = new ResourceLocation(Reference.MODID, "textures/entity/bullet_fire_single.png");

	public RenderProjectileBullet(RenderManager renderManager) {
		super(renderManager, IRON);
	}


	@Override
	protected ResourceLocation getEntityTexture(ProjectileBullet entity) {

		switch(entity.getType()) {
		case 2:
			return GOLD;
		case 3:
			return DIAMOND;
		case 4:
			return FIRE;
		default:
			return super.getEntityTexture(entity);
		}
	}

}
