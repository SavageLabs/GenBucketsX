package net.prosavage.genbucket.menu.impl;

import net.prosavage.genbucket.utils.MultiversionMaterials;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import net.prosavage.genbucket.GenBucket;
import net.prosavage.genbucket.menu.MenuBuilder;
import net.prosavage.genbucket.utils.ItemUtils;

public class GenerationShopGUI extends MenuBuilder {

	public GenerationShopGUI(GenBucket plugin) {
		super(plugin, plugin.getConfig().getString("generation-shop.name"), plugin.getConfig().getInt("generation-shop.rows"));
	}

	@Override
	public GenerationShopGUI init() {
		ConfigurationSection vertical = getPlugin().getConfig().getConfigurationSection("VERTICAL");
		for (String key : vertical.getKeys(false)) {
			ItemStack item = new ItemStack(Material.valueOf(key));
			item = ItemUtils.createItem(item, getPlugin().getConfig(), "VERTICAL." + key, "Vertical");
			getInventory().setItem(getPlugin().getConfig().getInt("VERTICAL." + key + ".slot"), item);
		}
		ConfigurationSection horizontal = getPlugin().getConfig().getConfigurationSection("HORIZONTAL");
		for (String key : horizontal.getKeys(false)) {
			ItemStack item = new ItemStack(Material.valueOf(key));
			item = ItemUtils.createItem(item, getPlugin().getConfig(), "HORIZONTAL." + key, "Horizontal");
			getInventory().setItem(getPlugin().getConfig().getInt("HORIZONTAL." + key + ".slot"), item);
		}
		for (int i = 0; i < getInventory().getSize(); i++) {
			if (getInventory().getItem(i) == null) getInventory().setItem(i, getBlankItem());
		}
		return this;
	}

	private final ItemStack getBlankItem() {
		// Auto converts to 1.8 / 1.13 material as needed.
		ItemStack item = new ItemStack(MultiversionMaterials.fromString(getPlugin().getConfig().getString("generation-shop.background-item")).parseMaterial());
		if (!getPlugin().getServer().getVersion().contains("1.13")) {
			item.setDurability( (short) getPlugin().getConfig().getInt("generation-shop.background-item-durability"));
		}

		if (getPlugin().getConfig().getBoolean("generation-shop.background-glow")) {
			item.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 1);
		}
		if (item.containsEnchantment(Enchantment.DAMAGE_ALL)) {
			ItemMeta itemMeta = item.getItemMeta();
			itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			item.setItemMeta(itemMeta);
		}
		return item;
	}

}
