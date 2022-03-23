package team.cqr.cqrepoured.entity.boss.spectrelord;

import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import team.cqr.cqrepoured.entity.ai.boss.spectrelord.*;
import team.cqr.cqrepoured.entity.ai.target.TargetUtil;
import team.cqr.cqrepoured.entity.bases.AbstractEntityCQRBoss;
import team.cqr.cqrepoured.entity.bases.ISummoner;
import team.cqr.cqrepoured.faction.EDefaultFaction;
import team.cqr.cqrepoured.faction.Faction;
import team.cqr.cqrepoured.init.CQRLoottables;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class EntityCQRSpectreLord extends AbstractEntityCQRBoss implements ISummoner {

	private static final DataParameter<Integer> SWORD_SHIELD_ACTIVE = EntityDataManager.<Integer>createKey(EntityCQRSpectreLord.class, DataSerializers.VARINT);
	private static final DataParameter<Boolean> CHANNELING_LASER = EntityDataManager.<Boolean>createKey(EntityCQRSpectreLord.class, DataSerializers.BOOLEAN);

	private final List<Entity> summonedEntities = new ArrayList<>();

	public EntityCQRSpectreLord(World world) {
		super(world);
	}

	@Override
	public CreatureAttribute getCreatureAttribute() {
		return CreatureAttribute.UNDEAD;
	}

	@Override
	protected ResourceLocation getLootTable() {
		return CQRLoottables.ENTITIES_SPECTRE_LORD;
	}

	@Override
	public float getBaseHealth() {
		return 200.0F;
	}

	@Override
	public EDefaultFaction getDefaultFaction() {
		return EDefaultFaction.UNDEAD;
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		// this.dataManager.register(INVISIBILITY, 0.0F);
		this.dataManager.register(SWORD_SHIELD_ACTIVE, 0);
		this.dataManager.register(CHANNELING_LASER, false);
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(Attributes.ARMOR).setBaseValue(16.0D);
		this.getEntityAttribute(Attributes.ARMOR_TOUGHNESS).setBaseValue(8.0D);
		this.getEntityAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0D);
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.spellHandler.addSpell(0, new EntityAISpectreLordChannelHate(this, 600, 60, 480));
		this.spellHandler.addSpell(1, new EntityAISpectreLordDash(this, 200, 40, 3, 3.0D, 1.5D));
		this.spellHandler.addSpell(2, new EntityAISpectreLordLaser(this, 600, 60, 320));
		this.spellHandler.addSpell(3, new EntityAISpectreLordSummonIllusions(this, 400, 40, 4, 800));
		this.spellHandler.addSpell(4, new EntityAISpectreLordSwordShield(this, 600, 60));
	}

	@Override
	public void baseTick() {
		if (!this.world.isRemote && this.fallDistance > 3.0F) {
			this.fallDistance = 0.0F;
			if (this.hasAttackTarget()) {
				Vector3d vec = TargetUtil.getPositionNearTarget(this.world, this, this.getAttackTarget(), 2.0D, 8.0D, 2.0D);
				if (vec != null) {
					this.teleport(vec.x, vec.y, vec.z);
				}
			} else if (this.hasHomePositionCQR()) {
				BlockPos pos = this.getHomePositionCQR();
				this.teleport(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D);
			} else {
				Vector3d vec = TargetUtil.getPositionNearTarget(this.world, this, this, 0.0D, 16.0D, 8.0D);
				if (vec != null) {
					this.teleport(vec.x, vec.y, vec.z);
				}
			}
		}

		super.baseTick();

		for (Iterator<Entity> iterator = this.summonedEntities.iterator(); iterator.hasNext();) {
			Entity e = iterator.next();
			if (!e.isEntityAlive()) {
				iterator.remove();
			}
		}

		if (!this.world.isRemote) {
			if (this.dataManager.get(SWORD_SHIELD_ACTIVE) > 0) {
				this.dataManager.set(SWORD_SHIELD_ACTIVE, this.dataManager.get(SWORD_SHIELD_ACTIVE) - 1);
			}
		}
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		if (this.getInvisibility() == 1.0F) {
			return false;
		}
		if (this.isChannelingLaser()) {
			amount *= 2.0F;
		}
		if (this.isSwordShieldActive()) {
			if (source.isProjectile()) {
				return false;
			}
			if (source.getImmediateSource() != null) {
				boolean flag = super.attackEntityFrom(source, amount);
				if (flag) {
					source.getImmediateSource().attackEntityFrom(new DamageSource("thorns").setDamageBypassesArmor(), amount * 0.25F);
				}
				return flag;
			}
		}
		return super.attackEntityFrom(source, amount);
	}

	@Override
	public float getInvisibility() {
		return this.dataManager.get(INVISIBILITY);
	}

	public void setSwordShieldActive(int ticks) {
		this.dataManager.set(SWORD_SHIELD_ACTIVE, ticks);
	}

	public boolean isSwordShieldActive() {
		return this.dataManager.get(SWORD_SHIELD_ACTIVE) > 0;
	}

	public void setChannelingLaser(boolean channelingLaser) {
		this.dataManager.set(CHANNELING_LASER, channelingLaser);
	}

	public boolean isChannelingLaser() {
		return this.dataManager.get(CHANNELING_LASER);
	}

	@Override
	public void teleport(double x, double y, double z) {
		double oldX = this.posX;
		double oldY = this.posY;
		double oldZ = this.posZ;
		super.teleport(x, y, z);
		this.playSound(SoundEvents.ENTITY_SHULKER_TELEPORT, 1.0F, 0.9F + this.rand.nextFloat() * 0.2F);
		((ServerWorld) this.world).spawnParticle(ParticleTypes.PORTAL, oldX, oldY + this.height * 0.5D, oldZ, 4, 0.2D, 0.2D, 0.2D, 0.0D);
		((ServerWorld) this.world).spawnParticle(ParticleTypes.PORTAL, x, y + this.height * 0.5D, z, 4, 0.2D, 0.2D, 0.2D, 0.0D);
	}

	@Override
	public Faction getSummonerFaction() {
		return this.getFaction();
	}

	@Override
	public List<Entity> getSummonedEntities() {
		return this.summonedEntities;
	}

	@Override
	public LivingEntity getSummoner() {
		return this;
	}

	@Override
	public void addSummonedEntityToList(Entity summoned) {
		this.summonedEntities.add(summoned);
	}

}
