package net.prosavage.genbucket;

import com.google.common.collect.ImmutableList;
import net.prosavage.genbucket.api.PlayerGenEvent;
import net.prosavage.genbucket.gen.GenType;
import net.prosavage.genbucket.gen.Generator;
import net.prosavage.genbucket.gen.impl.HorizontalGen;
import net.prosavage.genbucket.gen.impl.VerticalGen;
import net.prosavage.genbucket.hooks.impl.FactionHook;
import net.prosavage.genbucket.hooks.impl.WorldGuardHook;
import net.prosavage.genbucket.utils.ItemUtils;
import net.prosavage.genbucket.utils.Message;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.bukkit.block.BlockFace.*;

public class GenListener implements Listener, Runnable {

    private static final List<BlockFace> DIRECTIONS = ImmutableList.of(NORTH, EAST, SOUTH, WEST);
    public static List<Generator> generations = new ArrayList<>();
    private GenBucket plugin;

    public GenListener(GenBucket plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEmptyBucket(PlayerBucketEmptyEvent event) {
        ItemStack item = getTool(event.getPlayer());
        if ((item.getType() == Material.LAVA_BUCKET || item.getType() == Material.WATER_BUCKET) && ItemUtils.hasKey(item, "GENBUCKET")) {
            event.setCancelled(true);
            Block block = event.getBlockClicked().getRelative(event.getBlockFace());
            Player player = event.getPlayer();
            String name = ItemUtils.getKeyString(item, "GENBUCKET");
            Material mat = Material.valueOf(ItemUtils.getKeyString(item, "MATERIAL"));

            FactionHook facHook = ((FactionHook) plugin.getHookManager().getPluginMap().get("Factions"));
            if (plugin.getHookManager().getPluginMap().get("WorldGuard") != null) {
                WorldGuardHook wgHook = ((WorldGuardHook) plugin.getHookManager().getPluginMap().get("WorldGuard"));
                if (!wgHook.canBuild(player, block)) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', Message.GEN_CANCELLED.getMessage()));
                    return;
                }
            }

            if (facHook.canBuild(block, player) && !facHook.hasNearbyPlayer(player)) {
                if (name.contains("VERTICAL") && withdraw(name + "." + mat.name(), player)) {
                    register(new VerticalGen(plugin, player, mat, block));
                    Bukkit.getServer().getPluginManager().callEvent(new PlayerGenEvent(player, mat, block.getLocation(), GenType.VERTICAL));
                } else if (name.contains("HORIZONTAL") && DIRECTIONS.contains(event.getBlockFace()) && withdraw(name + "." + mat.name(), player)) {
                    register(new HorizontalGen(plugin, player, mat, block, event.getBlockFace()));
                    Bukkit.getServer().getPluginManager().callEvent(new PlayerGenEvent(player, mat, block.getLocation(), GenType.HORIZONTAL));
                }
            }

        }
    }

    @EventHandler
    public void inventoryClick(InventoryClickEvent event) {
        if (event.getView() == null) return;
        if (event.getView().getTitle().equals(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("generation-shop.name"))))
            return;
        ItemStack item = event.getCursor();
        if (item.getType() == Material.AIR && event.getClick().isShiftClick()) item = event.getCurrentItem();
        Player player = (Player) event.getWhoClicked();

        if (item.hasItemMeta() && player.getOpenInventory().getType().equals(InventoryType.FURNACE) && ItemUtils.hasKey(item, "GENBUCKET") && event.getClick().isShiftClick()) {
            event.setCancelled(true);
            player.sendMessage(Message.GEN_BLOCKED_ACTION.getMessage());
            player.closeInventory();
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlaceBlock(PlayerInteractEvent event) {
        ItemStack item = getTool(event.getPlayer());
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && !plugin.getConfig().getBoolean("use-bucket")) {
            if (item.hasItemMeta() && ItemUtils.hasKey(item, "GENBUCKET")) {
                event.setCancelled(true);

                Block block = event.getClickedBlock().getRelative(event.getBlockFace());
                Player player = event.getPlayer();
                String name = ItemUtils.getKeyString(item, "GENBUCKET");
                Material mat = Material.valueOf(ItemUtils.getKeyString(item, "MATERIAL"));

                FactionHook facHook = ((FactionHook) plugin.getHookManager().getPluginMap().get("Factions"));
                if (plugin.getHookManager().getPluginMap().get("WorldGuard") != null) {
                    WorldGuardHook wgHook = ((WorldGuardHook) plugin.getHookManager().getPluginMap().get("WorldGuard"));
                    if (!wgHook.canBuild(player, block)) {
                        event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', Message.GEN_CANCELLED.getMessage()));
                        return;
                    }
                }
                if (facHook.canBuild(block, player) && !facHook.hasNearbyPlayer(player)) {
                    if (name.contains("VERTICAL") && withdraw(name + "." + mat.name(), event.getPlayer())) {
                        register(new VerticalGen(plugin, event.getPlayer(), mat, block));
                        Bukkit.getServer().getPluginManager().callEvent(new PlayerGenEvent(player, mat, block.getLocation(), GenType.VERTICAL));
                    } else if (name.contains("HORIZONTAL") && DIRECTIONS.contains(event.getBlockFace()) && withdraw(name + "." + mat.name(), event.getPlayer())) {
                        register(new HorizontalGen(plugin, event.getPlayer(), mat, block, event.getBlockFace()));
                        Bukkit.getServer().getPluginManager().callEvent(new PlayerGenEvent(player, mat, block.getLocation(), GenType.HORIZONTAL));

                    }
                }
            }
        }
    }

    @EventHandler
    public void onDropItem(PlayerDropItemEvent event) {
        ItemStack item = event.getItemDrop().getItemStack();
        if (ItemUtils.hasKey(item, "GENBUCKET")) {
            event.getItemDrop().remove();
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (event.getView().getTitle().equals(plugin.generationShopGUI.getTitle()) && event.getView().getTopInventory() != player.getInventory()) {
            if (event.getCurrentItem() != null && event.getView().getTopInventory() != null && event.getCurrentItem().getType() != Material.AIR && ItemUtils.hasKey(event.getCurrentItem(), "GENBUCKET")) {
                ItemStack item = event.getCurrentItem().clone();
                if (item != null && item.getType() != Material.LAVA_BUCKET) {
                    if (plugin.getConfig().getBoolean("use-bucket")) {
                        item.setType(Material.LAVA_BUCKET);
                    } else {
                        item.setAmount(64);
                    }
                }
                if (!player.getInventory().contains(item)) {
                    player.getInventory().addItem(item);
                } else {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', Message.GEN_HAS_ALREADY.getMessage()));
                }
            }
            event.setCancelled(true);
        }
    }

    public void run() {
        if (!generations.isEmpty()) {
            for (Iterator<Generator> iterator = generations.iterator(); iterator.hasNext(); ) {
                Generator generator = iterator.next();
                if (generator.isFinished()) {
                    iterator.remove();
                    continue;
                }
                generator.run();
            }
        } else {
            GenBucket.get().stop();
            generations.clear();
        }
    }

    public void register(Generator generator) {
        if (generations.isEmpty()) {
            GenBucket.get().start();
        }
        generations.add(generator);
    }

    public boolean withdraw(String type, Player player) {
        int price = plugin.getConfig().getInt(type + ".price");
        if (GenBucket.econ.withdrawPlayer(player, price).transactionSuccess()) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', Message.GEN_CHARGED.getMessage().replace("{amount}", price + "")));
            return true;
        }
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', Message.GEN_CANT_AFFORD.getMessage()));
        return false;
    }

    @SuppressWarnings("deprecation")
    public static ItemStack getTool(Player player) {
        if (GenBucket.getServerVersion() <= 8) {
            return player.getInventory().getItemInHand();
        } else {
            return player.getInventory().getItemInMainHand();
        }
    }

}
