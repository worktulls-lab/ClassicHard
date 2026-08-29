package ru.dplus.hardmode.listeners;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.DragonFireball;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import ru.dplus.hardmode.HardMode;

import java.util.Random;

/**
 * Периодически даёт Дракону Края шанс выпустить дополнительный заряд кислоты
 * (файербол), не трогая при этом его базовый урон.
 */
public class DragonBreathTask extends BukkitRunnable {

    private final HardMode plugin;
    private final Random random = new Random();

    public DragonBreathTask(HardMode plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        double chance = plugin.getConfig().getDouble("dragon-extra-fireball-chance", 0.5);
        double doubleChance = plugin.getConfig().getDouble("dragon-double-fireball-chance", 0.25);

        for (World world : plugin.getServer().getWorlds()) {
            for (EnderDragon dragon : world.getEntitiesByClass(EnderDragon.class)) {
                if (random.nextDouble() >= chance) continue;

                Player target = nearestPlayer(dragon, 40.0);
                if (target == null) continue;

                Location from = dragon.getEyeLocation();
                Vector direction = target.getEyeLocation().toVector()
                        .subtract(from.toVector())
                        .normalize();

                shootFireball(world, dragon, from, direction);

                if (random.nextDouble() < doubleChance) {
                    // веером: слегка разворачиваем вектор по горизонтали для второго заряда
                    Vector spread = rotateAroundY(direction, 12.0);
                    shootFireball(world, dragon, from, spread);
                }
            }
        }
    }

    private void shootFireball(World world, EnderDragon dragon, Location from, Vector direction) {
        DragonFireball fireball = world.spawn(from, DragonFireball.class);
        fireball.setShooter(dragon);
        fireball.setVelocity(direction);
    }

    private Vector rotateAroundY(Vector vector, double degrees) {
        double rad = Math.toRadians(degrees);
        double cos = Math.cos(rad);
        double sin = Math.sin(rad);
        double x = vector.getX() * cos - vector.getZ() * sin;
        double z = vector.getX() * sin + vector.getZ() * cos;
        return new Vector(x, vector.getY(), z);
    }

    private Player nearestPlayer(EnderDragon dragon, double radius) {
        Player nearest = null;
        double nearestDistSq = radius * radius;
        for (Player player : dragon.getWorld().getPlayers()) {
            double distSq = player.getLocation().distanceSquared(dragon.getLocation());
            if (distSq < nearestDistSq) {
                nearestDistSq = distSq;
                nearest = player;
            }
        }
        return nearest;
    }
}
