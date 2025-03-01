package team.cqr.cqrepoured.entity.boss;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;
import team.cqr.cqrepoured.CQRMain;
import team.cqr.cqrepoured.config.CQRConfig;
import team.cqr.cqrepoured.entity.IAnimatableCQR;
import team.cqr.cqrepoured.entity.ai.spells.EntityAIBlindTargetSpell;
import team.cqr.cqrepoured.entity.ai.spells.EntityAIFangAttack;
import team.cqr.cqrepoured.entity.ai.spells.EntityAISummonMinionSpell;
import team.cqr.cqrepoured.entity.bases.AbstractEntityCQR;
import team.cqr.cqrepoured.entity.bases.ISummoner;
import team.cqr.cqrepoured.entity.misc.EntityFlyingSkullMinion;
import team.cqr.cqrepoured.entity.misc.EntitySummoningCircle.ECircleTexture;
import team.cqr.cqrepoured.faction.EDefaultFaction;
import team.cqr.cqrepoured.faction.Faction;
import team.cqr.cqrepoured.init.CQREntityTypes;

public class EntityCQRNecromancer extends AbstractEntityCQRMageBase implements ISummoner, IAnimatableCQR {

	private static final DataParameter<Boolean> BONE_SHIELD_ACTIVE = EntityDataManager.<Boolean>defineId(EntityCQRNecromancer.class, DataSerializers.BOOLEAN);

	protected List<Entity> summonedMinions = new ArrayList<>();
	protected List<EntityFlyingSkullMinion> summonedSkulls = new ArrayList<>();

	public EntityCQRNecromancer(World world) {
		this(CQREntityTypes.NECROMANCER.get(), world);
	}
	
	public EntityCQRNecromancer(EntityType<? extends AbstractEntityCQR> type, World worldIn) {
		super(type, worldIn);
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.spellHandler.addSpell(0, new EntityAISummonMinionSpell(this, 30, 10, new ResourceLocation(CQRMain.MODID, "skeleton"), ECircleTexture.SKELETON, true, 25, 5, new Vector3d(0, 0, 0)) {
			@Override
			public boolean isInterruptible() {
				return false;
			}
		});
		this.spellHandler.addSpell(1, new EntityAISummonMinionSpell(this, 20, 5, new ResourceLocation(CQRMain.MODID, "flying_skull"), ECircleTexture.FLYING_SKULL, false, 8, 4, new Vector3d(0, 2.5, 0)) {
			@Override
			public boolean isInterruptible() {
				return false;
			}
		});
		this.spellHandler.addSpell(2, new EntityAIBlindTargetSpell(this, 45, 10, 100));
		this.spellHandler.addSpell(3, new EntityAIFangAttack(this, 20, 10, 4, 8) {
			@Override
			public boolean isInterruptible() {
				return false;
			}
		});
		// this.spellHandler.addSpell(4, new EntityAIVampiricSpell(this, 30, 10));
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(BONE_SHIELD_ACTIVE, false);
	}

	@Override
	public void aiStep() {
		super.aiStep();
		this.filterSummonLists();

		if (!this.summonedSkulls.isEmpty() && !this.hasAttackTarget()) {
			this.summonedSkulls.get(0).setSide(false);
			if (this.summonedSkulls.size() >= 2) {
				this.summonedSkulls.get(1).setSide(true);
			}
		}

		if (!this.level.isClientSide && this.getHealth() <= this.getMaxHealth() / 2) {
			this.entityData.set(BONE_SHIELD_ACTIVE, true);
		} else if (!this.level.isClientSide) {
			this.entityData.set(BONE_SHIELD_ACTIVE, false);
		}

		if (this.getTarget() != null && this.getTarget().isAlive() && !this.summonedSkulls.isEmpty()) {
			for (int i = 0; i < this.summonedSkulls.size(); i++) {
				EntityFlyingSkullMinion skull = this.summonedSkulls.get(i);
				if (!skull.hasTarget()) {
					skull.setTarget(this.getTarget());
				}
			}
			for (int i = 0; i < this.summonedSkulls.size(); i++) {
				EntityFlyingSkullMinion skull = this.summonedSkulls.get(i);
				if (!skull.isAttacking()) {
					skull.startAttacking();
				}
			}
		}
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		if (!this.level.isClientSide && this.getHealth() <= this.getMaxHealth() / 2) {
			if (source.isProjectile() || source.getDirectEntity() instanceof AbstractArrowEntity || source.getDirectEntity() instanceof ProjectileEntity) {
				amount = 0;
				return false;
			}
		}
		return super.hurt(source, amount);
	}

	private void filterSummonLists() {
		List<Entity> tmp = new ArrayList<>();
		for (Entity ent : this.summonedMinions) {
			if (ent == null || !ent.isAlive()) {
				tmp.add(ent);
			}
		}
		for (Entity e : tmp) {
			this.summonedMinions.remove(e);
		}
		tmp.clear();
		for (Entity ent : this.summonedSkulls) {
			if (ent == null || !ent.isAlive()) {
				tmp.add(ent);
			}
		}
		for (Entity e : tmp) {
			this.summonedSkulls.remove(e);
		}
	}

	@Override
	public void die(DamageSource cause) {
		// Kill minions
		for (Entity e : this.getSummonedEntities()) {
			if (e != null && e.isAlive()) {
				if (e instanceof LivingEntity) {
					((LivingEntity) e).die(cause);
				}
				e.remove();
			}
		}
		this.summonedMinions.clear();

		super.die(cause);
	}

	@Override
	public double getBaseHealth() {
		return CQRConfig.SERVER_CONFIG.baseHealths.necromancer.get();
	}

	@Override
	public EDefaultFaction getDefaultFaction() {
		return EDefaultFaction.UNDEAD;
	}

	@Override
	public Faction getSummonerFaction() {
		return this.getFaction();
	}

	@Override
	public List<Entity> getSummonedEntities() {
		List<Entity> list = new ArrayList<>(this.summonedMinions);
		list.addAll(this.summonedSkulls);
		return list;
	}

	@Override
	public LivingEntity getSummoner() {
		return this;
	}

	@Override
	public void addSummonedEntityToList(Entity summoned) {
		if (summoned instanceof EntityFlyingSkullMinion) {
			this.summonedSkulls.add((EntityFlyingSkullMinion) summoned);
			return;
		}
		this.summonedMinions.add(summoned);
	}

	@Override
	public CreatureAttribute getMobType() {
		return CreatureAttribute.ILLAGER;
	}

	public boolean isBoneShieldActive() {
		return this.entityData.get(BONE_SHIELD_ACTIVE);
	}
	
	@Override
	public IPacket<?> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	// Geckolib
	private AnimationFactory factory = GeckoLibUtil.createFactory(this);
	
	@Override
	public AnimationFactory getFactory() {
		return this.factory;
	}
	
	@Override
	public void registerControllers(AnimationData data) {
		this.registerControllers(this, data);
		
		data.addAnimationController(new AnimationController<>(this, "controller_bone_shield", 0, this::animPredicateBoneShield));
	}
	
	public static final String ANIM_NAME_BONESHIELD_LOOP = "animation.bipednecromancer.boneshield.loop";
	
	protected <E extends EntityCQRNecromancer> PlayState animPredicateBoneShield(AnimationEvent<E> event) {
		if (event.getAnimatable().isIdentityHidden()) {
			if(event.getController().getCurrentAnimation() != null) {
				event.getController().clearAnimationCache();
			}
			return PlayState.STOP;
		}
		if (event.getController().getCurrentAnimation() == null) {
			event.getController().setAnimation(new AnimationBuilder().loop(ANIM_NAME_BONESHIELD_LOOP));
		}
		return PlayState.CONTINUE;
	}

	@Override
	public Set<String> getAlwaysPlayingAnimations() {
		return null;
	}
	
}
