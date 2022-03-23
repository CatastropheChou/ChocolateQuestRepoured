package team.cqr.cqrepoured.world.structure.generation.generators.stronghold.spiral;

import net.minecraft.block.Blocks;
import net.minecraft.block.RotatedPillarBlock;
import net.minecraft.block.TorchBlock;
import net.minecraft.util.Direction;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import team.cqr.cqrepoured.init.CQRBlocks;
import team.cqr.cqrepoured.world.structure.generation.generation.part.BlockDungeonPart;
import team.cqr.cqrepoured.world.structure.generation.generation.preparable.PreparableBlockInfo;
import team.cqr.cqrepoured.world.structure.generation.generators.volcano.StairCaseHelper;

public class EntranceBuilderHelper {

	public static final int SEGMENT_LENGTH = 3;

	public static void buildEntranceSegment(BlockPos startPosCentered, BlockDungeonPart.Builder partBuilder, Direction direction) {
		// COrner 2 is always the reference location for the part (!)
		BlockPos corner1, corner2, pillar1, pillar2, torch1, torch2;
		corner1 = null;
		corner2 = null;
		// Pillars are in the middle of the part (on the expansion axis)
		pillar1 = null;
		pillar2 = null;
		// marks the positions of the torches
		torch1 = null;
		torch2 = null;
		// these mark the corners of the complete part
		switch (direction) {
		case EAST:
			corner1 = startPosCentered.offset(0, 0, -3);
			corner2 = startPosCentered.offset(3, 0, 3);
			pillar1 = startPosCentered.offset(1, 0, 2);
			pillar2 = startPosCentered.offset(1, 0, -2);
			torch1 = startPosCentered.offset(1, 4, 1);
			torch2 = startPosCentered.offset(1, 4, -1);
			break;
		case NORTH:
			corner1 = startPosCentered.offset(-3, 0, 0);
			corner2 = startPosCentered.offset(3, 0, -3);
			pillar1 = startPosCentered.offset(2, 0, -1);
			pillar2 = startPosCentered.offset(-2, 0, -1);
			torch1 = startPosCentered.offset(1, 4, -1);
			torch2 = startPosCentered.offset(-1, 4, -1);
			break;
		case SOUTH:
			corner1 = startPosCentered.offset(3, 0, 0);
			corner2 = startPosCentered.offset(-3, 0, 3);
			pillar1 = startPosCentered.offset(-2, 0, 1);
			pillar2 = startPosCentered.offset(2, 0, 1);
			torch1 = startPosCentered.offset(-1, 4, 1);
			torch2 = startPosCentered.offset(1, 4, 1);
			break;
		case WEST:
			corner1 = startPosCentered.offset(0, 0, 3);
			corner2 = startPosCentered.offset(-3, 0, -3);
			pillar1 = startPosCentered.offset(-1, 0, -2);
			pillar2 = startPosCentered.offset(-1, 0, 2);
			torch1 = startPosCentered.offset(-1, 4, -1);
			torch2 = startPosCentered.offset(-1, 4, 1);
			break;
		default:
			break;
		}
		if (corner1 != null && corner2 != null && pillar1 != null && pillar2 != null) {
			/*
			 * for (BlockPos airPos : BlockPos.getAllInBox(air1, air2)) { blockInfoList.add(new PreparableBlockInfo(airPos,
			 * Blocks.AIR.getDefaultState(), null)); }
			 */
			BlockPos.betweenClosed(corner1, corner2.offset(0, 6, 0)).forEach(t -> partBuilder.add(new PreparableBlockInfo(t, Blocks.AIR.defaultBlockState(), null)));

			buildFloorAndCeiling(corner1, corner2, 5, partBuilder);

			// Left torch -> Facing side: rotate right (90.0°)
			buildPillar(pillar1, partBuilder);
			partBuilder.add(new PreparableBlockInfo(torch1, CQRBlocks.UNLIT_TORCH.defaultBlockState().setValue(TorchBlock.FACING, StairCaseHelper.getFacingWithRotation(direction, Rotation.COUNTERCLOCKWISE_90)), null));
			// Right torch -> Facing side: rotate left (-90.0°)
			buildPillar(pillar2, partBuilder);
			partBuilder.add(new PreparableBlockInfo(torch2, CQRBlocks.UNLIT_TORCH.defaultBlockState().setValue(TorchBlock.FACING, StairCaseHelper.getFacingWithRotation(direction, Rotation.CLOCKWISE_90)), null));
		}
	}

	private static void buildPillar(BlockPos bottom, BlockDungeonPart.Builder partBuilder) {
		for (int iY = 1; iY <= 4; iY++) {
			BlockPos pos = bottom.offset(0, iY, 0);
			partBuilder.add(new PreparableBlockInfo(pos, CQRBlocks.GRANITE_PILLAR.defaultBlockState().setValue(RotatedPillarBlock.AXIS, Direction.Axis.Y), null));
		}
		partBuilder.add(new PreparableBlockInfo(bottom.offset(0, 5, 0), CQRBlocks.GRANITE_CARVED.defaultBlockState(), null));
	}

	private static void buildFloorAndCeiling(BlockPos start, BlockPos end, int ceilingHeight, BlockDungeonPart.Builder partBuilder) {
		BlockPos endP = new BlockPos(end.getX(), start.getY(), end.getZ());

		// Floor
		for (BlockPos p : BlockPos.betweenClosed(start, endP)) {
			partBuilder.add(new PreparableBlockInfo(p, CQRBlocks.GRANITE_SMALL.defaultBlockState(), null));
		}

		// Ceiling
		for (BlockPos p : BlockPos.betweenClosed(start.offset(0, ceilingHeight + 1, 0), endP.offset(0, ceilingHeight + 1, 0))) {
			partBuilder.add(new PreparableBlockInfo(p, CQRBlocks.GRANITE_SQUARE.defaultBlockState(), null));
		}
	}

}
