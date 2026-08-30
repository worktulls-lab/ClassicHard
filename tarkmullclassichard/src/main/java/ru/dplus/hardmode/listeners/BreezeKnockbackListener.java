package ru.dplus.hardmode.listeners;

import org.bukkit.entity.Breeze;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;
import ru.dplus.hardmode.HardMode;

/**
 * Усиливает отталкивание от атак Вихря (Breeze) — и от заряда ветра, и от
 * ближней атаки.
 */
public class BreezeKnockbackListener implements Listener {

    private final HardMode plugin;

    public BreezeKnockbackListener(HardMode plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBreezeHit(EntityDamageByEntityEvent event) {
        Breeze breeze = resolveBreeze(event);
        if (breeze == null) return;
        if (!(event.getEntity() instanceof LivingEntity target)) return;

        double multiplier = plugin.getConfig().getDouble("breeze-knockback-multiplier", 1.6);
        Vector push = target.getLocation().toVector()
                .subtract(breeze.getLocation().toVector())
                .normalize()
                .multiply(multiplier);
        push.setY(Math.max(push.getY(), 0.3));
        target.setVelocity(target.getVelocity().add(push));
    }

    private Breeze resolveBreeze(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Breeze breeze) return breeze;
        if (event.getDamager() instanceof Projectile projectile
                && projectile.getShooter() instanceof Breeze breeze) {
            return breeze;
        }
        return null;
    }
}
