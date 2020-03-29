package net.prosavage.genbucket.menu.impl;

import com.cryptomorin.xseries.XMaterial;
import net.prosavage.genbucket.GenBucket;
import net.prosavage.genbucket.menu.MenuBuilder;
import net.prosavage.genbucket.utils.ChatUtils;
import net.prosavage.genbucket.utils.ItemUtils;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class GenerationShopGUI extends MenuBuilder {

    public GenerationShopGUI(GenBucket plugin) {
        super(plugin, plugin.getConfig().getString("generation-shop.name"), plugin.getConfig().getInt("generation-shop.rows"));
    }

    private ItemStack initItem(String key) {
        ItemStack item;
        try {
            item = new ItemStack(Material.valueOf(key));
        } catch (Exception ex) {
            item = XMaterial.matchXMaterial(key).get().parseItem();
        }
        if (item == null) {
            ChatUtils.sendConsole("[SavageGenBuckets] Error while parsing Material for key= " + key + " ... Skipping...");
        }
        return item;
    }

    @Override
    public GenerationShopGUI init() {
        ConfigurationSection vertical = getPlugin().getConfig().getConfigurationSection("VERTICAL");
        for (String key : vertical.getKeys(false)) {
            ItemStack item = initItem(key);
            if (item == null) continue;
            item = ItemUtils.createItem(item, getPlugin().getConfig(), "VERTICAL." + key, "Vertical");
            getInventory().setItem(getPlugin().getConfig().getInt("VERTICAL." + key + ".slot"), item);
        }
        ConfigurationSection horizontal = getPlugin().getConfig().getConfigurationSection("HORIZONTAL");
        for (String key : horizontal.getKeys(false)) {
            ItemStack item = initItem(key);
            if (item == null) continue;
            item = ItemUtils.createItem(item, getPlugin().getConfig(), "HORIZONTAL." + key, "Horizontal");
            getInventory().setItem(getPlugin().getConfig().getInt("HORIZONTAL." + key + ".slot"), item);
        }
        for (int i = 0; i < getInventory().getSize(); i++) {
            if (getInventory().getItem(i) == null) getInventory().setItem(i, getBlankItem());
        }
        return this;
    }

    private ItemStack getBlankItem() {
        // Auto converts to 1.8 / 1.13 material as needed.
        FileConfiguration config = getPlugin().getConfig();
        ItemStack item;
        try {
            item = XMaterial.matchXMaterial(config.getString("generation-shop.background-item")).get().parseItem();
        } catch (NullPointerException npe) {
            // Handle case in which the user uses invalid Material
            item = new ItemStack(Material.AIR);
        }
        if (config.getBoolean("generation-shop.background-glow")) {
            item.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 1);
        }
        ItemMeta itemMeta = item.getItemMeta();
        if (item.containsEnchantment(Enchantment.DAMAGE_ALL)) {
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        itemMeta.setDisplayName(ChatUtils.color(config.getString("generation-shop.blank-item.name")));
        if (!config.getBoolean("generation-shop.no-lore")) {
            itemMeta.setLore(ChatUtils.color(config.getStringList("generation-shop.blank-item.lore")));
        }
        item.setItemMeta(itemMeta);
        return item;
    }

}
