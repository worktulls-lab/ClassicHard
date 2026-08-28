package ru.dplus.hardmode.listeners;

import org.bukkit.Location;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.DragonFireball;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import ru.dplus.hardmode.HardMode;

/**
 * Усиливает бой с Драконом Края за пределами голого урона от самого дракона:
 * взрыв кристаллов задевает игрока сильнее, а кислотные лужи от плевков
 * держатся дольше и покрывают большую площадь.
 */
public class DragonFightListener implements Listener {

    private final HardMode plugin;

    public DragonFightListener(HardMode plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onCrystalExplosionDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof EnderCrystal)) return;
        if (event.getCause() != EntityDamageEvent.DamageCause.ENTITY_EXPLOSION) return;

        double multiplier = plugin.getConfig().getDouble("crystal-explosion-damage-multiplier", 1.6);
        event.setDamage(event.getDamage() * multiplier);
    }

    @EventHandler
    public void onDragonFireballHit(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof DragonFireball fireball)) return;

        Location hitLoc = fireball.getLocation();
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            double radiusMult = plugin.getConfig().getDouble("dragon-acid-radius-multiplier", 1.5);
            double durationMult = plugin.getConfig().getDouble("dragon-acid-duration-multiplier", 1.5);

            for (Entity nearby : hitLoc.getWorld().getNearbyEntities(hitLoc, 4, 4, 4)) {
                if (nearby instanceof AreaEffectCloud cloud) {
                    cloud.setRadius((float) (cloud.getRadius() * radiusMult));
                    cloud.setDuration((int) (cloud.getDuration() * durationMult));
                }
            }
        });
    }
}
