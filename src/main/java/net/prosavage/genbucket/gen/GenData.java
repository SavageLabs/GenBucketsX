package net.prosavage.genbucket.gen;

import com.cryptomorin.xseries.XMaterial;
import net.prosavage.genbucket.GenBucket;
import net.prosavage.genbucket.config.Config;
import net.prosavage.genbucket.config.Message;
import net.prosavage.genbucket.utils.ChatUtils;
import net.prosavage.genbucket.utils.ItemUtils;
import net.prosavage.genbucket.utils.Util;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GenData {

    private String genID;
    private String name;
    private String guiMaterial;
    private GenType type;
    private GenDirection direction;
    private String material;
    private boolean pseudo;
    private boolean glow;

    private int slot;
    private double price;
    private boolean consumable;
    private int horizontalDistance;
    private int amount;
    private int delayTicks;

    private String parsedMaterial;
    private String givenMaterial;
    private int data = 0;

    public GenData(String genID, String direction, String material, String guiMaterial, String givenMaterial, String name, boolean pseudo, int slot, int amount, double price, boolean consumable, int horizontalDistance, boolean glow, int delayTicks) {
        this.genID = genID;
        this.amount = amount;
        this.slot = slot;
        this.name = name;
        this.guiMaterial = guiMaterial;
        this.givenMaterial = givenMaterial;
        this.horizontalDistance = horizontalDistance;
        if (this.horizontalDistance <= 0)
            this.horizontalDistance = 10;
        this.consumable = consumable;
        this.material = material;
        this.glow = glow;
        this.delayTicks = delayTicks;
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
        this.pseudo = pseudo;
        this.price = price;
        ItemStack testItem = ItemUtils.parseItem(material);
        if (GenBucket.getServerVersion() < 13)
            this.data = testItem.getDurability();
        this.parsedMaterial = testItem.getType().name();
    }

    private ItemStack finalizeItem(ItemStack toFinalize) {
        if (this.name != null) {
            ItemMeta meta = toFinalize.getItemMeta();
            meta.setDisplayName(ChatUtils.color(this.name));
            toFinalize.setItemMeta(meta);
        }
        if (toFinalize.getAmount() != amount && amount > 0)
            toFinalize.setAmount(amount);
        ItemStack itemWithGenId = ItemUtils.setKeyString(toFinalize, "GENBUCKET-ID", genID);
        return ItemUtils.setKeyString(itemWithGenId, "UNIQUE_ID", UUID.randomUUID().toString());
    }

    public List<String> parsePlaceholders(List<String> toParse) {
        List<String> parsed = new ArrayList<>(toParse);
        parsed.replaceAll(s -> s
                .replace("%type%", getTypeLocalized())
                .replace("%pseudo%", isPseudo() + "")
                .replace("%consumable%", isConsumable() + "")
                .replace("%distance%", getHorizontalDistance() + "")
                .replace("%material%", parsedMaterial)
                .replace("%delay%", getDelayTicks() + "")
                .replace("%price%", Util.formatPrice(getPrice()))
        );
        return ChatUtils.color(parsed);
    }

    public ItemStack getItem() {
        ItemStack item = ItemUtils.parseItem(givenMaterial);
        if (givenMaterial.equalsIgnoreCase(material))
            if (item.getType() == XMaterial.WATER.parseMaterial()) {
                item = XMaterial.WATER_BUCKET.parseItem();
            } else if (item.getType() == XMaterial.LAVA.parseMaterial()) {
                item = XMaterial.LAVA_BUCKET.parseItem();
            } else if (Config.USE_BUCKETS.getOption()) {
                item = XMaterial.matchXMaterial(Config.DEFAULT_BUCKET_TYPE.getString()).orElse(XMaterial.BUCKET).parseItem();
            }
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setLore(parsePlaceholders(Config.GEN_ITEM_LORE.getStringList()));
        }
        item = finalizeItem(item);
        if (!Config.GLOW_ONLY_GUI.getOption() && this.glow) {
            item = ItemUtils.setGlowing(item);
        }
        return item;
    }

    public ItemStack getShownItem() {
        ItemStack shown = ItemUtils.parseItem(guiMaterial);
        if (guiMaterial.equalsIgnoreCase(material))
            if (shown.getType() == XMaterial.WATER.parseMaterial()) {
                shown = XMaterial.WATER_BUCKET.parseItem();
            } else if (shown.getType() == XMaterial.LAVA.parseMaterial()) {
                shown = XMaterial.LAVA_BUCKET.parseItem();
            }
        ItemMeta meta = shown.getItemMeta();
        if (meta != null) {
            meta.setLore(parsePlaceholders(Config.GUI_ITEM_GEN_LORE.getStringList()));
            shown.setItemMeta(meta);
        }
        if (glow)
            return ItemUtils.setGlowing(shown);
        return finalizeItem(shown);
    }

    public GenType getType() {
        return type;
    }

    public String getTypeLocalized() {
        if (getType() == GenType.HORIZONTAL) {
            return Message.GENTYPE_HORIZONTAL.getMessage();
        } else {
            return Message.GENTYPE_VERTICAL.getMessage();
        }
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

    public boolean isGlow() {
        return glow;
    }

    public int getDelayTicks() {
        return delayTicks;
    }

    public Material getMaterial() {
        return ItemUtils.parseMaterial(material);
    }

    public String getParsedMaterial() {
        return parsedMaterial;
    }
}
