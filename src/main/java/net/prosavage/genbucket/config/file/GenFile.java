package net.prosavage.genbucket.config.file;

import net.prosavage.genbucket.GenBucket;
import net.prosavage.genbucket.gen.GenData;
import pro.dracarys.configlib.config.CustomFile;

public class GenFile extends CustomFile {

    public GenFile() {
        super("");
    }

    @Override
    public GenFile init() {
        if (!getConfig().isConfigurationSection("GenBuckets"))
            GenBucket.get().saveResource(getName() + ".yml", true);
        reloadConfig();
        GenBucket.genDataMap.clear();
        for (String genID : this.getConfig().getConfigurationSection("GenBuckets").getKeys(false)) {
            try {
                String direction = this.getConfig().getString("GenBuckets." + genID + ".direction");
                if (direction == null) direction = "vertical";
                String material = this.getConfig().getString("GenBuckets." + genID + ".material");
                String guiMaterial = this.getConfig().getString("GenBuckets." + genID + ".gui-material");
                String givenMaterial = this.getConfig().getString("GenBuckets." + genID + ".given-material");
                if (guiMaterial == null) guiMaterial = material;
                if (givenMaterial == null) givenMaterial = material;
                String name = this.getConfig().getString("GenBuckets." + genID + ".name", "GenBucket");
                boolean pseudo = this.getConfig().getBoolean("GenBuckets." + genID + ".pseudo");
                int slot = this.getConfig().getInt("GenBuckets." + genID + ".slot");
                int amount = Math.max(this.getConfig().getInt("GenBuckets." + genID + ".amount"), 1);
                double price = this.getConfig().getInt("GenBuckets." + genID + ".price");
                int distance = this.getConfig().getInt("GenBuckets." + genID + ".distance");
                boolean consumable = this.getConfig().getBoolean("GenBuckets." + genID + ".consumable");
                boolean glow = this.getConfig().getBoolean("GenBuckets." + genID + ".glow");
                int delay = this.getConfig().getInt("GenBuckets." + genID + ".delay");
                GenBucket.genDataMap.put(genID.toLowerCase(), new GenData(genID.toLowerCase(), direction, material, guiMaterial, givenMaterial, name, pseudo, slot, amount, price, consumable, distance, glow, delay));
            } catch (NullPointerException npe) {
                npe.printStackTrace();
            }
        }
        return this;
    }

    @Override
    public String getName() {
        return "genbuckets";
    }
}
