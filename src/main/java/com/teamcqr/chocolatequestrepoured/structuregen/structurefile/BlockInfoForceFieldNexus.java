package com.teamcqr.chocolatequestrepoured.structuregen.structurefile;

import com.teamcqr.chocolatequestrepoured.CQRMain;
import com.teamcqr.chocolatequestrepoured.init.CQRBlocks;
import com.teamcqr.chocolatequestrepoured.structuregen.inhabitants.DungeonInhabitant;
import com.teamcqr.chocolatequestrepoured.structureprot.ProtectedRegion;
import com.teamcqr.chocolatequestrepoured.util.BlockPlacingHelper;

import io.netty.buffer.ByteBuf;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.template.PlacementSettings;

public class BlockInfoForceFieldNexus extends AbstractBlockInfo {

	public BlockInfoForceFieldNexus(int x, int y, int z) {
		super(x, y, z);
	}

	public BlockInfoForceFieldNexus(BlockPos pos) {
		super(pos);
	}

	@Override
	public void generateAt(World world, BlockPos dungeonPos, BlockPos dungeonPartPos, PlacementSettings settings, DungeonInhabitant dungeonMob, ProtectedRegion protectedRegion, BlockPos pos) {
		if (protectedRegion == null) {
			BlockPlacingHelper.setBlockState(world, pos, Blocks.AIR.getDefaultState(), 18, false);
			return;
		}

		BlockPlacingHelper.setBlockState(world, pos, CQRBlocks.FORCE_FIELD_NEXUS.getDefaultState(), 18, false);

		if (world.getBlockState(pos) == CQRBlocks.FORCE_FIELD_NEXUS) {
			protectedRegion.addBlockDependency(pos);
		} else {
			CQRMain.logger.warn("Failed to place force field nexus at {}", pos);
		}
	}

	@Override
	public byte getId() {
		return NEXUS_INFO_ID;
	}

	@Override
	protected void writeToByteBufInternal(ByteBuf buf, BlockStatePalette blockStatePalette, NBTTagList compoundTagList) {

	}

	@Override
	protected void readFromByteBufInternal(ByteBuf buf, BlockStatePalette blockStatePalette, NBTTagList compoundTagList) {

	}

}
