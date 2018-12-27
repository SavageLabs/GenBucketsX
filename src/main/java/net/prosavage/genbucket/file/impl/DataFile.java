package net.prosavage.genbucket.file.impl;

import java.util.ArrayList;
import java.util.Collection;

import net.prosavage.genbucket.GenBucket;
import net.prosavage.genbucket.GenListener;
import net.prosavage.genbucket.file.CustomFile;
import net.prosavage.genbucket.utils.Logger;
import net.prosavage.genbucket.gen.GenType;
import net.prosavage.genbucket.gen.Generator;
import net.prosavage.genbucket.gen.impl.HorizontalGen;
import net.prosavage.genbucket.gen.impl.VerticalGen;
import org.bukkit.Bukkit;

public class DataFile extends CustomFile {

	private Collection<String> vertical = new ArrayList<String>();
	private Collection<String> horizontal = new ArrayList<String>();

	public DataFile() {
		super(GenBucket.get(), "data", "generations");
		getConfig().addDefault("gen-vertical", vertical);
		getConfig().addDefault("gen-horizontal", horizontal);
		getConfig().options().copyDefaults(true);
		saveConfig();
	}

	public void init() {
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
			Logger.print(GenListener.generations.size() + " Generations have been continued", Logger.PrefixType.DEFAULT);
			getConfigFile().delete();
		}, 100L);
	}

	@Override
	public void onExit() {
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

}
