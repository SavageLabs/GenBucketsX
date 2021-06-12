package net.prosavage.genbucket.command.impl;

import com.cryptomorin.xseries.XMaterial;
import net.prosavage.genbucket.GenBucket;
import net.prosavage.genbucket.command.AbstractCommand;
import net.prosavage.genbucket.config.Config;
import net.prosavage.genbucket.config.Message;
import net.prosavage.genbucket.gen.GenData;
import net.prosavage.genbucket.utils.ChatUtils;
import net.prosavage.genbucket.utils.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CommandGive extends AbstractCommand {


    public CommandGive(GenBucket plugin) {
        super(plugin, "give", false);
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        //gen give [player] [genID] [amount]
        if (args.length < 4) {
            sender.sendMessage(ChatUtils.color(Message.PREFIX.getMessage() + Message.CMD_USAGE.getMessage()
                    .replace("%command%", "/gen give")
                    .replace("%args%", "[Player] [GenID] [Amount]")));
            return false;
        }
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(Message.PREFIX.getMessage() + Message.ERROR_INVALID_PLAYER.getMessage()
                    .replace("%player%", args[1]));
            return false;
        }
        if (!GenBucket.genDataMap.containsKey(args[2])) {
            sender.sendMessage(Message.PREFIX.getMessage() + Message.CMD_GIVE_INVALID_ID.getMessage()
                    .replace("%genid%", args[2]));
            return false;
        }
        GenData genData = GenBucket.genDataMap.get(args[2]);
        int amount;
        try {
            amount = Integer.parseInt(args[3]);
        } catch (NumberFormatException nfe) {
            amount = 1;
        }
        amount = Math.max(1, amount); // Ensure amount > 0
        amount = Math.min(127, amount); // Ensure amount <= 127
        ItemStack item;
        if (Config.USE_BUCKETS.getOption()) {
            item = XMaterial.LAVA_BUCKET.parseItem();
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(genData.getItem().getItemMeta().getDisplayName());
            item.setItemMeta(meta);
        } else {
            item = genData.getItem();
            item.setAmount(amount);
        }
        item = ItemUtils.setKeyString(item, "GENBUCKET-ID", genData.getGenID());
        target.getInventory().addItem(item);
        return false;
    }

    @Override
    public String getDescription() {
        return Message.CMD_GIVE_DESC.getMessage();
    }

    @Override
    public String getPermission() {
        return Config.PERMISSION_GIVE.getString();
    }
}
