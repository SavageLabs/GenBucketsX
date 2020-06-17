package net.prosavage.genbucket.file;

import net.prosavage.genbucket.file.impl.ConfigFile;
import net.prosavage.genbucket.file.impl.GenFile;
import net.prosavage.genbucket.file.impl.MessageFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class ConfigManager {

    private Map<String, CustomFile> fileMap = new HashMap<>();

    public ConfigManager(JavaPlugin plugin) {
        addFile(new MessageFile(plugin));
        addFile(new ConfigFile(plugin));
        addFile(new GenFile(plugin));
    }

    private void addFile(CustomFile file) {
        fileMap.put(file.getName(), file);
        file.init();
    }

    public Map<String, CustomFile> getFileMap() {
        return fileMap;
    }

}