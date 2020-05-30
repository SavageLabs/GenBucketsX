package net.prosavage.genbucket;

import com.tchristofferson.configupdater.ConfigUpdater;
import net.milkbowl.vault.economy.Economy;
import net.prosavage.genbucket.command.GenBucketCommand;
import net.prosavage.genbucket.file.FileManager;
import net.prosavage.genbucket.file.impl.DataFile;
import net.prosavage.genbucket.hooks.HookManager;
import net.prosavage.genbucket.hooks.impl.WorldGuard;
import net.prosavage.genbucket.menu.impl.GenerationShopGUI;
import net.prosavage.genbucket.utils.ChatUtils;
import net.prosavage.genbucket.utils.ItemUtils;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

public class GenBucket extends JavaPlugin {

    public static Economy econ;
    private static GenBucket instance;

    public int taskID;

    public GenerationShopGUI generationShopGUI;

    public Set<Material> replaceBlocksWhiteList = new HashSet<>();
    public boolean replaceLiquids = false;

    private HookManager hookManager;
    private FileManager fileManager;

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
        saveDefaultConfig();
        File configFile = new File(getDataFolder(), "config.yml");
        try {
            ConfigUpdater.update(instance, "config.yml", configFile, Arrays.asList("VERTICAL", "HORIZONTAL", "generation-shop"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        checkServerVersion();
        this.getCommand("genbucket").setExecutor(new GenBucketCommand(this));
        Bukkit.getScheduler().runTaskLater(this, () -> {
            this.hookManager = new HookManager(this);
            if (Bukkit.getPluginManager().isPluginEnabled("WorldGuard")) {
                hook_WG = true;
                ChatUtils.debug("WorldGuard found, enabling hook...");
                wg = new WorldGuard();
            }
            this.fileManager = new FileManager(this);
            loadConfig();
            getServer().getPluginManager().registerEvents(new GenListener(this), this);
        }, 2);
    }

    public void loadConfig() {
        reloadConfig();
        getFileManager().getFileMap().get("messages").init();
        replaceBlocksWhiteList.clear();
        getConfig().getStringList("replace-blocks").forEach(s -> {
            replaceBlocksWhiteList.add(ItemUtils.parseMaterial(s));
        });
        replaceLiquids = getConfig().getBoolean("replace-liquids", false);
        this.generationShopGUI = new GenerationShopGUI(this);
    }

    @Override
    public void onDisable() {
        DataFile dataFile = (DataFile) this.fileManager.getFileMap().get("data");
        dataFile.saveGenBuckets();
        getServer().getScheduler().cancelTasks(this);
        instance = null;
    }

    public void start() {
        taskID = getServer().getScheduler().scheduleSyncRepeatingTask(this, new GenListener(this), 0L, getConfig().getInt("delay"));
    }

    public void stop() {
        getServer().getScheduler().cancelTask(taskID);
    }

    public FileManager getFileManager() {
        return fileManager;
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
        return getConfig().getBoolean("worldguard-check") && hook_WG;
    }

}