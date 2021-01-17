package net.prosavage.genbucket.command.impl;

import net.prosavage.genbucket.GenBucket;
import net.prosavage.genbucket.api.GenBucketsXAPI;
import net.prosavage.genbucket.command.AbstractCommand;
import net.prosavage.genbucket.config.Config;
import net.prosavage.genbucket.config.Message;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class CommandMain extends AbstractCommand {

    public CommandMain(GenBucket plugin) {
        super(plugin, "menu", true);
    }

    @Override
    public boolean execute(CommandSender commandSender, String[] args) {
        Player player = (Player) commandSender;
        GenBucketsXAPI.openGenShopGUI(player);
        return false;
    }

    @Override
    public String getDescription() {
        return Message.CMD_MAIN_DESC.getMessage();
    }

    @Override
    public String getPermission() {
        return Config.PERMISSION_GUI.getString();
    }

}
