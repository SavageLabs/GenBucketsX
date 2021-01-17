package net.prosavage.genbucket;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.messages.ActionBar;
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
import net.prosavage.genbucket.tasks.GenTask;
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

public class GenListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEmptyBucket(PlayerBucketEmptyEvent event) {
        if (!Config.USE_BUCKETS.getOption()) return;
        ItemStack item = getTool(event.getPlayer());
        if ((item.getType() == XMaterial.LAVA_BUCKET.parseMaterial() || item.getType() == XMaterial.WATER_BUCKET.parseMaterial() || item.getType() == XMaterial.MILK_BUCKET.parseMaterial())
                && ItemUtils.hasKey(item, "GENBUCKET-ID")) {
            ChatUtils.debug("key check passed");
            event.setCancelled(true);
            Block block = event.getBlockClicked().getRelative(event.getBlockFace());
            if (!gen(event.getPlayer(), item, block, event.getBlockFace())) event.getPlayer().updateInventory();
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlaceBlock(PlayerInteractEvent event) {
        if (Config.USE_BUCKETS.getOption()) return;
        ItemStack item = getTool(event.getPlayer());
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK
                && item.hasItemMeta() && ItemUtils.hasKey(item, "GENBUCKET-ID")) {
            Block block = event.getClickedBlock().getRelative(event.getBlockFace());
            if (!gen(event.getPlayer(), item, block, event.getBlockFace())) {
                event.setCancelled(true);
                event.getPlayer().updateInventory();
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onDropItem(PlayerDropItemEvent event) {
        if (ItemUtils.hasKey(event.getItemDrop().getItemStack(), "GENBUCKET-ID")) {
            if (Config.PREVENT_DROP.getOption()) {
                event.setCancelled(true);
            }
            if (Config.REMOVE_ONDROP.getOption())
                event.getItemDrop().remove();
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        try {
            ItemStack item = event.getCursor();
            if (item != null) {
                if (item.getType() == Material.AIR && event.getClick().isShiftClick()) item = event.getCurrentItem();
                if (item != null
                        && item.hasItemMeta()
                        && player.getOpenInventory().getType().equals(InventoryType.FURNACE)
                        && ItemUtils.hasKey(item, "GENBUCKET-ID")
                        && event.getClick().isShiftClick()) {
                    event.setCancelled(true);
                    player.sendMessage(Message.PREFIX.getMessage() + Message.GEN_BLOCKED_ACTION.getMessage());
                    player.closeInventory();
                }
            }
        } catch (NullPointerException npe) {
            //ignored
        }
    }

    public boolean withdraw(GenData genData, Player player) {
        double price = genData.getPrice();
        if (price <= 0) return true;
        if (GenBucket.econ == null) {
            player.sendMessage(ChatUtils.color(Message.PREFIX.getMessage() + "No Economy provider found! ex: Essentials, please install one.\n Automatically approving purchase for now..."));
            return true;
        }
        if (GenBucket.econ.withdrawPlayer(player, price).transactionSuccess()) {
            if (!Config.ECON_DISABLE_CHARGEDMSG.getOption()) {
                String message = ChatUtils.color(Message.PREFIX.getMessage() + Message.GEN_CHARGED.getMessage().replace("%amount%", price + ""));
                if (!Config.ECON_USE_ACTIONBAR.getOption()) {
                    player.sendMessage(message);
                } else {
                    ActionBar.sendActionBar(player, message);
                }
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

    private static void register(Generator generator, int delayTicks) {
        if (delayTicks <= 0) {
            delayTicks = Config.GENERATION_DELAY.getInt();
        }
        new GenTask(generator).runTaskTimer(GenBucket.get(), 0L, delayTicks);
    }

    private boolean gen(Player player, ItemStack item, Block block, BlockFace blockFace) {
        if (!Util.isEnabledWorld(block.getWorld().getName())) {
            player.sendMessage(Message.PREFIX.getMessage() + Message.GEN_CANT_PLACE_WORLD.getMessage()
                    .replace("%world%", block.getWorld().getName()));
            if (player.isOp() || player.hasPermission("is.op"))
                player.sendMessage(Message.PREFIX.getMessage() + "&cHey, OP, if you want to enable this world for GenBuckets, look in the config for the enabled-worlds section!");
            return false;
        }
        String genID = ItemUtils.getKeyString(item, "GENBUCKET-ID");
        //if (!GenBucket.genDataMap.containsKey(genID)) {
        //    item.setType(Material.AIR);
        //    return;
        //}
        GenData genData = GenBucket.genDataMap.get(genID);
        PlayerPlaceGenEvent placeGenEvent = new PlayerPlaceGenEvent(player, block, genData);
        Bukkit.getServer().getPluginManager().callEvent(placeGenEvent);
        if (placeGenEvent.isCancelled()) {
            ChatUtils.debug("PlayerPlaceGenEvent was cancelled by another plugin");
            return false;
        }
        FactionHook facHook = ((FactionHook) GenBucket.get().getHookManager().getPluginMap().get("Factions"));
        if (Config.HOOK_WG_CHECK.getOption() && GenBucket.get().hasWorldGuard() && !GenBucket.get().getWorldGuard().hasBuildPermission(player, block)) {
            player.sendMessage(ChatUtils.color(Message.PREFIX.getMessage() + Message.GEN_CANCELLED.getMessage()));
            ChatUtils.debug("WorldGuard denied " + player.getName() + "from placing a genBucket at " + block.getLocation().toString());
            return false;
        }
        if (!facHook.canBuild(block, player) || facHook.hasNearbyPlayer(player)) {
            ChatUtils.debug("Factions denied " + player.getName() + "from placing a genBucket at " + block.getLocation().toString());
            return false;
        }
        if (!withdraw(genData, player)) return false;
        if (genData.getType() == GenType.VERTICAL) {
            register(new VerticalGen(GenBucket.get(), player, block, blockFace, genData), genData.getDelayTicks());
        } else {
            register(new HorizontalGen(GenBucket.get(), player, block, blockFace, genData), genData.getDelayTicks());
        }
        Bukkit.getServer().getPluginManager().callEvent(new PlayerGenEvent(player, block, genData));
        if (genData.isConsumable()) {
            int amountPlayer = getTool(player).getAmount();
            if (amountPlayer > 1) {
                getTool(player).setAmount(amountPlayer - 1);
            } else {
                getTool(player).setAmount(0);
            }
        }
        return true;
    }

}
