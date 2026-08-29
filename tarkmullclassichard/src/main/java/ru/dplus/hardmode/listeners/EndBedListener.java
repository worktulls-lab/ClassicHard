package ru.dplus.hardmode.listeners;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import ru.dplus.hardmode.HardMode;

/**
 * В ванильном Minecraft кровать можно ПОСТАВИТЬ в Энде — она взрывается только
 * при попытке лечь спать (правый клик). Этот листенер запрещает саму установку.
 */
public class EndBedListener implements Listener {

    private final HardMode plugin;

    public EndBedListener(HardMode plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBedPlace(BlockPlaceEvent event) {
        Material type = event.getBlock().getType();
        if (!type.name().endsWith("_BED")) return;

        World world = event.getBlock().getWorld();
        if (world.getEnvironment() != World.Environment.THE_END) return;

        event.setCancelled(true);
        Player player = event.getPlayer();
        player.sendMessage("§cКровати нельзя ставить в Энде.");
    }
}
