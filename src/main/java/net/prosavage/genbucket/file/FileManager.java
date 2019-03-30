package net.prosavage.genbucket.file;

import net.prosavage.genbucket.GenBucket;
import net.prosavage.genbucket.file.impl.DataFile;
import net.prosavage.genbucket.file.impl.MessageFile;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class FileManager {

    private GenBucket plugin;
    private Map<String, CustomFile> fileMap = new HashMap<>();

    public FileManager(GenBucket plugin) {
        this.plugin = plugin;
        addFile(new MessageFile(plugin));
        addFile(new DataFile(plugin));
    }

    private void addFile(CustomFile file) {
        fileMap.put(file.getName(), file);
        plugin.getLogger().log(Level.INFO, file.getName() + ".yml has initialized.");
        file.init();
    }

    public Map<String, CustomFile> getFileMap() {
        return fileMap;
    }

}