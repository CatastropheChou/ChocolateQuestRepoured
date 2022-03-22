package team.cqr.cqrepoured.entity.projectiles;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import java.util.List;

public class ProjectileEarthQuake extends ThrowableEntity {
	private int lifeTime = 60;
	@SuppressWarnings("unused")
	private LivingEntity thrower;
	private double throwY = 0.3D;

	public void setThrowHeight(double amount) {
		this.throwY = amount;
	}

	public ProjectileEarthQuake(World worldIn) {
		super(worldIn);
	}

	public ProjectileEarthQuake(World worldIn, double x, double y, double z) {
		super(worldIn, x, y, z);
	}

	public ProjectileEarthQuake(World worldIn, LivingEntity throwerIn) {
		super(worldIn, throwerIn);
		this.thrower = throwerIn;

		this.posY -= 1.2D;
		this.motionX = 0.1D;
		this.motionY = -2.0D;
		this.motionZ = 0.1D;
		this.isImmuneToFire = true;
	}

	@Override
	protected void onImpact(RayTraceResult result) {
		if (!this.world.isRemote) {
			if (!(result.entityHit instanceof MobEntity)) {
				this.motionY = 0.0D;
			}
		}
	}

	@Override
	public void onUpdate() {
		this.motionX *= 1.01D;
		this.motionY *= 1.01D;
		this.motionZ *= 1.01D;

		if (this.getThrower() != null && this.getThrower().isDead) {
			this.setDead();
		}

		else {
			if (this.ticksExisted++ > 300) {
				this.setDead();
			}

			this.onUpdateInAir();
			super.tick();
		}
	}

	private void onUpdateInAir() {
		this.lifeTime -= 1;

		if (this.lifeTime <= 0) {
			this.setDead();
		}

		BlockPos pos = new BlockPos(this.getPosition().getX(), this.getPosition().getY() - 1, this.getPosition().getZ());
		BlockState iblockstate = this.world.getBlockState(pos);

		if (iblockstate.getBlock() == null || iblockstate.getBlock().isAir(iblockstate, this.world, pos)) {
			iblockstate = Blocks.GLASS.getDefaultState();
		}

		double dist = 1.0D;
		AxisAlignedBB var3 = this.getEntityBoundingBox().expand(dist, 2.0D, dist);
		List<Entity> list = this.world.getEntitiesWithinAABB(LivingEntity.class, var3);

		for (Entity entity : list) {
			if (entity instanceof LivingEntity && entity != this.getThrower() && !this.world.isRemote && entity.onGround) {
				entity.motionY += this.getEntityThrowDistance();
				entity.attackEntityFrom(DamageSource.causeIndirectMagicDamage(this, this.getThrower()), 1.0F);
			}
		}

		if (this.world.isRemote) {
			for (int i = 0; i < 10; i++) {
				this.world.spawnParticle(ParticleTypes.BLOCK_CRACK, this.posX + this.rand.nextFloat() - 0.5D, this.posY + this.rand.nextFloat() - 0.5D, this.posZ + this.rand.nextFloat() - 0.5D, this.rand.nextFloat() - 0.5F, this.rand.nextFloat(), this.rand.nextFloat() - 0.5F, Block.getStateId(iblockstate));
			}
		}
	}

	public double getEntityThrowDistance() {
		return this.throwY;
	}
}
