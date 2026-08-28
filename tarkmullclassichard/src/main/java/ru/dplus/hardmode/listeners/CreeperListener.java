package ru.dplus.hardmode.listeners;

import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import ru.dplus.hardmode.HardMode;

/**
 * Увеличивает радиус взрыва крипера (и, соответственно, яму от взрыва).
 */
public class CreeperListener implements Listener {

    private final HardMode plugin;

    public CreeperListener(HardMode plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onExplosionPrime(ExplosionPrimeEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Creeper)) return;

        double multiplier = plugin.getConfig().getDouble("creeper-explosion-radius-multiplier", 1.2);
        event.setRadius((float) (event.getRadius() * multiplier));
    }
}
