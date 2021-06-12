package net.prosavage.genbucket.config;

import net.prosavage.genbucket.utils.ChatUtils;

import java.util.Arrays;
import java.util.List;

public enum Message {

    PREFIX("General.prefix", "&8[&4S&eGenBuckets&8] &f"),
    NO_PERMISSION("General.no-permission", "&cYou do not have permission."),

    GEN_BLOCKED_ACTION("GenBuckets.blocked-action", "&cYou may not shift click or place the bucket in this slot!"),
    GEN_CANCELLED("GenBuckets.generation-cancelled", "&cYou have cancelled your generation."),
    GEN_HAS_ALREADY("GenBuckets.already-have-bucket", "&cYou already have this gen bucket!"),
    GEN_CANT_PLACE("GenBuckets.cant-place-here", "&cYou can't place gen buckets here!"),
    GEN_CANT_PLACE_WORLD("GenBuckets.cant-place-in-this-world", "&cYou can't place gen buckets in this world!"),
    GEN_CANT_AFFORD("GenBuckets.cant-afford", "&cYou do not have sufficient funds."),
    GEN_CHARGED("GenBuckets.you-have-been-charged", "&cYou have been charged $%amount%"),
    GEN_ENEMY_NEARBY("GenBuckets.enemy-nearby", "&cYou cannot place gen buckets with enemies nearby!"),
    GEN_LIQUID_DISABLED("GenBuckets.liquid-buckets-disabled", "&cLiquid buckets are disabled, as the water flow causes lag. This can be bypassed in config.yml, option liquid-blocks."),

    GENTYPE_HORIZONTAL("GenType.horizontal", "Horizontal"),
    GENTYPE_VERTICAL("GenType.vertical", "Vertical"),

    ERROR_HOOK_NOTFOUND("Error.hook-not-found", "&cCould not hook to %plugin%, skipping..."),
    ERROR_ITEM_PARSE_FAILED("Error.failed-to-parse-item", "&cCould not parse ItemStack for Material %material%, aborting..."),
    ERROR_INVALID_PLAYER("Error.failed-to-parse-player", "&cCould not find a Player named %player%, aborting..."),

    PLAYER_REQUIRED("Command.player-required", "&cYou need to be a player to run this command!"),
    CMD_USAGE("Command.usage-format", "&cUsage: &e%command% &6%args%"),
    CMD_HELP_HEADER("Command.help-header", "&8=====[ &4Savage&eGenbuckets&8 ]=====[ v.&7%version%&8 ]====="),
    CMD_HELP_FORMAT("Command.help-format", " &e%command% &6%args% &8> &7%description%"),
    CMD_HELP_DESC("Command.help-description", "Basic help command."),
    CMD_GIVE_DESC("Command.give-description", "Gives the player a GenBucket."),
    CMD_GIVE_INVALID_ID("Command.give-invalid-id", "&cIdentifier for the GenBucket not recognized!"),
    CMD_MAIN_DESC("Command.main-description", "Displays GenBucket menu."),
    CMD_RELOAD_SUCCESS("Command.reload-success", "&aConfiguration files reloaded!"),
    CMD_RELOAD_DESC("Command.reload-description", "Reloads Config and Messages.");

    String config, message;
    String[] messages;

    Message(String config, String message) {
        this.config = config;
        this.message = message;
    }

    Message(String config, String[] messages) {
        this.config = config;
        this.messages = messages;
    }

    public String getConfig() {
        return config;
    }

    public String getMessage() {
        return message;
    }

    public String[] getMessages() {
        return this.messages;
    }

    public List<String> getStringList() {
        return Arrays.asList(this.messages);
    }

    public void setMessages(List<String> list) {
        this.messages = list.toArray(ChatUtils.color(new String[0]));
    }

    public void setMessage(String message) {
        this.message = ChatUtils.color(message);
    }

}