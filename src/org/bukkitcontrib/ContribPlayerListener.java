package org.bukkitcontrib;

import java.lang.reflect.Field;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkitcontrib.player.ContribCraftPlayer;

public class ContribPlayerListener extends PlayerListener{

    @Override
    public void onPlayerJoin(PlayerJoinEvent event) {
        ContribCraftPlayer.updateNetServerHandler(event.getPlayer());
        ContribCraftPlayer.updateBukkitEntity(event.getPlayer());
        updatePlayerEvent(event);
    }

    @Override
    public void onPlayerTeleport(final PlayerTeleportEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (!event.getFrom().getWorld().getName().equals(event.getTo().getWorld().getName())) {
            Runnable update = new Runnable() {
                public void run() {
                    ContribCraftPlayer.updateBukkitEntity(event.getPlayer());
                }
            };
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(BukkitContrib.getInstance(), update);
        }
    }

    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        if (event.getClickedBlock() != null) {
            Material type = event.getClickedBlock().getType();
            if (type == Material.CHEST || type == Material.DISPENSER || type == Material.WORKBENCH || type == Material.FURNACE) {
                ContribCraftPlayer player = (ContribCraftPlayer)event.getPlayer();
                player.getNetServerHandler().activeLocation = event.getClickedBlock().getLocation();
            }
        }
    }
    
    private void updatePlayerEvent(PlayerEvent event) {
        try {
            Field player = PlayerEvent.class.getDeclaredField("player");
            player.setAccessible(true);
            player.set(event, ((ContribCraftPlayer)((CraftPlayer)event.getPlayer()).getHandle().getBukkitEntity()));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}