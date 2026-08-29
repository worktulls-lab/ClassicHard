package ru.dplus.hardmode.listeners;

import org.bukkit.entity.Blaze;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import ru.dplus.hardmode.HardMode;

/**
 * Когда ифрит поджигает цель, горение длится дольше. Проверяются оба
 * варианта того, кем публичный API может отметить источник поджога —
 * самим ифритом (ближний бой) или его файерболом (getShooter()) — чтобы
 * не промахнуться мимо реального сценария.
 */
public class BlazeFireDurationListener implements Listener {

    private final HardMode plugin;

    public BlazeFireDurationListener(HardMode plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onCombust(EntityCombustByEntityEvent event) {
        Entity combuster = event.getCombuster();
        boolean fromBlaze = combuster instanceof Blaze
                || (combuster instanceof Projectile projectile && projectile.getShooter() instanceof Blaze);
        if (!fromBlaze) return;

        double multiplier = plugin.getConfig().getDouble("blaze-fire-duration-multiplier", 1.8);
        event.setDuration((int) (event.getDuration() * multiplier));
    }
}
