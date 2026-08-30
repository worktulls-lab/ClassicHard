package ru.dplus.hardmode.listeners;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Skeleton;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import ru.dplus.hardmode.HardMode;

import java.util.Random;

/**
 * По аналогии с ифритом и вихрем — периодически даёт скелету небольшой шанс
 * на дополнительный выстрел стрелой в паузе между обычными атаками.
 */
public class SkeletonExtraShotTask extends BukkitRunnable {

    private final HardMode plugin;
    private final Random random = new Random();

    public SkeletonExtraShotTask(HardMode plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        double chance = plugin.getConfig().getDouble("skeleton-extra-shot-chance", 0.1);

        for (World world : plugin.getServer().getWorlds()) {
            for (Skeleton skeleton : world.getEntitiesByClass(Skeleton.class)) {
                LivingEntity target = skeleton.getTarget();
                if (target == null || !target.isValid()) continue;
                if (random.nextDouble() >= chance) continue;

                Location from = skeleton.getEyeLocation();
                Vector direction = target.getEyeLocation().toVector()
                        .subtract(from.toVector())
                        .normalize();

                Arrow arrow = world.spawnArrow(from, direction, 3.0f, 0f);
                arrow.setShooter(skeleton);
            }
        }
    }
}
