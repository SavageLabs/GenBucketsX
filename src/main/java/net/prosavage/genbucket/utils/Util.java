package net.prosavage.genbucket.utils;

import net.prosavage.genbucket.GenBucket;
import net.prosavage.genbucket.config.Config;

import java.text.NumberFormat;
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

    public static String formatPrice(Double price) {
        if (GenBucket.econ == null) return NumberFormat.getCurrencyInstance().format(price);
        return GenBucket.econ.format(price);
    }
}
