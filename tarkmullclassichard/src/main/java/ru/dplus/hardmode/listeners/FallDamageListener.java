package ru.dplus.hardmode.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import ru.dplus.hardmode.HardMode;

public class FallDamageListener implements Listener {

    private final HardMode plugin;

    public FallDamageListener(HardMode plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onFallDamage(EntityDamageEvent event) {
        if (event.getCause() != EntityDamageEvent.DamageCause.FALL) return;
        if (!(event.getEntity() instanceof Player)) return;

        double multiplier = plugin.getConfig().getDouble("fall-damage-multiplier", 1.5);
        event.setDamage(event.getDamage() * multiplier);
    }
}
