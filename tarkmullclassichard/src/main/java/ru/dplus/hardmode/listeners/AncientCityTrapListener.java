package ru.dplus.hardmode.listeners;

import org.bukkit.Biome;
import org.bukkit.entity.Entity;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import ru.dplus.hardmode.HardMode;

/**
 * Усиливает взрыв TNT-ловушек в Ancient City. Отличить именно ловушку от
 * TNT, поставленного игроком где-то ещё, по публичному API нельзя, но TNT,
 * взрывающийся в биоме Deep Dark, практически всегда означает ловушку
 * Ancient City (это единственная структура в этом биоме) — проверка идёт
 * по биому в точке взрыва, тот же надёжный паттерн, что и у радиуса взрыва
 * крипера (ExplosionPrimeEvent#setRadius).
 */
public class AncientCityTrapListener implements Listener {

    private final HardMode plugin;

    public AncientCityTrapListener(HardMode plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onTntExplode(ExplosionPrimeEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof TNTPrimed)) return;
        if (entity.getLocation().getBlock().getBiome() != Biome.DEEP_DARK) return;

        double multiplier = plugin.getConfig().getDouble("ancient-city-tnt-multiplier", 1.5);
        event.setRadius(event.getRadius() * multiplier);
    }
}
