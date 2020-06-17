package net.prosavage.genbucket.menu.impl;

import com.cryptomorin.xseries.XMaterial;
import net.prosavage.genbucket.GenBucket;
import net.prosavage.genbucket.config.Config;
import net.prosavage.genbucket.config.Message;
import net.prosavage.genbucket.gen.GenData;
import net.prosavage.genbucket.gen.GenType;
import net.prosavage.genbucket.menu.MenuBuilder;
import net.prosavage.genbucket.utils.ChatUtils;
import net.prosavage.genbucket.utils.ItemUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class GenerationShopGUI extends MenuBuilder {

    public GenerationShopGUI(GenBucket plugin) {
        super(plugin, ChatUtils.color(Config.GUI_TITLE.getString()), Config.GUI_SIZE.getInt());
    }

    @Override
    public GenerationShopGUI init() {
        for (GenData genData : GenBucket.genDataMap.values()) {
            ItemStack item = genData.getItem().clone();
            List<String> lore = new ArrayList<>();
            lore.addAll(Config.GUI_ITEM_GEN_LORE.getStringList());
            if (lore != null && !lore.isEmpty()) {
                String genTypeLocale;
                if (genData.getType() == GenType.HORIZONTAL) {
                    genTypeLocale = Message.GENTYPE_HORIZONTAL.getMessage();
                } else {
                    genTypeLocale = Message.GENTYPE_VERTICAL.getMessage();
                }
                ItemMeta meta = item.getItemMeta();
                lore.replaceAll(s -> s
                        .replace("%type%", genTypeLocale)
                        .replace("%pseudo%", genData.isPseudo() + "")
                        .replace("%consumable%", genData.isConsumable() + "")
                        .replace("%distance%", genData.getHorizontalDistance() + "")
                        .replace("%material%", item.getType().name())
                        .replace("%price%", GenBucket.econ.format(genData.getPrice()))
                );
                meta.setLore(ChatUtils.color(lore));
                item.setItemMeta(meta);
            }
            getInventory().setItem(genData.getSlot(), item);
        }
        for (int i = 0; i < getInventory().getSize(); i++) {
            if (getInventory().getItem(i) == null) getInventory().setItem(i, getBlankItem());
        }
        return this;
    }

    private ItemStack getBlankItem() {
        ItemStack item;
        try {
            item = XMaterial.matchXMaterial(Config.GUI_ITEM_EMPTY.getString()).get().parseItem();
        } catch (NullPointerException npe) {
            // Handle case in which the user uses invalid Material
            item = new ItemStack(Material.AIR);
        }
        if (Config.GUI_ITEM_EMPTY_GLOW.getOption()) {
            item = ItemUtils.setGlowing(item);
        }
        ItemMeta itemMeta = item.getItemMeta();
        String displayName = Config.GUI_ITEM_EMPTY_NAME.getString();
        List<String> lore = Config.GUI_ITEM_EMPTY_LORE.getStringList();
        if (displayName != null)
            itemMeta.setDisplayName(ChatUtils.color(displayName));
        if (lore != null && !lore.isEmpty()) {
            itemMeta.setLore(ChatUtils.color(lore));
        }
        item.setItemMeta(itemMeta);
        return item;
    }

}
