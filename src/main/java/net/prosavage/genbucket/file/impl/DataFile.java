package net.prosavage.genbucket.file.impl;

import net.prosavage.genbucket.GenBucket;
import net.prosavage.genbucket.GenListener;
import net.prosavage.genbucket.file.CustomFile;
import net.prosavage.genbucket.gen.GenType;
import net.prosavage.genbucket.gen.Generator;
import net.prosavage.genbucket.gen.impl.HorizontalGen;
import net.prosavage.genbucket.gen.impl.VerticalGen;
import net.prosavage.genbucket.utils.ChatUtils;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.Collection;

public class DataFile extends CustomFile {

    private Collection<String> vertical = new ArrayList<>();
    private Collection<String> horizontal = new ArrayList<>();

    public DataFile(GenBucket plugin) {
        super(plugin, "data");
        getConfig().addDefault("gen-vertical", vertical);
        getConfig().addDefault("gen-horizontal", horizontal);
        getConfig().options().copyDefaults(true);
        saveConfig();
    }

    public DataFile init() {
        Bukkit.getScheduler().scheduleSyncDelayedTask(GenBucket.get(), () -> {
            for (String string : getConfig().getStringList("gen-vertical")) {
                VerticalGen verticalGen = new VerticalGen(string);
                GenListener.generations.add(verticalGen);
            }

            for (String string : getConfig().getStringList("gen-horizontal")) {
                HorizontalGen horizontalGen = new HorizontalGen(string);
                GenListener.generations.add(horizontalGen);
            }

            if (GenListener.generations.size() != 0) {
                GenBucket.get().start();
            }
            vertical.clear();
            horizontal.clear();
            ChatUtils.sendConsole(GenListener.generations.size() + " Generations have been continued");
            getConfigFile().delete();
        }, 100L);
        return this;
    }


    public void saveGenBuckets() {
        for (Generator generation : GenListener.generations) {
            if (generation.getType() == GenType.VERTICAL) {
                vertical.add(generation.toString());
            } else {
                horizontal.add(generation.toString());
            }
        }
        getConfig().set("gen-vertical", vertical);
        getConfig().set("gen-horizontal", horizontal);
        saveConfig();
    }

    @Override
    public String getName() {
        return "data";
    }

}
