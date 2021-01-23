package net.prosavage.genbucket.menu;

import com.cryptomorin.xseries.XMaterial;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import net.prosavage.genbucket.GenBucket;
import net.prosavage.genbucket.config.Config;
import net.prosavage.genbucket.config.Message;
import net.prosavage.genbucket.gen.GenData;
import net.prosavage.genbucket.utils.ChatUtils;
import net.prosavage.genbucket.utils.ItemUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GenShopGUI implements InventoryProvider {

    @Override
    public void init(Player player, InventoryContents contents) {
        contents.fill(ClickableItem.empty(ItemUtils.getBlankItem()));
        for (GenData genData : GenBucket.genDataMap.values()) {
            ItemStack item = genData.getShownItem().clone();
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
