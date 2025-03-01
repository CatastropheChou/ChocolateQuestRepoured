package team.cqr.cqrepoured.client.render.entity.boss.netherdragon;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import team.cqr.cqrepoured.CQRMain;
import team.cqr.cqrepoured.client.model.geo.entity.boss.ModelNetherDragonBodyGeo;
import team.cqr.cqrepoured.client.render.entity.RenderEntityGeo;
import team.cqr.cqrepoured.entity.boss.netherdragon.SubEntityNetherDragonSegment;

public class RenderNetherDragonBodyPart extends RenderEntityGeo<SubEntityNetherDragonSegment> {
	
	private static final ResourceLocation TEXTURE = CQRMain.prefix("textures/entity/boss/nether_dragon_body.png");
	private static final ResourceLocation MODEL_RESLOC = CQRMain.prefix("geo/entity/boss/netherdragon/body/nether_dragon_body_normal.geo.json");
	
	public RenderNetherDragonBodyPart(EntityRendererManager renderManager) {
		this(renderManager, new ModelNetherDragonBodyGeo(MODEL_RESLOC, TEXTURE, "boss/nether_dragon"));
	}

	public RenderNetherDragonBodyPart(EntityRendererManager renderManager, AnimatedGeoModel<SubEntityNetherDragonSegment> modelProvider) {
		super(renderManager, modelProvider);
	}
	
}
