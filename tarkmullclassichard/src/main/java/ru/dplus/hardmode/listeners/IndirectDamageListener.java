package ru.dplus.hardmode.listeners;

import org.bukkit.entity.Creeper;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Shulker;
import org.bukkit.entity.Wither;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.projectiles.ProjectileSource;
import ru.dplus.hardmode.HardMode;

/**
 * Множит урон, который не покрывается атрибутом ATTACK_DAMAGE:
 * снаряды (стрелы, файерболы, снаряды шалкера) и взрыв крипера.
 */
public class IndirectDamageListener implements Listener {

    private final HardMode plugin;

    public IndirectDamageListener(HardMode plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Projectile projectile) {
            ProjectileSource shooter = projectile.getShooter();
            if (shooter instanceof LivingEntity livingShooter) {
                Double multiplier = damageMultiplierFor(livingShooter);
                if (multiplier != null) {
                    event.setDamage(event.getDamage() * multiplier);
                }
            }
            return;
        }

        if (event.getDamager() instanceof Creeper
                && event.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION) {
            double multiplier = plugin.getConfig().getDouble("creeper-damage-multiplier", 1.5);
            event.setDamage(event.getDamage() * multiplier);
        }
    }

    private Double damageMultiplierFor(LivingEntity entity) {
        if (entity instanceof EnderDragon || entity instanceof Wither) {
            return plugin.getConfig().getDouble("boss-damage-multiplier", 1.6);
        }
        if (entity instanceof Monster || entity instanceof Ghast || entity instanceof Shulker) {
            return plugin.getConfig().getDouble("mob-damage-multiplier", 2.0);
        }
        return null;
    }
}
