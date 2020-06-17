package net.prosavage.genbucket.gen;

import com.cryptomorin.xseries.XMaterial;
import net.prosavage.genbucket.GenBucket;
import net.prosavage.genbucket.utils.ChatUtils;
import net.prosavage.genbucket.utils.ItemUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class GenData {

    private String genID;
    private ItemStack item;
    private GenType type;
    private GenDirection direction;
    private boolean pseudo;
    private int data = 0;
    private int slot;
    private double price;
    private boolean consumable;
    private int horizontalDistance = 10;

    public GenData(String genID, String direction, String material, String name, boolean pseudo, int slot, double price, boolean consumable, int horizontalDistance) {
        this.genID = genID;
        this.slot = slot;
        this.horizontalDistance = horizontalDistance;
        this.consumable = consumable;
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
        Material mat = ItemUtils.parseMaterial(material);
        if (mat == XMaterial.WATER.parseMaterial()) {
            item = XMaterial.WATER_BUCKET.parseItem();
        } else if (mat == XMaterial.LAVA.parseMaterial()) {
            item = XMaterial.LAVA_BUCKET.parseItem();
        } else {
            try {
                item = new ItemStack(Material.valueOf(material));
            } catch (Exception ex) {
                item = XMaterial.matchXMaterial(material).get().parseItem();
            }
        }
        if (GenBucket.getServerVersion() < 13)
            this.data = item.getDurability();
        if (name != null) {
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ChatUtils.color(name));
            item.setItemMeta(meta);
        }
        item = ItemUtils.setKeyString(item, "GENBUCKET-ID", genID);
        this.pseudo = pseudo;
        this.price = price;
    }

    public ItemStack getItem() {
        return item;
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

}
