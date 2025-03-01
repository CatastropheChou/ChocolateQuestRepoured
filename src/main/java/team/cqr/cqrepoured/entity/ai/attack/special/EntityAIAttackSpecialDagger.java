package team.cqr.cqrepoured.entity.ai.attack.special;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import team.cqr.cqrepoured.entity.bases.AbstractEntityCQR;
import team.cqr.cqrepoured.item.sword.ItemDagger;

public class EntityAIAttackSpecialDagger extends AbstractEntityAIAttackSpecial {

	public EntityAIAttackSpecialDagger() {
		super(true, false, 10, 100);
	}

	@Override
	public boolean shouldStartAttack(AbstractEntityCQR attacker, LivingEntity target) {
		ItemStack stack = attacker.getMainHandItem();
		return stack.getItem() instanceof ItemDagger;
	}

	@Override
	public boolean shouldContinueAttack(AbstractEntityCQR attacker, LivingEntity target) {
		ItemStack stack = attacker.getMainHandItem();
		return stack.getItem() instanceof ItemDagger;
	}

	@Override
	public boolean isInterruptible(AbstractEntityCQR entity) {
		return false;
	}

	@Override
	public void startAttack(AbstractEntityCQR attacker, LivingEntity target) {

	}

	@Override
	public void continueAttack(AbstractEntityCQR attacker, LivingEntity target, int tick) {

	}

	@Override
	public void stopAttack(AbstractEntityCQR attacker, LivingEntity target) {

	}

	@Override
	public void resetAttack(AbstractEntityCQR attacker) {

	}
}
