package team.cqr.cqrepoured.client.render.tileentity;

import net.minecraft.block.Block;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;
import team.cqr.cqrepoured.CQRMain;
import team.cqr.cqrepoured.block.BlockExporterChest;
import team.cqr.cqrepoured.block.BlockExporterChestCustom;
import team.cqr.cqrepoured.client.model.ModelChestLeft;
import team.cqr.cqrepoured.client.model.ModelChestNormal;
import team.cqr.cqrepoured.client.model.ModelChestRight;
import team.cqr.cqrepoured.tileentity.TileEntityExporterChest;

public class TileEntityExporterChestRenderer extends TileEntityRenderer<TileEntityExporterChest> {

	private final ModelChestNormal chestNormal = new ModelChestNormal();
	private final ModelChestNormal chestRight = new ModelChestRight();
	private final ModelChestNormal chestLeft = new ModelChestLeft();

	@Override
	public void render(TileEntityExporterChest te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		BlockPos pos = te.getPos();
		World world = te.getWorld();
		Block block = te.getBlockType();

		Direction facing = Direction.SOUTH;
		ModelChestNormal model = this.chestNormal;
		ResourceLocation texture = new ResourceLocation(CQRMain.MODID, "textures/entity/chest/exporter_chest.png");
		ResourceLocation overlayTexture = new ResourceLocation("textures/items/stick.png");

		if (world != null && world.getBlockState(pos).getPropertyKeys().contains(BlockHorizontal.FACING)) {
			facing = world.getBlockState(pos).getValue(HorizontalBlock.FACING);

			if (this.isDoubleChest(world, pos)) {
				if (this.isChest(world.getBlockState(pos.offset(facing.rotateY())).getBlock())) {
					model = this.chestRight;
					texture = new ResourceLocation(CQRMain.MODID, "textures/entity/chest/exporter_chest_right.png");
				} else {
					model = this.chestLeft;
					texture = new ResourceLocation(CQRMain.MODID, "textures/entity/chest/exporter_chest_left.png");
				}
			}
		}

		if (block instanceof BlockExporterChest) {
			overlayTexture = ((BlockExporterChest) block).getOverlayTexture();
		}

		if (block instanceof BlockExporterChestCustom && this.rendererDispatcher.cameraHitResult != null && te.getPos().equals(this.rendererDispatcher.cameraHitResult.getBlockPos())) {
			this.drawNameplate(te, te.getDisplayName().getFormattedText(), x, y, z, 16);
		}

		GlStateManager.pushMatrix();

		GlStateManager.enableDepth();
		GlStateManager.depthFunc(515);
		GlStateManager.depthMask(true);
		GlStateManager.enableRescaleNormal();

		GlStateManager.translate((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
		GlStateManager.scale(1.0F, -1.0F, -1.0F);
		GlStateManager.rotate(facing.getHorizontalAngle(), 0.0F, 1.0F, 0.0F);

		Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
		model.renderAll();

		GlStateManager.disableRescaleNormal();

		GlStateManager.popMatrix();

		this.renderOverlay(x, y, z, overlayTexture, facing.getOpposite().getHorizontalAngle());
	}

	private boolean isChest(Block block) {
		return block instanceof BlockExporterChest;
	}

	private boolean isDoubleChest(World world, BlockPos pos) {
		for (Direction facing : Direction.HORIZONTALS) {
			if (this.isChest(world.getBlockState(pos.offset(facing)).getBlock())) {
				return true;
			}
		}
		return false;
	}

	private void renderOverlay(double x, double y, double z, ResourceLocation texture, double rotation) {
		GL11.glPushMatrix();
		Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
		GL11.glTranslated(x + 0.5D, y + 0.5D, z + 0.5D);

		for (Direction side : Direction.HORIZONTALS) {
			GL11.glPushMatrix();
			GL11.glRotated(side.getHorizontalAngle(), 0.0D, 1.0D, 0.0D);
			GL11.glTranslated(0.0, 0.0D, 0.4375D + 0.0001D);
			GL11.glScaled(0.75D, 0.75D, 0.75D);

			GL11.glBegin(GL11.GL_QUADS);
			GL11.glTexCoord2d(0.0D, 1.0D);
			GL11.glVertex3d(0.0D - 0.5D, 0.0D - 0.5D, 0.0D);

			GL11.glTexCoord2d(1.0D, 1.0D);
			GL11.glVertex3d(1.0D - 0.5D, 0.0D - 0.5D, 0.0D);

			GL11.glTexCoord2d(1.0D, 0.0D);
			GL11.glVertex3d(1.0D - 0.5D, 1.0D - 0.5D, 0.0D);

			GL11.glTexCoord2d(0.0D, 0.0D);
			GL11.glVertex3d(0.0D - 0.5D, 1.0D - 0.5D, 0.0D);
			GL11.glEnd();
			GL11.glPopMatrix();
		}

		GL11.glTranslated(0.0D, 0.375D + 0.0001D, 0.0D);
		GL11.glScaled(0.75D, 0.75D, 0.75D);

		GL11.glBegin(GL11.GL_QUADS);
		GL11.glTexCoord2d(0.0D, 1.0D);
		GL11.glVertex3d(0.0D - 0.5D, 0.0D, 1.0D - 0.5D);

		GL11.glTexCoord2d(1.0D, 1.0D);
		GL11.glVertex3d(1.0D - 0.5D, 0.0D, 1.0D - 0.5D);

		GL11.glTexCoord2d(1.0D, 0.0D);
		GL11.glVertex3d(1.0D - 0.5D, 0.0D, 0.0D - 0.5D);

		GL11.glTexCoord2d(0.0D, 0.0D);
		GL11.glVertex3d(0.0D - 0.5D, 0.0D, 0.0D - 0.5D);
		GL11.glEnd();
		GL11.glPopMatrix();
	}

}
