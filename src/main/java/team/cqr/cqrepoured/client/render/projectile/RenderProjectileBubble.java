package team.cqr.cqrepoured.client.render.projectile;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import team.cqr.cqrepoured.CQRMain;
import team.cqr.cqrepoured.client.render.RenderSpriteBase;
import team.cqr.cqrepoured.entity.projectiles.ProjectileBubble;

public class RenderProjectileBubble extends RenderSpriteBase<ProjectileBubble>
{
	public RenderProjectileBubble(EntityRendererManager renderManager)
	{
		super(renderManager, new ResourceLocation(CQRMain.MODID, "textures/entity/bubble.png"));
	}
}