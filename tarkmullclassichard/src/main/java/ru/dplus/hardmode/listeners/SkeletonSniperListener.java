package ru.dplus.hardmode.listeners;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Skeleton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.util.Vector;
import ru.dplus.hardmode.HardMode;

import java.util.Random;

/**
 * Идеальную баллистику с учётом гравитации и упреждения по движению цели
 * публичный API не даёт, но направление выстрела точно на цель (с той же
 * скоростью стрелы) уже сильно поднимает процент попаданий.
 */
public class SkeletonSniperListener implements Listener {

    private final HardMode plugin;
    private final Random random = new Random();

    public SkeletonSniperListener(HardMode plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onShootBow(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Skeleton skeleton)) return;
        LivingEntity target = skeleton.getTarget();
        if (target == null) return;
        if (!(event.getProjectile() instanceof Projectile projectile)) return;

        double chance = plugin.getConfig().getDouble("skeleton-snipe-chance", 0.85);
        if (random.nextDouble() >= chance) return;

        double speed = projectile.getVelocity().length();
        if (speed <= 0) return;

        Vector direction = target.getEyeLocation().toVector()
                .subtract(skeleton.getEyeLocation().toVector())
                .normalize();
        projectile.setVelocity(direction.multiply(speed));
    }
}
