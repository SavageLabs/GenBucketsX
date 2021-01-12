package net.prosavage.genbucket.file.impl;

import net.prosavage.genbucket.GenBucket;
import net.prosavage.genbucket.file.CustomFile;
import net.prosavage.genbucket.gen.GenData;
import org.bukkit.plugin.java.JavaPlugin;

public class GenFile extends CustomFile {

    public GenFile(JavaPlugin instance) {
        super(instance, "");
    }

    @Override
    public GenFile init() {
        if (!this.getConfig().isConfigurationSection("GenBuckets"))
            GenBucket.get().saveResource(getName() + ".yml", true);
        this.reloadConfig();
        GenBucket.genDataMap.clear();
        for (String genID : this.getConfig().getConfigurationSection("GenBuckets").getKeys(false)) {
            String direction = this.getConfig().getString("GenBuckets." + genID + ".direction");
            String material = this.getConfig().getString("GenBuckets." + genID + ".material");
            String name = this.getConfig().getString("GenBuckets." + genID + ".name", "GenBucket");
            boolean pseudo = this.getConfig().getBoolean("GenBuckets." + genID + ".pseudo");
            int slot = this.getConfig().getInt("GenBuckets." + genID + ".slot");
            int amount = Math.max(this.getConfig().getInt("GenBuckets." + genID + ".amount"), 1);
            double price = this.getConfig().getInt("GenBuckets." + genID + ".price");
            int distance = this.getConfig().getInt("GenBuckets." + genID + ".distance");
            boolean consumable = this.getConfig().getBoolean("GenBuckets." + genID + ".consumable");
            boolean glow = this.getConfig().getBoolean("GenBuckets." + genID + ".glow");
            GenBucket.genDataMap.put(genID.toLowerCase(), new GenData(genID.toLowerCase(), direction, material, name, pseudo, slot, amount, price, consumable, distance, glow));
        }
        return this;
    }

    @Override
    public String getName() {
        return "genbuckets";
    }
}
