package net.prosavage.genbucket.utils;


import net.prosavage.genbucket.utils.nbt.NBTItem;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemUtils {

    public static ItemStack setKeyString(ItemStack itemStack, String key, String value) {
        NBTItem nbtItem = new NBTItem(itemStack);
        nbtItem.setString(key, value);
        return nbtItem.getItem();
    }

    public static String getKeyString(ItemStack itemStack, String key) {
        NBTItem nbtItem = new NBTItem(itemStack);
        return nbtItem.getString(key);
    }

    public static boolean hasKey(ItemStack itemStack, String key) {
        if (itemStack == null) return false;
        return new NBTItem(itemStack).hasKey(key);
    }

    public static ItemStack createItem(ItemStack item, FileConfiguration config, String key, String type) {
        item.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', config.getString(key + ".name")));
        ArrayList<String> lore = new ArrayList<String>();
        for (String s : config.getStringList("genbucket-lore")) {
            lore.add(ChatColor.translateAlternateColorCodes('&', s.replace("%type%", type).replace("%price%", String.valueOf(config.getInt(key + ".price")))));
        }
        itemMeta.setLore(lore);
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(itemMeta);
        item = setKeyString(item, "MATERIAL", item.getType().name());
        return setKeyString(item, "GENBUCKET", type.toUpperCase());
    }

    public static ItemStack setDisplayName(ItemStack itemStack, String name) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static ItemStack setLore(ItemStack itemStack, List<String> lore) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        List<String> coloredLore = new ArrayList<>();
        for (String string : lore) {
            coloredLore.add(ChatColor.translateAlternateColorCodes('&', string));
        }
        itemMeta.setLore(coloredLore);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

}