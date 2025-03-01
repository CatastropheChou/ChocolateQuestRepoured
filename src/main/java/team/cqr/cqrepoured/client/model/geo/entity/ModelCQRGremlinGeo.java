package team.cqr.cqrepoured.client.model.geo.entity;

import net.minecraft.util.ResourceLocation;
import team.cqr.cqrepoured.CQRMain;
import team.cqr.cqrepoured.client.model.geo.AbstractModelGeoCQRStandardBiped;
import team.cqr.cqrepoured.entity.mobs.EntityCQRGremlin;

public class ModelCQRGremlinGeo extends AbstractModelGeoCQRStandardBiped<EntityCQRGremlin> {
	
	protected final ResourceLocation STANDARD_GREMLIN_ANIMATIONS = CQRMain.prefix("animations/biped_gremlin.animation.json");

	public ModelCQRGremlinGeo(ResourceLocation model, ResourceLocation textureDefault, String entityName) {
		super(model, textureDefault, entityName);
	}

	@Override
	protected String getHeadBoneIdent() {
		return STANDARD_HEAD_IDENT;
	}
	
	@Override
	public ResourceLocation getAnimationFileLocation(EntityCQRGremlin animatable) {
		return STANDARD_GREMLIN_ANIMATIONS;
	}

}
