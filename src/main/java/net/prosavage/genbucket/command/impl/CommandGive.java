package net.prosavage.genbucket.command.impl;

import com.cryptomorin.xseries.XMaterial;
import net.prosavage.genbucket.GenBucket;
import net.prosavage.genbucket.command.AbstractCommand;
import net.prosavage.genbucket.utils.ChatUtils;
import net.prosavage.genbucket.utils.ItemUtils;
import net.prosavage.genbucket.utils.Message;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
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
                if (Bukkit.getPlayer(args[1]) != null && XMaterial.matchXMaterial(args[3].toUpperCase()).isPresent()) {
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
                    ItemStack item = XMaterial.matchXMaterial(args[3].toUpperCase()).get().parseItem();
                    String type = args[2].substring(0, 1).toUpperCase() + args[2].substring(1).toLowerCase();
                    item = ItemUtils.createItem(item, getPlugin().getConfig(), args[2].toUpperCase() + "." + args[3].toUpperCase(), type);

                    if (getPlugin().getConfig().getBoolean("use-bucket")) {
                        item.setType(Material.LAVA_BUCKET);
                    } else {
                        item.setAmount(amount);
                    }

                    Bukkit.getPlayer(args[1]).getInventory().addItem(item);
                    return false;
                }
            }

        }
        sender.sendMessage(ChatUtils.color("&cUsage: &e/gen give &6[Player] [GenType] [Material] [Amount]"));
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
