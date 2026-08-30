package ru.dplus.hardmode.listeners;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Breeze;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.WindCharge;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import ru.dplus.hardmode.HardMode;

import java.util.Random;

/**
 * Периодически даёт Вихрю (Breeze) шанс на дополнительный заряд ветра в
 * паузе между обычными атаками — в целом атакует заметно чаще.
 */
public class BreezeExtraShotTask extends BukkitRunnable {

    private final HardMode plugin;
    private final Random random = new Random();

    public BreezeExtraShotTask(HardMode plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        double chance = plugin.getConfig().getDouble("breeze-extra-shot-chance", 0.4);

        for (World world : plugin.getServer().getWorlds()) {
            for (Breeze breeze : world.getEntitiesByClass(Breeze.class)) {
                LivingEntity target = breeze.getTarget();
                if (target == null || !target.isValid()) continue;
                if (random.nextDouble() >= chance) continue;

                Location from = breeze.getEyeLocation();
                Vector direction = target.getEyeLocation().toVector()
                        .subtract(from.toVector())
                        .normalize();

                WindCharge charge = world.spawn(from, WindCharge.class);
                charge.setShooter(breeze);
                charge.setVelocity(direction.multiply(1.3));
            }
        }
    }
}
