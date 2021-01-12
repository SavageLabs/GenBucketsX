package net.prosavage.genbucket;

import net.milkbowl.vault.economy.Economy;
import net.prosavage.genbucket.command.GenBucketCommand;
import net.prosavage.genbucket.config.Config;
import net.prosavage.genbucket.config.file.ConfigFile;
import net.prosavage.genbucket.config.file.GenFile;
import net.prosavage.genbucket.config.file.MessageFile;
import net.prosavage.genbucket.gen.GenData;
import net.prosavage.genbucket.hooks.HookManager;
import net.prosavage.genbucket.hooks.impl.WorldGuard;
import net.prosavage.genbucket.menu.impl.GenerationShopGUI;
import net.prosavage.genbucket.utils.ChatUtils;
import net.prosavage.genbucket.utils.ItemUtils;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;
import pro.dracarys.configlib.ConfigLib;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

public class GenBucket extends JavaPlugin {

    public static Economy econ;
    private static GenBucket instance;

    public GenerationShopGUI generationShopGUI;

    public Set<Material> replaceBlocksWhiteList = new HashSet<>();

    private HookManager hookManager;

    public static Map<String, GenData> genDataMap = new HashMap<>();

    public static GenBucket get() {
        return instance;
    }

    @Override
    public void onEnable() {
        try {
            int pluginId = 7225;
            new Metrics(this, pluginId);
        } catch (Exception ex) {
            getServer().getLogger().log(Level.SEVERE, "Error while trying to register Metrics (bStats)");
        }
        GenBucket.instance = this;
        checkServerVersion();
        this.getCommand("genbucket").setExecutor(new GenBucketCommand(this));
        Bukkit.getScheduler().runTaskLater(this, () -> {
            initConfig();
            loadConfig();
            this.hookManager = new HookManager(this);
            if (Bukkit.getPluginManager().isPluginEnabled("WorldGuard")) {
                hook_WG = true;
                ChatUtils.debug("WorldGuard found, enabling hook...");
                wg = new WorldGuard();
            }
            getServer().getPluginManager().registerEvents(new GenListener(), this);
        }, 2);
    }

    public void initConfig() {
        ConfigLib.setPlugin(this);
        ConfigLib.addFile(new ConfigFile());
        ConfigLib.addFile(new MessageFile());
        ConfigLib.addFile(new GenFile());
    }

    public void loadConfig() {
        ConfigLib.initAll();
        replaceBlocksWhiteList.clear();
        Config.REPLACE_BLOCKS.getStringList().forEach(s -> replaceBlocksWhiteList.add(ItemUtils.parseMaterial(s)));
        this.generationShopGUI = new GenerationShopGUI(this);
    }

    @Override
    public void onDisable() {
        getServer().getScheduler().cancelTasks(this);
        instance = null;
    }

    public HookManager getHookManager() {
        return hookManager;
    }

    public Set<Material> getReplacements() {
        return replaceBlocksWhiteList;
    }

    private static int ver;

    private void checkServerVersion() {
        ver = Integer.parseInt(Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3].replace("1_", "").substring(1).replaceAll("_R\\d", ""));
    }

    public static int getServerVersion() {
        return ver;
    }

    public WorldGuard getWorldGuard() {
        return this.wg;
    }

    private static boolean hook_WG = false;
    private WorldGuard wg;

    public boolean hasWorldGuard() {
        return Config.HOOK_WG_CHECK.getOption() && hook_WG;
    }

}