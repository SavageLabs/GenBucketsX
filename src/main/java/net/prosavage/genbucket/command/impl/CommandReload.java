package net.prosavage.genbucket.command.impl;

import com.cryptomorin.xseries.XMaterial;
import net.prosavage.genbucket.GenBucket;
import net.prosavage.genbucket.command.AbstractCommand;
import net.prosavage.genbucket.menu.impl.GenerationShopGUI;
import net.prosavage.genbucket.utils.ChatUtils;
import net.prosavage.genbucket.utils.Message;
import org.bukkit.command.CommandSender;

public class CommandReload extends AbstractCommand {

    public CommandReload(GenBucket plugin) {
        super(plugin, "reload", false);
        this.alias.add("r");
    }

    @Override
    public boolean execute(CommandSender commandSender, String[] args) {
        getPlugin().loadConfig();
        commandSender.sendMessage(ChatUtils.color(Message.PLUGIN_RELOAD.getMessage()));
        return false;
    }

    @Override
    public String getDescription() {
        return Message.CMD_RELOAD_DESC.getMessage();
    }

    @Override
    public String getPermission() {
        return "genbucket.command.reload";
    }

}
