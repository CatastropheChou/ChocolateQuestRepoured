package team.cqr.cqrepoured.capability.armor.kingarmor;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;

public class CapabilityDynamicCrown {

	private Item attachedItem = null;

	@Nullable
	public Item getAttachedItem() {
		return this.attachedItem;
	}

	public void attachItem(ItemStack item) {
		this.attachedItem = item.getItem();
	}

	public void attachItem(Item item) {
		this.attachedItem = item;
	}

	public void attachItem(ResourceLocation itemResLoc) {
		if (ForgeRegistries.ITEMS.containsKey(itemResLoc)) {
			this.attachedItem = ForgeRegistries.ITEMS.getValue(itemResLoc);
		}
	}

}
