package ru.dplus.hardmode.listeners;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Blaze;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.SmallFireball;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import ru.dplus.hardmode.HardMode;

import java.util.Random;

/**
 * Не трогает ванильную очередь из 2-3 файерболов у ифрита (она и так есть),
 * а раз в некоторое время даёт шанс на ОТДЕЛЬНЫЙ дополнительный выстрел
 * в паузе между обычными атаками — итого ифрит стреляет в целом чаще.
 */
public class BlazeExtraShotTask extends BukkitRunnable {

    private final HardMode plugin;
    private final Random random = new Random();

    public BlazeExtraShotTask(HardMode plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        double chance = plugin.getConfig().getDouble("blaze-extra-shot-chance", 0.4);

        for (World world : plugin.getServer().getWorlds()) {
            for (Blaze blaze : world.getEntitiesByClass(Blaze.class)) {
                LivingEntity target = blaze.getTarget();
                if (target == null || !target.isValid()) continue;
                if (random.nextDouble() >= chance) continue;

                Location from = blaze.getEyeLocation();
                Vector direction = target.getEyeLocation().toVector()
                        .subtract(from.toVector())
                        .normalize();

                SmallFireball fireball = world.spawn(from, SmallFireball.class);
                fireball.setShooter(blaze);
                fireball.setDirection(direction);
                fireball.setVelocity(direction.multiply(1.0));
            }
        }
    }
}
