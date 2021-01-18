package net.prosavage.genbucket.menu;

import com.cryptomorin.xseries.XMaterial;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import net.prosavage.genbucket.GenBucket;
import net.prosavage.genbucket.config.Config;
import net.prosavage.genbucket.config.Message;
import net.prosavage.genbucket.gen.GenData;
import net.prosavage.genbucket.gen.GenType;
import net.prosavage.genbucket.utils.ChatUtils;
import net.prosavage.genbucket.utils.ItemUtils;
import net.prosavage.genbucket.utils.Util;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class GenShopGUI implements InventoryProvider {

    @Override
    public void init(Player player, InventoryContents contents) {
        contents.fill(ClickableItem.empty(ItemUtils.getBlankItem()));
        for (GenData genData : GenBucket.genDataMap.values()) {
            ItemStack item = genData.getShownItem().clone();
            List<String> lore = new ArrayList<>(Config.GUI_ITEM_GEN_LORE.getStringList());
            if (!lore.isEmpty()) {
                String genTypeLocale;
                if (genData.getType() == GenType.HORIZONTAL) {
                    genTypeLocale = Message.GENTYPE_HORIZONTAL.getMessage();
                } else {
                    genTypeLocale = Message.GENTYPE_VERTICAL.getMessage();
                }
                ItemMeta meta = item.getItemMeta();
                if (meta != null) {
                    lore.replaceAll(s -> s
                            .replace("%type%", genTypeLocale)
                            .replace("%pseudo%", genData.isPseudo() + "")
                            .replace("%consumable%", genData.isConsumable() + "")
                            .replace("%distance%", genData.getHorizontalDistance() + "")
                            .replace("%material%", item.getType().name())
                            .replace("%price%", Util.formatPrice(genData.getPrice()))
                    );
                    meta.setLore(ChatUtils.color(lore));
                    item.setItemMeta(meta);
                }
            }
            contents.set(genData.getSlot(), ClickableItem.from(item, e -> {
                if ((item.getType() == XMaterial.WATER_BUCKET.parseMaterial() || item.getType() == XMaterial.LAVA_BUCKET.parseMaterial())
                        && !Config.ALLOW_LIQUIDS.getOption()) {
                    player.sendMessage(ChatUtils.color(Message.PREFIX.getMessage() + Message.GEN_LIQUID_DISABLED.getMessage()));
                    return;
                }
                ItemUtils.giveOrDrop(player, genData.getItem());
                if (Config.GUI_CLOSE_AFTER_CLICK.getOption())
                    player.closeInventory();
            }));
        }
    }

    @Override
    public void update(Player player, InventoryContents contents) {
    }

}
