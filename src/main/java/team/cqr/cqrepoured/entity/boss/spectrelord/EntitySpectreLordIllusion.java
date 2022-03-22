package team.cqr.cqrepoured.entity.boss.spectrelord;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import team.cqr.cqrepoured.entity.ai.boss.spectrelord.EntityAISpectreLordIllusionExplosion;
import team.cqr.cqrepoured.entity.ai.boss.spectrelord.EntityAISpectreLordIllusionHeal;
import team.cqr.cqrepoured.entity.mobs.EntityCQRSpectre;
import team.cqr.cqrepoured.faction.Faction;
import team.cqr.cqrepoured.faction.FactionRegistry;
import team.cqr.cqrepoured.init.CQRItems;
import team.cqr.cqrepoured.util.EntityUtil;

import java.util.UUID;

public class EntitySpectreLordIllusion extends EntityCQRSpectre {

	private LivingEntity caster;
	private int lifeTime;
	private boolean canCastHeal;
	private boolean canCastExplosion;

	public EntitySpectreLordIllusion(World worldIn) {
		this(worldIn, null, 200, false, false);
	}

	public EntitySpectreLordIllusion(World worldIn, LivingEntity caster, int lifeTime, boolean canCastHeal, boolean canCastExplosion) {
		super(worldIn);
		if (caster != null) {
			this.caster = caster;
			Faction faction = FactionRegistry.instance(this).getFactionOf(caster);
			if (faction != null) {
				this.setFaction(faction.getName(), true);
			}
		}
		this.lifeTime = lifeTime;
		this.canCastHeal = canCastHeal;
		this.canCastExplosion = canCastExplosion;
		if (canCastHeal) {
			this.spellHandler.addSpell(0, new EntityAISpectreLordIllusionHeal(this, 160, 40));
		}
		if (canCastExplosion) {
			this.spellHandler.addSpell(0, new EntityAISpectreLordIllusionExplosion(this, 160, 40));
		}
	}

	@Override
	public ILivingEntityData onInitialSpawn(DifficultyInstance difficulty, ILivingEntityData livingdata) {
		super.onInitialSpawn(difficulty, livingdata);
		if (this.rand.nextDouble() < 0.3D) {
			switch (this.rand.nextInt(3)) {
			case 0:
				this.setHeldItem(Hand.MAIN_HAND, new ItemStack(CQRItems.SPEAR_IRON));
				break;
			case 1:
				this.setHeldItem(Hand.MAIN_HAND, new ItemStack(CQRItems.DAGGER_IRON));
				break;
			case 2:
				this.setHeldItem(Hand.MAIN_HAND, new ItemStack(CQRItems.GREAT_SWORD_IRON));
				break;
			}
		} else {
			this.setHeldItem(Hand.MAIN_HAND, new ItemStack(Items.IRON_SWORD));
		}
		this.setHealingPotions(0);
		return livingdata;
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.225D);
		this.getEntityAttribute(Attributes.ARMOR).setBaseValue(this.canCastHeal || this.canCastExplosion ? 8.0D : 12.0D);
		this.getEntityAttribute(Attributes.ARMOR_TOUGHNESS).setBaseValue(this.canCastHeal || this.canCastExplosion ? 4.0D : 6.0D);
	}

	@Override
	public void onEntityUpdate() {
		if (!this.world.isRemote && this.lifeTime-- <= 0) {
			this.setDead();
		}

		super.onEntityUpdate();
	}

	@Override
	public float getBaseHealth() {
		return 20.0F;
	}

	@Override
	public boolean canPutOutFire() {
		return false;
	}

	@Override
	public boolean canIgniteTorch() {
		return false;
	}

	@Override
	public boolean canTameEntity() {
		return false;
	}

	@Override
	public boolean canMountEntity() {
		return false;
	}

	@Override
	protected boolean shouldDropLoot() {
		return false;
	}

	@Override
	public void save(CompoundNBT compound) {
		super.save(compound);
		if (this.caster != null && this.caster.isEntityAlive()) {
			compound.setUniqueId("Summoner", this.caster.getPersistentID());
		}
		compound.setInteger("lifeTime", this.lifeTime);
	}

	@Override
	public void readEntityFromNBT(CompoundNBT compound) {
		super.readEntityFromNBT(compound);
		if (compound.hasKey("SummonerMost", Constants.NBT.TAG_LONG)) {
			UUID uuid = compound.getUniqueId("Summoner");
			Entity e = EntityUtil.getEntityByUUID(this.world, uuid);
			if (e instanceof EntityCQRSpectreLord) {
				this.caster = (EntityCQRSpectreLord) e;
			}
		}
		this.lifeTime = compound.getInteger("lifeTime");
	}

	@Override
	public void onDeath(DamageSource cause) {
		super.die(cause);
		if (this.caster != null) {
			this.caster.attackEntityFrom(DamageSource.causeMobDamage(this).setDamageBypassesArmor(), this.caster.getMaxHealth() * 0.025F);
		}
	}

}
