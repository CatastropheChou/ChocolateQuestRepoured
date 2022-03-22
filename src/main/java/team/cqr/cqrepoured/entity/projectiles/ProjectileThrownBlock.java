package team.cqr.cqrepoured.entity.projectiles;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.entity.PartEntity;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import team.cqr.cqrepoured.config.CQRConfig;

public class ProjectileThrownBlock extends ProjectileBase implements IEntityAdditionalSpawnData {

	private ResourceLocation block = Blocks.END_STONE.getRegistryName();
	private BlockState state = null;
	private boolean placeOnImpact = false;

	public ProjectileThrownBlock(World worldIn) {
		super(worldIn);
		this.setSize(1, 1);
	}

	private ProjectileThrownBlock(World worldIn, double x, double y, double z) {
		super(worldIn, x, y, z);
		this.setSize(1, 1);
	}

	public ProjectileThrownBlock(World worldIn, LivingEntity shooter, BlockState block, boolean placeOnImpact) {
		super(worldIn, shooter);
		this.block = block.getBlock().getRegistryName();
		this.placeOnImpact = placeOnImpact;
		this.state = block;
		this.setSize(1, 1);
	}

	@Override
	public void writeSpawnData(ByteBuf buffer) {
		ByteBufUtils.writeUTF8String(buffer, this.block.toString());
	}

	@Override
	public void readSpawnData(ByteBuf additionalData) {
		this.block = new ResourceLocation(ByteBufUtils.readUTF8String(additionalData));
		this.state = Block.REGISTRY.getObject(this.block).getDefaultState();
	}

	public BlockState getBlock() {
		return this.state != null ? this.state : Blocks.BEDROCK.getDefaultState();
	}

	@Override
	public boolean isNoGravity() {
		return false;
	}

	@Override
	protected void onHit(RayTraceResult result) {
		if (this.world.isRemote) {
			return;
		}

		if (result.typeOfHit == Type.ENTITY) {
			if (result.entityHit == this.thrower) {
				return;
			}

			if (result.entityHit instanceof PartEntity && ((PartEntity) result.entityHit).parent == this.thrower) {
				return;
			}

			result.entityHit.attackEntityFrom(DamageSource.causeIndirectDamage(this, this.thrower), 10);
			this.setDead();
			return;
		}
		if (CQRConfig.bosses.thrownBlocksGetPlaced && this.placeOnImpact) {
			// TODO: Add placed block to whitelist of protected region
			this.world.setBlockState(new BlockPos(result.hitVec.x, result.hitVec.y, result.hitVec.z), this.state);
			// this.world.createExplosion(this.thrower, this.posX, this.posY, this.posZ, 1.5F, false);
			if (this.world instanceof ServerWorld) {
				ServerWorld ws = (ServerWorld) this.world;
				Vector3d pos = result.hitVec;
				double particleSpeed = 0.2D;
				for (int i = 0; i < 50; i++) {
					double dx = -0.5 + this.rand.nextDouble();
					dx *= particleSpeed;
					double dy = -0.5 + this.rand.nextDouble();
					dy *= particleSpeed;
					double dz = -0.5 + this.rand.nextDouble();
					dz *= particleSpeed;
					ws.spawnParticle(ParticleTypes.BLOCK_CRACK, pos.x, pos.y, pos.z, dx, dy, dz, Block.getStateId(this.state));
					this.playSound(this.state.getBlock().getSoundType(this.state, this.world, this.getPosition(), this).getPlaceSound(), 1.5F, 1.25F);
				}
			}
		}

		this.setDead();
	}

	@Override
	public void writeEntityToNBT(CompoundNBT compound) {
		CompoundNBT blockstate = new CompoundNBT();
		NBTUtil.writeBlockState(blockstate, this.state);
		compound.setTag("blockdata", blockstate);
		super.save(compound);
	}

	@Override
	public void readEntityFromNBT(CompoundNBT compound) {
		try {
			CompoundNBT blockstate = compound.getCompoundTag("blockdata");
			this.state = NBTUtil.readBlockState(blockstate);
		} catch (Exception ex) {
			// Ignore
			this.state = Blocks.END_STONE.getDefaultState();
		}
		super.readEntityFromNBT(compound);
	}

}
