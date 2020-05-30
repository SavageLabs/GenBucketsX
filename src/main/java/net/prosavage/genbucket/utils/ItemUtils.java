package net.prosavage.genbucket.utils;

import com.cryptomorin.xseries.XEnchantment;
import com.cryptomorin.xseries.XMaterial;
import net.prosavage.genbucket.GenBucket;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ItemUtils {

    private ItemUtils() {
        throw new AssertionError("Instantiating utility class.");
    }

    public static ItemStack setKeyString(ItemStack item, String key, String value) {
        return NBTEditor.set(item, value, key);
    }

    public static ItemStack setKeyInt(ItemStack item, String key, int value) {
        return NBTEditor.set(item, value, key);
    }

    public static String getKeyString(ItemStack item, String key) {
        return NBTEditor.getString(item, key);
    }

    public static int getKeyInt(ItemStack item, String key) {
        return NBTEditor.getInt(item, key);
    }

    public static boolean hasKey(ItemStack item, String key) {
        try {
            if (item == null) return false;
            return NBTEditor.contains(item, key);
        } catch (Exception ex) {
            return false;
        }
    }

    public static ItemStack createItem(ItemStack item, FileConfiguration config, String key, String type, int data) {
        Material itemType = item.getType();
        // Need to change water and lava to buckets.
        if (itemType == XMaterial.WATER.parseMaterial()) {
            item = XMaterial.WATER_BUCKET.parseItem();
        } else if (itemType == XMaterial.LAVA.parseMaterial()) {
            item = XMaterial.LAVA_BUCKET.parseItem();
        }
        item = setGlowing(item);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatUtils.color(config.getString(key + ".name")));
        ArrayList<String> lore = new ArrayList<>();
        for (String s : config.getStringList("genbucket-lore")) {
            lore.add(ChatUtils.color(s
                    .replace("%type%", type)
                    .replace("%price%", "" + config.getInt(key + ".price"))));
        }
        itemMeta.setLore(lore);
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(itemMeta);
        if (data > 0) {
            item = setKeyInt(item, "MATERIALDATA", data);
        }
        item = setKeyString(item, "MATERIAL", itemType.name());
        return setKeyString(item, "GENBUCKET", type.toUpperCase());
    }

    public static ItemStack setDisplayName(ItemStack itemStack, String name) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(ChatUtils.color(name));
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static ItemStack setLore(ItemStack itemStack, List<String> lore) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        List<String> coloredLore = new ArrayList<>();
        for (String string : lore) {
            coloredLore.add(ChatUtils.color(string));
        }
        itemMeta.setLore(coloredLore);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static ItemStack setGlowing(ItemStack item) {
        if (item == null) return null;
        try {
            if (item.getType() == XMaterial.FISHING_ROD.parseMaterial()) {
                item.addUnsafeEnchantment(XEnchantment.ARROW_KNOCKBACK.parseEnchantment(), 1);
            } else {
                item.addUnsafeEnchantment(XEnchantment.LURE.parseEnchantment(), 1);
            }
            ItemMeta metadata = item.getItemMeta();
            if (metadata != null) {
                metadata.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                item.setItemMeta(metadata);
            }
            return item;
        } catch (Exception e) {
            ChatUtils.sendConsole("&cError while making item glow! Contact the developer!");
            return item;
        }
    }

    public static void setFacing(Block block, BlockFace blockFace) {
        if (GenBucket.get().getConfig().getBoolean("use-opposite-facing")) blockFace = blockFace.getOppositeFace();
        // 1.13+
        if (GenBucket.getServerVersion() >= 13) {
            BlockData blockData = block.getBlockData();
            if (blockData instanceof Directional) {
                ((Directional) blockData).setFacing(blockFace);
                block.setBlockData(blockData, false);
            }
        } else {
            // 1.8-1.12
            BlockState blockState = block.getState();
            if (blockState.getData() instanceof org.bukkit.material.Directional) {
                MaterialData blockData = blockState.getData();
                ((org.bukkit.material.Directional) blockData).setFacingDirection(blockFace);
                blockState.setData(blockData);
                blockState.update(false, false);
            }
        }
    }

    public static Material parseMaterial(String material) {
        try {
            Material mat = Material.valueOf(material.toUpperCase());
            return mat;
        } catch (Exception e) {
            return XMaterial.matchXMaterial(material).get().parseMaterial();
        }
    }

    public static BlockFace yawToFace(float yaw, boolean useSubCardinalDirections) {
        if (useSubCardinalDirections)
            return radial[Math.round(yaw / 45f) & 0x7].getOppositeFace();
        return axis[Math.round(yaw / 90f) & 0x3].getOppositeFace();
    }

    private static Method setDataMethod = null;

    public static void setBlockData(Block block, int data) {
        if (GenBucket.get().getConfig().getBoolean("use-block-data") && GenBucket.getServerVersion() < 13)

            try {
                if (setDataMethod == null) {
                    setDataMethod = Block.class.getMethod("setData", byte.class);
                }
                setDataMethod.invoke(block, (byte) data);
            } catch (Exception ex) {
                ChatUtils.debug("Exception while getting setDataMethod");
            }

    }

    private static final BlockFace[] axis = {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};
    private static final BlockFace[] radial = {BlockFace.NORTH, BlockFace.NORTH_EAST, BlockFace.EAST, BlockFace.SOUTH_EAST, BlockFace.SOUTH, BlockFace.SOUTH_WEST, BlockFace.WEST, BlockFace.NORTH_WEST};

}