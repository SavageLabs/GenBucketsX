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
        getPlugin().reloadConfig();
        getPlugin().getReplacements().clear();
        getPlugin().generationShopGUI = new GenerationShopGUI(getPlugin());
        getPlugin().getConfig().getStringList("replace-blocks").forEach(s -> getPlugin().replaceBlocksWhiteList.add(XMaterial.valueOf(s).parseMaterial()));
        getPlugin().replaceLiquids = getPlugin().getConfig().getBoolean("replace-liquids", false);
        getPlugin().getFileManager().getFileMap().get("messages").init();
        commandSender.sendMessage(ChatUtils.color(Message.PLUGIN_RELOAD.getMessage()));
        return false;
    }

    @Override
    public String getDescription() {
        return Message.CMD_RELOAD_DESC.getMessage();
    }

    @Override
    public String getPermission() {
        return "genbucket.command.admin";
    }

}
