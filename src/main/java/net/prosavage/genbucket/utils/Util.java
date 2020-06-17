package net.prosavage.genbucket.utils;

import net.prosavage.genbucket.config.Config;

import java.util.Arrays;

public class Util {

    private Util() {
        throw new AssertionError("Instantiating utility class.");
    }

    public static boolean isEnabledWorld(String worldName) {
        boolean output = Arrays.stream(Config.ENABLED_WORLDS.getStrings()).anyMatch(worldName::equalsIgnoreCase);
        if (Config.ENABLED_WORLDS_ASBLACKLIST.getOption()) return !output;
        return output;
    }
}
