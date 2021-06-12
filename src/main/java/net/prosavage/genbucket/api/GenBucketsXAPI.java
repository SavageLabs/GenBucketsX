package net.prosavage.genbucket.api;

import fr.minuskube.inv.SmartInventory;
import net.prosavage.genbucket.GenBucket;
import net.prosavage.genbucket.config.Config;
import net.prosavage.genbucket.menu.GenShopGUI;
import net.prosavage.genbucket.utils.ChatUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;

public class GenBucketsXAPI {

    public static void openGenShopGUI(Player player) {
        SmartInventory genShopGUI = SmartInventory.builder()
                .id("gsxgui+" + player.getUniqueId())
                .provider(new GenShopGUI())
                .manager(GenBucket.invManager)
                .type(InventoryType.CHEST)
                .size(Config.GUI_SIZE.getInt(), 9)
                .title(ChatUtils.color(Config.GUI_TITLE.getString()))
                .build();
        genShopGUI.open(player);
    }

}
