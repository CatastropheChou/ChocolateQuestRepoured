package team.cqr.cqrepoured.entity.pathfinding;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.pathfinding.*;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.Region;
import net.minecraft.world.World;
import team.cqr.cqrepoured.entity.ai.EntityAIOpenCloseDoor;
import team.cqr.cqrepoured.world.ChunkCacheCQR;

import javax.annotation.Nullable;
import java.util.EnumSet;

/**
 * Copied from {@link GroundPathNavigator}
 */
public class PathNavigateGroundCQR extends GroundPathNavigator {

	private int ticksAtLastPos;
	private Vector3d lastPosCheck = Vector3d.ZERO;
	private Vector3d timeoutCachedNode = Vector3d.ZERO;
	private long timeoutTimer;
	@SuppressWarnings("unused")
	private long lastTimeoutCheck;
	private double timeoutLimit;
	private long lastTimeUpdated;
	private BlockPos targetPos;
	private PathFinder pathFinder;

	public PathNavigateGroundCQR(MobEntity entitylivingIn, World worldIn) {
		super(entitylivingIn, worldIn);
	}

	@Override
	protected PathFinder getPathFinder() {
		this.nodeProcessor = new WalkNodeProcessor() {

			@Override
			public PathNodeType getPathNodeType(IBlockAccess p_193577_1_, int x, int y, int z, int xSize, int ySize, int zSize, boolean canOpenDoorsIn, boolean canEnterDoorsIn, EnumSet<PathNodeType> p_193577_10_, PathNodeType p_193577_11_, BlockPos p_193577_12_) {
				for (int i = 0; i < xSize; ++i) {
					for (int j = 0; j < ySize; ++j) {
						for (int k = 0; k < zSize; ++k) {
							int l = i + x;
							int i1 = j + y;
							int j1 = k + z;
							PathNodeType pathnodetype = this.getPathNodeType(p_193577_1_, l, i1, j1);

							if (pathnodetype == PathNodeType.DOOR_WOOD_CLOSED && canOpenDoorsIn && canEnterDoorsIn) {
								pathnodetype = PathNodeType.WALKABLE;
							}

							// TODO better method for calculating the facing from which the door will be entered
							if (pathnodetype == PathNodeType.DOOR_IRON_CLOSED && canOpenDoorsIn && canEnterDoorsIn
									&& EntityAIOpenCloseDoor.canMoveThroughDoor(p_193577_1_, new BlockPos(l, i1, j1), Direction.getFacingFromVector(l - p_193577_12_.getX(), i1 - p_193577_12_.getY(), j1 - p_193577_12_.getZ()).getOpposite(), true)) {
								pathnodetype = PathNodeType.WALKABLE;
							}

							if (pathnodetype == PathNodeType.DOOR_OPEN && !canEnterDoorsIn) {
								pathnodetype = PathNodeType.BLOCKED;
							}

							if (pathnodetype == PathNodeType.RAIL && !(p_193577_1_.getBlockState(p_193577_12_).getBlock() instanceof AbstractRailBlock) && !(p_193577_1_.getBlockState(p_193577_12_.down()).getBlock() instanceof AbstractRailBlock)) {
								pathnodetype = PathNodeType.FENCE;
							}

							if (i == 0 && j == 0 && k == 0) {
								p_193577_11_ = pathnodetype;
							}

							p_193577_10_.add(pathnodetype);
						}
					}
				}

				return p_193577_11_;
			}

			@Override
			protected PathNodeType getPathNodeTypeRaw(IBlockAccess p_189553_1_, int p_189553_2_, int p_189553_3_, int p_189553_4_) {
				BlockPos blockpos = new BlockPos(p_189553_2_, p_189553_3_, p_189553_4_);
				BlockState iblockstate = p_189553_1_.getBlockState(blockpos);
				Block block = iblockstate.getBlock();
				Material material = iblockstate.getMaterial();

				PathNodeType type = block.getAiPathNodeType(iblockstate, p_189553_1_, blockpos, this.currentEntity);
				if (type != null) {
					return type;
				}

				if (material == Material.AIR) {
					return PathNodeType.OPEN;
				} else if (block != Blocks.TRAPDOOR && block != Blocks.IRON_TRAPDOOR && block != Blocks.WATERLILY) {
					if (block == Blocks.FIRE) {
						return PathNodeType.DAMAGE_FIRE;
					} else if (block == Blocks.CACTUS) {
						return PathNodeType.DAMAGE_CACTUS;
					} else if (block instanceof DoorBlock && material == Material.WOOD && !iblockstate.getActualState(p_189553_1_, blockpos).getValue(DoorBlock.OPEN)) {
						return PathNodeType.DOOR_WOOD_CLOSED;
					} else if (block instanceof DoorBlock && material == Material.IRON && !iblockstate.getActualState(p_189553_1_, blockpos).getValue(DoorBlock.OPEN)) {
						return PathNodeType.DOOR_IRON_CLOSED;
					} else if (block instanceof DoorBlock && iblockstate.getActualState(p_189553_1_, blockpos).getValue(DoorBlock.OPEN)) {
						return PathNodeType.DOOR_OPEN;
					} else if (block instanceof AbstractRailBlock) {
						return PathNodeType.RAIL;
					} else if (!(block instanceof FenceBlock) && !(block instanceof WallBlock) && (!(block instanceof FenceGateBlock) || iblockstate.getValue(FenceGateBlock.OPEN).booleanValue())) {
						if (material == Material.WATER) {
							return PathNodeType.WATER;
						} else if (material == Material.LAVA) {
							return PathNodeType.LAVA;
						} else {
							return block.isPassable(p_189553_1_, blockpos) ? PathNodeType.OPEN : PathNodeType.BLOCKED;
						}
					} else {
						return PathNodeType.FENCE;
					}
				} else {
					return PathNodeType.TRAPDOOR;
				}
			}

		};
		this.nodeProcessor.setCanEnterDoors(true);
		this.pathFinder = new PathFinder(this.nodeProcessor);
		return this.pathFinder;
	}

	@Override
	public float getPathSearchRange() {
		return 256.0F;
	}

	@Override
	public void updatePath() {
		if (this.hasMount()) {
			this.getMount().getNavigator().updatePath();
		}
		if (this.world.getTotalWorldTime() - this.lastTimeUpdated > 20L) {
			if (this.targetPos != null) {
				this.currentPath = null;
				this.currentPath = this.getPathToPos(this.targetPos);
				this.lastTimeUpdated = this.world.getTotalWorldTime();
				this.tryUpdatePath = false;
			}
		} else {
			this.tryUpdatePath = true;
		}
	}

	@Override
	public void onUpdateNavigation() {
		super.onUpdateNavigation();
		if (!this.noPath() && this.hasMount()) {
			this.getMount().getNavigator().onUpdateNavigation();
		}
	}

	private boolean hasMount() {
		return this.entity.getRidingEntity() instanceof MobEntity;
	}

	@Override
	public Path getPathToPos(BlockPos pos) {
		if (this.world.getBlockState(pos).getMaterial() == Material.AIR) {
			BlockPos blockpos;

			for (blockpos = pos.down(); blockpos.getY() > 0 && this.world.getBlockState(blockpos).getMaterial() == Material.AIR; blockpos = blockpos.down()) {

			}

			if (blockpos.getY() > 0) {
				return this.getPathToPosCQR(blockpos.up());
			}

			while (blockpos.getY() < this.world.getHeight() && this.world.getBlockState(blockpos).getMaterial() == Material.AIR) {
				blockpos = blockpos.up();
			}

			pos = blockpos;
		}

		if (!this.world.getBlockState(pos).getMaterial().isSolid()) {
			return this.getPathToPosCQR(pos);
		} else {
			BlockPos blockpos1;

			for (blockpos1 = pos.up(); blockpos1.getY() < this.world.getHeight() && this.world.getBlockState(blockpos1).getMaterial().isSolid(); blockpos1 = blockpos1.up()) {

			}

			return this.getPathToPosCQR(blockpos1);
		}
	}

	@Nullable
	private Path getPathToPosCQR(BlockPos pos) {
		if (!this.canNavigate()) {
			return null;
		} else if (this.currentPath != null && !this.currentPath.isFinished() && pos.equals(this.targetPos)) {
			return this.currentPath;
		} else {
			Entity ent = this.hasMount() ? this.getMount() : this.entity;
			float distance = MathHelper.sqrt(ent.getDistanceSqToCenter(pos));
			if (distance > this.getPathSearchRange()) {
				return null;
			}

			this.world.profiler.startSection("pathfind");
			BlockPos entityPos = new BlockPos(this.hasMount() ? this.getMount() : this.entity);
			Region chunkcache = new ChunkCacheCQR(this.world, entityPos, pos, entityPos, 32, false);
			Path path = this.pathFinder.findPath(chunkcache, this.hasMount() ? this.getMount() : this.entity, pos, MathHelper.ceil(distance + 32.0F));
			this.world.profiler.endSection();
			return path;
		}
	}

	@Override
	public boolean setPath(Path pathentityIn, double speedIn) {
		if (pathentityIn == null) {
			this.currentPath = null;
			this.targetPos = null;
			return false;
		} else {

			if (this.hasMount()) {
				this.getMount().getNavigator().setPath(pathentityIn, speedIn);
			}

			if (pathentityIn.isSamePath(this.currentPath)) {
				return true;
			}

			this.currentPath = pathentityIn;

			this.removeSunnyPath();

			if (this.currentPath.getCurrentPathLength() <= 0) {
				this.currentPath = null;
				this.targetPos = null;
				return false;
			} else {
				PathPoint finalPathPoint = pathentityIn.getFinalPathPoint();
				this.targetPos = new BlockPos(finalPathPoint.x, finalPathPoint.y, finalPathPoint.z);
				this.speed = speedIn;
				this.ticksAtLastPos = this.totalTicks;
				this.lastPosCheck = this.getEntityPosition();
				return true;
			}
		}
	}

	@Override
	protected boolean canNavigate() {
		return super.canNavigate() || this.hasMount();
	}

	@Override
	protected void checkForStuck(Vector3d positionVec3) {
		if (this.totalTicks - this.ticksAtLastPos >= 100) {
			double aiMoveSpeed = this.hasMount() ? this.getMount().getAIMoveSpeed() : this.entity.getAIMoveSpeed();
			aiMoveSpeed = aiMoveSpeed * aiMoveSpeed * 0.98D / 0.454D;
			if (positionVec3.distanceTo(this.lastPosCheck) / 100.0D < aiMoveSpeed * 0.5D) {
				this.clearPath();
			}

			this.ticksAtLastPos = this.totalTicks;
			this.lastPosCheck = positionVec3;
		}

		if (this.currentPath != null && !this.currentPath.isFinished()) {
			Vector3d vec3d = this.currentPath.getCurrentPos();

			if (!vec3d.equals(this.timeoutCachedNode)) {
				this.timeoutCachedNode = vec3d;
				this.timeoutTimer = this.totalTicks;
				double aiMoveSpeedOrig = this.hasMount() ? this.getMount().getAIMoveSpeed() : this.entity.getAIMoveSpeed();
				double aiMoveSpeed = aiMoveSpeedOrig;
				if (aiMoveSpeed > 0.0F) {
					aiMoveSpeed = aiMoveSpeed * aiMoveSpeed * 0.98D / 0.454D;
					double distance = positionVec3.distanceTo(this.timeoutCachedNode);
					this.timeoutLimit = aiMoveSpeedOrig > 0.0F ? MathHelper.ceil(distance / aiMoveSpeed) : 0.0D;
				} else {
					this.timeoutLimit = 0.0D;
				}
			}

			if (this.timeoutLimit > 0.0D && this.totalTicks - this.timeoutTimer > this.timeoutLimit * 2.0D) {
				this.timeoutCachedNode = Vector3d.ZERO;
				this.timeoutTimer = 0L;
				this.timeoutLimit = 0.0D;
				this.clearPath();
			}
		}
	}

	@Nullable
	private MobEntity getMount() {
		try {
			return (MobEntity) this.entity.getRidingEntity();
		} catch (NullPointerException npe) {
			return null;
		}
	}

	@Override
	public void clearPath() {
		if (this.hasMount()) {
			this.getMount().getNavigator().clearPath();
		}
		this.currentPath = null;
		this.targetPos = null;
		super.clearPath();
	}

}
