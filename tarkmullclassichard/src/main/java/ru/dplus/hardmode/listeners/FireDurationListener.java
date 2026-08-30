package ru.dplus.hardmode.listeners;

import org.bukkit.entity.Blaze;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import ru.dplus.hardmode.HardMode;

/**
 * Продлевает горение от ЛЮБОГО источника (лава, контакт с огнём, костёр
 * и т.п.). Поджог от ифрита сюда не входит — он уже обрабатывается отдельно
 * в BlazeFireDurationListener со своим множителем, чтобы не удваивать эффект
 * (EntityCombustByEntityEvent — это тот же самый объект события, что и
 * EntityCombustEvent, просто более узкий тип, так что оба листенера иначе
 * сработали бы на одно и то же событие).
 */
public class FireDurationListener implements Listener {

    private final HardMode plugin;

    public FireDurationListener(HardMode plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onCombust(EntityCombustEvent event) {
        if (isBlazeCaused(event)) return;

        double multiplier = plugin.getConfig().getDouble("fire-duration-multiplier", 1.5);
        event.setDuration((int) (event.getDuration() * multiplier));
    }

    private boolean isBlazeCaused(EntityCombustEvent event) {
        if (!(event instanceof EntityCombustByEntityEvent byEntity)) return false;
        Entity combuster = byEntity.getCombuster();
        return combuster instanceof Blaze
                || (combuster instanceof Projectile projectile && projectile.getShooter() instanceof Blaze);
    }
}
