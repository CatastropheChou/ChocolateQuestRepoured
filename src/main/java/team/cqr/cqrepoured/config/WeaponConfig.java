package team.cqr.cqrepoured.config;

import net.minecraft.item.IItemTier;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.LazyValue;

import java.util.function.Supplier;

public class WeaponConfig implements IItemTier {

	public int uses;
	public float attackDamageBonus;
	public int enchantmentValue;
	public LazyValue<Ingredient> repairIngredient;

	public WeaponConfig(int uses, float attackDamageBonus, int enchantmentValue, Supplier<Ingredient> repairIngredient)
	{
		this.uses = uses;
		this.attackDamageBonus = attackDamageBonus;
		this.enchantmentValue = enchantmentValue;
		this.repairIngredient = new LazyValue<>(repairIngredient);
	}

	@Override
	public int getUses()
	{
		return this.uses;
	}

	@Override
	public float getSpeed()
	{
		return 0;
	}

	@Override
	public float getAttackDamageBonus()
	{
		return this.attackDamageBonus;
	}

	@Override
	public int getLevel()
	{
		return 0;
	}

	@Override
	public int getEnchantmentValue()
	{
		return this.enchantmentValue;
	}

	@Override
	public Ingredient getRepairIngredient()
	{
		return this.repairIngredient.get();
	}
}
