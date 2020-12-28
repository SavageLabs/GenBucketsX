package net.prosavage.genbucket.gen;

import com.cryptomorin.xseries.XMaterial;
import net.prosavage.genbucket.GenBucket;
import net.prosavage.genbucket.config.Config;
import net.prosavage.genbucket.utils.ChatUtils;
import net.prosavage.genbucket.utils.ItemUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class GenData {

    private String genID;
    private ItemStack item;
    private GenType type;
    private GenDirection direction;
    private String material;
    private boolean pseudo;
    private int data = 0;
    private int slot;
    private double price;
    private boolean consumable;
    private int horizontalDistance = 10;
    private int amount;

    public GenData(String genID, String direction, String material, String name, boolean pseudo, int slot, int amount, double price, boolean consumable, int horizontalDistance) {
        this.genID = genID;
        this.amount = amount;
        this.slot = slot;
        this.horizontalDistance = horizontalDistance;
        this.consumable = consumable;
        this.material = material;
        if (direction.equalsIgnoreCase("horizontal")) {
            this.type = GenType.HORIZONTAL;
            this.direction = GenDirection.AUTO;
        } else {
            if (direction.equalsIgnoreCase("up")) {
                this.direction = GenDirection.UP;
            } else if (direction.equalsIgnoreCase("down")) {
                this.direction = GenDirection.DOWN;
            } else {
                this.direction = GenDirection.AUTO;
            }
            this.type = GenType.VERTICAL;
        }
        item = ItemUtils.parseItem(material);
        if (GenBucket.getServerVersion() < 13)
            this.data = item.getDurability();
        if (name != null) {
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ChatUtils.color(name));
            item.setItemMeta(meta);
        }
        if (item.getAmount() != amount)
            item.setAmount(amount);
        item = ItemUtils.setKeyString(item, "GENBUCKET-ID", genID);
        this.pseudo = pseudo;
        this.price = price;
    }

    public ItemStack getItem() {
        return item.clone();
    }

    public ItemStack getShownItem() {
        ItemStack clone = item.clone();
        if (Config.USE_BUCKETS.getOption() || item.getType() == XMaterial.LAVA.parseMaterial() || item.getType() == XMaterial.WATER.parseMaterial()) {
            String name = clone.getItemMeta().getDisplayName();
            List<String> lore = clone.getItemMeta().getLore();
            clone = item.getType() == XMaterial.WATER.parseMaterial() ? XMaterial.WATER_BUCKET.parseItem() : XMaterial.LAVA_BUCKET.parseItem();
            if (clone != null && clone.getItemMeta() != null) {
                ItemMeta itmMeta = clone.getItemMeta();
                itmMeta.setDisplayName(name);
                if (lore != null && !lore.isEmpty())
                    itmMeta.setLore(lore);
                clone.setItemMeta(itmMeta);
            }
            return ItemUtils.setKeyString(clone, "GENBUCKET-ID", getGenID());
        } else {
            return clone;
        }
    }

    public GenType getType() {
        return type;
    }

    public GenDirection getDirection() {
        return direction;
    }

    public boolean isPseudo() {
        return pseudo;
    }

    public int getData() {
        return data;
    }

    public int getSlot() {
        return slot;
    }

    public double getPrice() {
        return price;
    }

    public boolean isConsumable() {
        return consumable;
    }

    public int getHorizontalDistance() {
        if (horizontalDistance <= 0) return 10;
        return horizontalDistance;
    }

    public String getGenID() {
        return genID;
    }

    public int getAmount() {
        return amount;
    }

    public Material getMaterial() {
        return ItemUtils.parseMaterial(material);
    }

}
