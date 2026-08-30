package ru.dplus.hardmode.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemDamageEvent;
import ru.dplus.hardmode.HardMode;

/**
 * Ускоряет потерю прочности инструментов и брони у игроков.
 */
public class DurabilityListener implements Listener {

    private final HardMode plugin;

    public DurabilityListener(HardMode plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onItemDamage(PlayerItemDamageEvent event) {
        double multiplier = plugin.getConfig().getDouble("durability-loss-multiplier", 2.0);
        int newDamage = (int) Math.ceil(event.getDamage() * multiplier);
        event.setDamage(Math.max(newDamage, 1));
    }
}
