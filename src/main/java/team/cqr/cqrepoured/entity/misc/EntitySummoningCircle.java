package team.cqr.cqrepoured.entity.misc;

import java.util.Arrays;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.network.NetworkHooks;
import team.cqr.cqrepoured.entity.IDontRenderFire;
import team.cqr.cqrepoured.entity.ai.target.TargetUtil;
import team.cqr.cqrepoured.entity.bases.AbstractEntityCQR;
import team.cqr.cqrepoured.entity.bases.ISummoner;
import team.cqr.cqrepoured.faction.Faction;
import team.cqr.cqrepoured.faction.FactionRegistry;
import team.cqr.cqrepoured.init.CQREntityTypes;

public class EntitySummoningCircle extends Entity implements IEntityAdditionalSpawnData, IDontRenderFire {

	protected static final int BORDER_WHEN_TO_SPAWN_IN_TICKS = 60;

	protected ResourceLocation entityToSpawn;
	protected float timeMultiplierForSummon;
	protected ECircleTexture texture;
	protected ISummoner summoner;
	protected LivingEntity summonerLiving;
	protected Vector3d velForSummon = null;

	public enum ECircleTexture {
		ZOMBIE(), SKELETON(), FLYING_SKULL(), FLYING_SWORD(), METEOR();

		static {
			values();
		}
	}

	public EntitySummoningCircle(EntityType<? extends EntitySummoningCircle> type, World worldIn) {
		this(type, worldIn, new ResourceLocation("minecraft", "zombie"), 1F, ECircleTexture.ZOMBIE, null);
	}
	
	public EntitySummoningCircle(World world) {
		this(CQREntityTypes.SUMMONING_CIRCLE.get(), world);
	}

	public EntitySummoningCircle(EntityType<? extends EntitySummoningCircle> type, World worldIn, ResourceLocation entityToSpawn, float timeMultiplier, ECircleTexture texture, ISummoner summoner) {
		this(type, worldIn, entityToSpawn, timeMultiplier, texture, summoner, null);
	}
	
	public EntitySummoningCircle(World worldIn, ResourceLocation entityToSpawn, float timeMultiplier, ECircleTexture texture, ISummoner summoner) {
		this(CQREntityTypes.SUMMONING_CIRCLE.get(), worldIn, entityToSpawn, timeMultiplier, texture, summoner);
	}

	public EntitySummoningCircle(EntityType<? extends EntitySummoningCircle> type, World worldIn, ResourceLocation entityToSpawn, float timeMultiplier, ECircleTexture texture, ISummoner isummoner, LivingEntity summoner) {
		super(type, worldIn);
		this.entityToSpawn = entityToSpawn;
		this.timeMultiplierForSummon = timeMultiplier;
		this.texture = texture;
		this.summoner = isummoner;
		this.summonerLiving = summoner;
	}
	
	public EntitySummoningCircle(World worldIn, ResourceLocation entityToSpawn, float timeMultiplier, ECircleTexture texture, ISummoner isummoner, LivingEntity summoner) {
		this(CQREntityTypes.SUMMONING_CIRCLE.get(), worldIn, entityToSpawn, timeMultiplier, texture, isummoner, summoner);
	}

	@Override
	protected void defineSynchedData() {

	}

	@Override
	public void tick() {
		super.tick();

		if (!this.level.isClientSide) {
			if (this.tickCount >= EntitySummoningCircle.BORDER_WHEN_TO_SPAWN_IN_TICKS * this.timeMultiplierForSummon) {
				Entity summon = EntityList.createEntityByIDFromName(this.entityToSpawn, this.level);

				if (summon != null) {
					//summon.setPosition(this.posX, this.posY + 0.5D, this.posZ);
					summon.setPos(this.position().x, this.position().y + 0.5D, this.position().z);

					if (this.velForSummon != null) {
						/*summon.motionX = this.velForSummon.x;
						summon.motionY = this.velForSummon.y;
						summon.motionZ = this.velForSummon.z;
						summon.velocityChanged = true;*/
						summon.setDeltaMovement(this.velForSummon);
						summon.hasImpulse = true;
					}

					if (this.summonerLiving instanceof PlayerEntity && summon instanceof AbstractEntityCQR) {
						Arrays.stream(EquipmentSlotType.values()).forEach(slot -> ((AbstractEntityCQR) summon).setDropChance(slot, 1.01F));
					}

					this.level.addFreshEntity(summon);

					if (this.summonerLiving != null && summon instanceof AbstractEntityCQR) {
						((AbstractEntityCQR) summon).setLeader(this.summonerLiving);
						Faction faction = FactionRegistry.instance(this).getFactionOf(this.summonerLiving);
						if (faction != null) {
							((AbstractEntityCQR) summon).setFaction(faction.getName());
						}
					}

					if (this.summoner != null && !this.summoner.getSummoner().isDeadOrDying()) {
						this.summoner.setSummonedEntityFaction(summon);
						this.summoner.tryEquipSummon(summon, this.level.random);
						this.summoner.addSummonedEntityToList(summon);
					}
				}

				this.remove();
			}
		} else {
			if (this.tickCount >= EntitySummoningCircle.BORDER_WHEN_TO_SPAWN_IN_TICKS * this.timeMultiplierForSummon * 0.8F) {
				for (int i = 0; i < 4; i++) {
					this.level.addParticle(ParticleTypes.WITCH, this.position().x, this.position().y + 0.02D, this.position().z, this.random.nextDouble(), this.random.nextDouble(), this.random.nextDouble());
				}

				if (!this.level.isClientSide) {
					Faction faction = this.summoner != null ? this.summoner.getSummonerFaction() : null;
					for (Entity ent : this.level.getEntities(this, new AxisAlignedBB(this.position().add(this.getBbWidth() / 2, 0, this.getBbWidth() / 2), this.position().add(-this.getBbWidth() / 2, 3, -this.getBbWidth() / 2)), faction != null ? TargetUtil.createPredicateNonAlly(faction) : TargetUtil.PREDICATE_LIVING)) {
						if (ent != null && ent.isAlive() && ent instanceof LivingEntity) {
							((LivingEntity) ent).addEffect(new EffectInstance(Effects.WITHER, 80, 0));
						}
					}
				}
				// this.world.spawnParticle(EnumParticleTypes.SPELL_WITCH, this.posX, this.posY + 0.02D, this.posZ, 1.0F, 0.0F, 0.0F,
				// 20);
				// this.world.spawnParticle(EnumParticleTypes.SPELL_WITCH, this.posX, this.posY + 0.02D, this.posZ, 0.5F, 0.0F, 0.5F,
				// 1);
				// this.world.spawnParticle(EnumParticleTypes.SPELL_WITCH, this.posX, this.posY + 0.02D, this.posZ, 0.5F, 0.0F, -0.5F,
				// 1);
				// this.world.spawnParticle(EnumParticleTypes.SPELL_WITCH, this.posX, this.posY + 0.02D, this.posZ, -0.5F, 0.0F, 0.5F,
				// 1);
				// this.world.spawnParticle(EnumParticleTypes.SPELL_WITCH, this.posX, this.posY + 0.02D, this.posZ, -0.5F, 0.0F, -0.5F,
				// 1);
			}
		}
	}

	@Override
	public void readAdditionalSaveData(CompoundNBT compound) {
		this.timeMultiplierForSummon = compound.getFloat("cqrdata.timeMultiplier");
		String resD = compound.getString("cqrdata.entityToSpawn.Domain");
		String resP = compound.getString("cqrdata.entityToSpawn.Path");
		this.entityToSpawn = new ResourceLocation(resD, resP);
		this.tickCount = compound.getInt("ticksExisted");
	}

	@Override
	public void addAdditionalSaveData(CompoundNBT compound) {
		compound.putFloat("cqrdata.timeMultiplier", this.timeMultiplierForSummon);
		compound.putString("cqrdata.entityToSpawn.Domain", this.entityToSpawn.getNamespace());
		compound.putString("cqrdata.entityToSpawn.Path", this.entityToSpawn.getPath());
		compound.putInt("ticksExisted", this.tickCount);
	}

	@Override
	public void writeSpawnData(PacketBuffer buffer) {
		buffer.writeByte(this.texture.ordinal());
		buffer.writeFloat(this.timeMultiplierForSummon);
		buffer.writeInt(this.tickCount);
	}

	@Override
	public void readSpawnData(PacketBuffer additionalData) {
		this.texture = ECircleTexture.values()[additionalData.readByte()];
		this.timeMultiplierForSummon = additionalData.readFloat();
		this.tickCount = additionalData.readInt();
	}

	@OnlyIn(Dist.CLIENT)
	public int getTextureID() {
		return this.texture.ordinal();
	}

	public void setVelocityForSummon(Vector3d v) {
		this.velForSummon = v;
	}

	public void setSummon(ResourceLocation summon) {
		if (summon != null) {
			this.entityToSpawn = summon;
		}
	}

	@Override
	public IPacket<?> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

}
