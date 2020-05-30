package net.prosavage.genbucket.command.impl;

import com.cryptomorin.xseries.XMaterial;
import net.prosavage.genbucket.GenBucket;
import net.prosavage.genbucket.command.AbstractCommand;
import net.prosavage.genbucket.utils.ChatUtils;
import net.prosavage.genbucket.utils.ItemUtils;
import net.prosavage.genbucket.utils.Message;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CommandGive extends AbstractCommand {


    public CommandGive(GenBucket plugin) {
        super(plugin, "give", false);
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {

        if (args.length == 5) {
            //gen give [player] [gentype] [material] [amount]
            if (args[2].equalsIgnoreCase("HORIZONTAL") || args[2].equalsIgnoreCase("VERTICAL")) {
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    sender.sendMessage(Message.PREFIX.getMessage() + Message.ERROR_INVALID_PLAYER.getMessage().replace("%player%", args[1]));
                    return false;
                }
                if (XMaterial.matchXMaterial(args[3].toUpperCase()).isPresent()) {
                    int amount;
                    if (args[4].matches("-?\\d+(\\.\\d+)?")) {
                        try {
                            amount = Integer.parseInt(args[4]);
                        } catch (NumberFormatException nfe) {
                            amount = 1;
                        }
                    } else {
                        amount = 1;
                    }
                    amount = Math.max(1, amount); // Ensure amount > 0
                    amount = Math.min(127, amount); // Ensure amount <= 127
                    ItemStack item;
                    if (getPlugin().getConfig().getBoolean("use-bucket")) {
                        item = XMaterial.LAVA_BUCKET.parseItem();
                    } else {
                        item = XMaterial.matchXMaterial(args[3].toUpperCase()).get().parseItem();
                    }
                    if (item == null) {
                        sender.sendMessage(Message.PREFIX.getMessage() + Message.ERROR_ITEM_PARSE_FAILED.getMessage().replace("%material%", args[3].toUpperCase()));
                        return false;
                    }
                    int data = 0;
                    if (ItemUtils.hasKey(item, "MATERIALDATA")) {
                        data = ItemUtils.getKeyInt(item, "MATERIALDATA");
                    }
                    String type = args[2].substring(0, 1).toUpperCase() + args[2].substring(1).toLowerCase();
                    item = ItemUtils.createItem(item, getPlugin().getConfig(), args[2].toUpperCase() + "." + args[3].toUpperCase(), type, data);

                    if (!getPlugin().getConfig().getBoolean("use-bucket")) {
                        item.setAmount(amount);
                    }
                    target.getInventory().addItem(item);
                    return false;
                } else {
                    sender.sendMessage(Message.PREFIX.getMessage() + Message.ERROR_ITEM_PARSE_FAILED.getMessage().replace("%material%", args[3].toUpperCase()));
                    return false;
                }
            }
        }
        sender.sendMessage(ChatUtils.color(Message.PREFIX.getMessage() + Message.CMD_USAGE.getMessage()
                .replace("%command%", "/gen give")
                .replace("%args%", "[Player] [GenType] [Material] [Amount]")));
        return false;
    }


    @Override
    public String getDescription() {
        return Message.CMD_GIVE_DESC.getMessage();
    }

    @Override
    public String getPermission() {
        return "genbucket.command.give";
    }
}
