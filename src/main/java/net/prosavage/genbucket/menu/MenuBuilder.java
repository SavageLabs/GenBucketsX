package net.prosavage.genbucket.menu;

import net.prosavage.genbucket.GenBucket;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public abstract class MenuBuilder<T> implements InventoryHolder {

    private String title;
    private int rows;
    private Inventory inventory;
    private GenBucket plugin;

    public MenuBuilder(GenBucket plugin, String title, int rows) {
        this.plugin = plugin;
        this.title = title;
        this.rows = rows;
        this.inventory = Bukkit.createInventory(this, 9 * rows, ChatColor.translateAlternateColorCodes('&', title));
    }

    public String getTitle() {
        return ChatColor.translateAlternateColorCodes('&', title);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public GenBucket getPlugin() {
        return plugin;
    }

    public abstract T init();

    @Override
    public Inventory getInventory() {
        return this.inventory;
    }

}
