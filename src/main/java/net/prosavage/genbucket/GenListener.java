package net.prosavage.genbucket;

import com.cryptomorin.xseries.XMaterial;
import net.prosavage.genbucket.api.PlayerGenEvent;
import net.prosavage.genbucket.api.PlayerPlaceGenEvent;
import net.prosavage.genbucket.config.Config;
import net.prosavage.genbucket.config.Message;
import net.prosavage.genbucket.gen.GenData;
import net.prosavage.genbucket.gen.GenType;
import net.prosavage.genbucket.gen.Generator;
import net.prosavage.genbucket.gen.impl.HorizontalGen;
import net.prosavage.genbucket.gen.impl.VerticalGen;
import net.prosavage.genbucket.hooks.impl.FactionHook;
import net.prosavage.genbucket.utils.ChatUtils;
import net.prosavage.genbucket.utils.ItemUtils;
import net.prosavage.genbucket.utils.Util;
import org.bukkit.Bukkit;
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
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GenListener implements Listener, Runnable {

    public static List<Generator> generations = new ArrayList<>();
    private GenBucket plugin;

    public GenListener(GenBucket plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEmptyBucket(PlayerBucketEmptyEvent event) {
        if (!Config.USE_BUCKETS.getOption()) return;
        ItemStack item = getTool(event.getPlayer());
        if ((item.getType() == XMaterial.LAVA_BUCKET.parseMaterial() || item.getType() == XMaterial.WATER_BUCKET.parseMaterial())
                && ItemUtils.hasKey(item, "GENBUCKET-ID")) {
            ChatUtils.debug("key check passed");
            event.setCancelled(true);
            Block block = event.getBlockClicked().getRelative(event.getBlockFace());
            gen(event.getPlayer(), item, block, event.getBlockFace());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlaceBlock(PlayerInteractEvent event) {
        if (Config.USE_BUCKETS.getOption()) return;
        ItemStack item = getTool(event.getPlayer());
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK
                && item.hasItemMeta() && ItemUtils.hasKey(item, "GENBUCKET-ID")) {
            Block block = event.getClickedBlock().getRelative(event.getBlockFace());
            gen(event.getPlayer(), item, block, event.getBlockFace());
        }
    }

    @EventHandler
    public void onDropItem(PlayerDropItemEvent event) {
        if (ItemUtils.hasKey(event.getItemDrop().getItemStack(), "GENBUCKET-ID"))
            event.getItemDrop().remove();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        try {
            if (event.getView() == null) return;
            if (event.getView().getTitle().equals(plugin.generationShopGUI.getTitle()) && event.getView().getTopInventory() != player.getInventory()) {
                event.setCancelled(true);
                if (event.getCurrentItem() != null
                        && event.getCurrentItem().getType() != XMaterial.AIR.parseMaterial()
                        && ItemUtils.hasKey(event.getCurrentItem(), "GENBUCKET-ID")) {

                    ItemStack item = event.getCurrentItem().clone();
                    GenData genData = GenBucket.genDataMap.get(ItemUtils.getKeyString(item, "GENBUCKET-ID").toLowerCase());
                    if ((item.getType() == XMaterial.WATER_BUCKET.parseMaterial() || item.getType() == XMaterial.LAVA_BUCKET.parseMaterial())
                            && !Config.ALLOW_LIQUIDS.getOption()) {
                        player.sendMessage(ChatUtils.color(Message.PREFIX.getMessage() + Message.GEN_LIQUID_DISABLED.getMessage()));
                        return;
                    }
                    if (item.getType() != XMaterial.LAVA_BUCKET.parseMaterial()) {
                        if (Config.USE_BUCKETS.getOption()) {
                            String name = item.getItemMeta().getDisplayName();
                            List<String> lore = item.getItemMeta().getLore();
                            item = XMaterial.LAVA_BUCKET.parseItem();
                            if (item != null && item.getItemMeta() != null) {
                                ItemMeta itmMeta = item.getItemMeta();
                                itmMeta.setDisplayName(name);
                                if (lore != null && !lore.isEmpty())
                                    itmMeta.setLore(lore);
                                item.setItemMeta(itmMeta);
                            }
                            item = ItemUtils.setKeyString(item, "GENBUCKET-ID", genData.getGenID());
                        } else {
                            item.setAmount(64);
                        }
                    }
                    if (!player.getInventory().contains(item)) {
                        player.getInventory().addItem(item);
                    } else {
                        player.sendMessage(Message.PREFIX.getMessage() + Message.GEN_HAS_ALREADY.getMessage());
                    }
                }
            }
            if (event.getView().getTitle().equals(ChatUtils.color(Config.GUI_TITLE.getString())))
                return;
            ItemStack item = event.getCursor();
            assert item != null;
            if (item.getType() == Material.AIR && event.getClick().isShiftClick()) item = event.getCurrentItem();
            if (item.hasItemMeta()
                    && player.getOpenInventory().getType().equals(InventoryType.FURNACE)
                    && ItemUtils.hasKey(item, "GENBUCKET-ID")
                    && event.getClick().isShiftClick()) {
                event.setCancelled(true);
                player.sendMessage(Message.PREFIX.getMessage() + Message.GEN_BLOCKED_ACTION.getMessage());
                player.closeInventory();
            }
        } catch (NullPointerException npe) {
            //ignored
            ChatUtils.debug("NPE on InventoryClick TITLE=" + plugin.generationShopGUI.getTitle());
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

    public boolean withdraw(GenData genData, Player player) {
        double price = genData.getPrice();
        if (GenBucket.econ == null) {
            player.sendMessage(ChatUtils.color(Message.PREFIX.getMessage() + "No Economy provider found! ex: Essentials, please install one.\n Automatically approving purchase for now..."));
            return true;
        }
        if (GenBucket.econ.withdrawPlayer(player, price).transactionSuccess()) {
            if (!Config.DISABLE_GEN_CHARGED_MESSAGE.getOption()) {
                player.sendMessage(ChatUtils.color(Message.PREFIX.getMessage() + Message.GEN_CHARGED.getMessage().replace("%amount%", price + "")));
            }
            return true;
        }
        player.sendMessage(ChatUtils.color(Message.PREFIX.getMessage() + Message.GEN_CANT_AFFORD.getMessage()));
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

    @SuppressWarnings("deprecation")
    public static void setTool(Player player, ItemStack item) {
        if (GenBucket.getServerVersion() <= 8) {
            player.getInventory().setItemInHand(item);
        } else {
            player.getInventory().setItemInMainHand(item);
        }
    }

    private void gen(Player player, ItemStack item, Block block, BlockFace blockFace) {
        if (!Util.isEnabledWorld(block.getWorld().getName())) {
            player.sendMessage(Message.PREFIX.getMessage() + Message.GEN_CANT_PLACE_WORLD.getMessage()
                    .replace("%world%", block.getWorld().getName()));
            if (player.isOp() || player.hasPermission("is.op"))
                player.sendMessage(Message.PREFIX.getMessage() + "&cHey, OP, if you want to enable this world for GenBuckets, look in the config for the enabled-worlds section!");
            return;
        }
        String genID = ItemUtils.getKeyString(item, "GENBUCKET-ID");
        if (!GenBucket.genDataMap.containsKey(genID)) {
            item.setType(Material.AIR);
            return;
        }
        GenData genData = GenBucket.genDataMap.get(genID);
        PlayerPlaceGenEvent placeGenEvent = new PlayerPlaceGenEvent(player, block, genData);
        Bukkit.getServer().getPluginManager().callEvent(placeGenEvent);
        if (placeGenEvent.isCancelled()) {
            ChatUtils.debug("PlayerPlaceGenEvent was cancelled by another plugin");
            return;
        }
        FactionHook facHook = ((FactionHook) plugin.getHookManager().getPluginMap().get("Factions"));
        if (Config.HOOK_WG_CHECK.getOption() && plugin.hasWorldGuard() && !plugin.getWorldGuard().hasBuildPermission(player, block)) {
            player.sendMessage(ChatUtils.color(Message.PREFIX.getMessage() + Message.GEN_CANCELLED.getMessage()));
            ChatUtils.debug("WorldGuard denied " + player.getName() + "from placing a genBucket at " + block.getLocation().toString());
            return;
        }
        if (!facHook.canBuild(block, player) || facHook.hasNearbyPlayer(player)) {
            ChatUtils.debug("Factions denied " + player.getName() + "from placing a genBucket at " + block.getLocation().toString());
            return;
        }
        if (!withdraw(genData, player)) return;
        if (genData.getType() == GenType.VERTICAL) {
            register(new VerticalGen(plugin, player, block, blockFace, genData));
        } else {
            register(new HorizontalGen(plugin, player, block, blockFace, genData));
        }
        Bukkit.getServer().getPluginManager().callEvent(new PlayerGenEvent(player, block, genData));
        if (genData.isConsumable())
            setTool(player, XMaterial.AIR.parseItem());
    }

}
