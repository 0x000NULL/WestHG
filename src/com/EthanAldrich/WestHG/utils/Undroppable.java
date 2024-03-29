package WestHG.utils;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;

public class Undroppable extends Enchantment {

	public static Undroppable ench = new Undroppable();

	public Undroppable() {
		super(69);
	}

	@Override
	public boolean canEnchantItem(ItemStack is) {
		return true;
	}

	@Override
	public boolean conflictsWith(Enchantment ench) {
		return false;
	}

	@Override
	public EnchantmentTarget getItemTarget() {
		return EnchantmentTarget.ALL;
	}

	@Override
	public boolean isTreasure() {
		return false;
	}

	@Override
	public boolean isCursed() {
		return false;
	}

	@Override
	public int getMaxLevel() {
		return 1;
	}

	@Override
	public String getName() {
		return "Undroppable";
	}

	@Override
	public int getStartLevel() {
		return 1;
	}
}
