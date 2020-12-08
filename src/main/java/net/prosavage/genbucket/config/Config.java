package net.prosavage.genbucket.config;

import java.util.Arrays;
import java.util.List;

public enum Config {

    INFO("Info", new String[]{
            "# [GenBuckets] SpigotMC Page: https://www.spigotmc.org/resources/savagegenbuckets-1-8-1-15-the-ultimate-genbucket-plugin.63051\n" +
                    "\n" +
                    "# |----------|\n" +
                    "# |  F.A.Q.  |\n" +
                    "# |----------|\n" +
                    "#   > What are the permissions for this plugin?\n" +
                    "#     You can find (and also edit :D) all permissions in the Permissions section of this config.\n" +
                    "#   > My server has low specs and TPS drop too much, how to mitigate?\n" +
                    "#     You can mitigate TPS loss by using a higher 'ticks-between-block-generations' value and\n" +
                    "#     disabling liquids generation. Other heavy options are 'apply-facing', 'apply-blockdata' and 'allow-vert-down-gravity-block-place'\n" +
                    "\n"
    }),

    DEBUG("Settings.debug", false),

    USE_BUCKETS("Settings.genbuckets.use-buckets", true),
    ALLOW_LIQUIDS("Settings.genbuckets.allow-placing-liquid-blocks", false),
    REPLACE_LIQUIDS("Settings.genbuckets.replace-liquid-blocks", false),
    REPLACE_BLOCKS("Settings.genbuckets.replace-blocks", new String[] {
            "AIR",
            "TALL_GRASS",
            "LARGE_FERN",
            "FERN",
            "GRASS",
            "POPPY"
    }),
    USE_SOURCEBLOCK("Settings.genbuckets.source-block.enabled", true),
    USE_FACING("Settings.genbuckets.apply-facing", false),
    USE_OPPOSITE_FACING("Settings.genbuckets.invert-facing", false),
    USE_BLOCKDATA("Settings.genbuckets.apply-block-data", false),
    ALLOW_GRAVITY_DOWN("Settings.genbuckets.allow-vert-down-gravity-block-place", false),
    GENERATION_DELAY("Settings.genbuckets.ticks-between-block-generations", 15),
    USE_SPONGE_CHECK("Settings.genbuckets.cancel-generation-if-near-sponge", false),
    SOURCEBLOCK_MATERIAL("Settings.genbuckets.source-block.material", "WHITE_WOOL"),

    ENABLED_WORLDS("Settings.enabled-worlds.worlds", new String[]{
            "worldName",
            "worldName2"
    }),
    ENABLED_WORLDS_ASBLACKLIST("Settings.enabled-worlds.make-enabled-worlds-a-blacklist", true),

    GUI_ENABLED("Settings.gen-gui.enabled", true),
    GUI_SIZE("Settings.gen-gui.gui-size", 5),
    GUI_TITLE("Settings.gen-gui.gui-title", "&9Genbucket Shop"),
    GUI_ITEM_EMPTY("Settings.gen-gui.blank-item.material", "BLACK_STAINED_GLASS_PANE"),
    GUI_ITEM_EMPTY_GLOW("Settings.gen-gui.blank-item.glow", false),
    GUI_ITEM_EMPTY_NAME("Settings.gen-gui.blank-item.name", "&0"),
    GUI_ITEM_EMPTY_LORE("Settings.gen-gui.blank-item.lore", new String[]{
            "&7Click a GenBucket to buy!"
    }),
    GUI_ITEM_GEN_LORE("Settings.gen-gui.genbucket-item.lore", new String[]{
            "&7Type:&4 %type%",
            "&7Placement Price:&4 $%price%"
    }),

    HOOK_CANBUILD_CHECK("Hooks.General.use-canbuild-check", true),
    HOOK_DISABLE_WILD("Hooks.General.disable-genbuckets-in-wilderness", false),
    HOOK_VANILLA_BORDER("Hooks.General.use-vanilla-worldborder-check", true),
    HOOK_NEARBY_CHECK("Hooks.General.use-nearby-enemy-check", true),
    HOOK_NEARBY_RADIUS("Hooks.General.nearby-enemy-check-radius", 30),
    HOOK_WG_CHECK("Hooks.WorldGuard.disable-genbuckets-in-protected-regions", true),
    HOOK_BORDER_CHECK("Hooks.WorldBorder.disable-genbuckets-outside-worldborder", true),
    HOOK_FUUID_FORCESTANDARD("Hooks.FactionsUUID.force-standard-hook", false),

    PERMISSION_RELOAD("Permissions.perm-reload", "genbuckets.command.reload"),
    PERMISSION_GIVE("Permissions.perm-give", "genbuckets.command.give"),
    PERMISSION_HELP("Permissions.perm-help", "genbuckets.command.help"),
    PERMISSION_GUI("Permissions.perm-gui", "genbuckets.command.gui"),

    ECON_DISABLE_CHARGEDMSG("Economy.disable-gen-charged-message", false),
    ECON_USE_ACTIONBAR("Economy.use-actionbar-for-charged-message", false);

    String config, message;
    Boolean option;
    String[] messages;
    Integer number;
    Double dnumber;

    Config(String config, String message) {
        this.config = config;
        this.message = message;
    }

    Config(String config, String[] messages) {
        this.config = config;
        this.messages = messages;
    }

    Config(String config, Boolean option) {
        this.config = config;
        this.option = option;
    }

    Config(String config, Integer number) {
        this.config = config;
        this.number = number;
    }

    Config(String config, Double dnumber) {
        this.config = config;
        this.dnumber = dnumber;
    }

    public boolean getOption() {
        return option;
    }

    public String getConfig() {
        return config;
    }

    public String getString() {
        return message;
    }

    public Double getDouble() {
        return dnumber;
    }

    public Integer getInt() {
        return number;
    }

    public String[] getStrings() {
        return this.messages;
    }

    public List<String> getStringList() {
        return Arrays.asList(this.messages);
    }

    public void setInt(int number) {
        this.number = number;
    }

    public void setDouble(double dnumber) {
        this.dnumber = dnumber;
    }

    public void setStrings(List<String> list) {
        this.messages = list.toArray(new String[0]);
    }

    public void setString(String message) {
        this.message = message;
    }

    public void setOption(Boolean option) {
        this.option = option;
    }
}
