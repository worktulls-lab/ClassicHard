package ru.dplus.hardmode.listeners;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Enderman;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import ru.dplus.hardmode.HardMode;

import java.util.Random;

public class EndermanTeleportListener implements Listener {

    private final HardMode plugin;
    private final Random random = new Random();

    public EndermanTeleportListener(HardMode plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEndermanHit(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Enderman enderman)) return;

        double chance = plugin.getConfig().getDouble("enderman-teleport-on-hit-chance", 0.7);
        if (random.nextDouble() >= chance) return;

        Location safe = findSafeSpot(enderman, 6);
        if (safe == null) return;

        World world = enderman.getWorld();
        world.spawnParticle(Particle.PORTAL, enderman.getLocation(), 32, 0.5, 1, 0.5, 0.1);
        enderman.teleport(safe);
        world.spawnParticle(Particle.PORTAL, safe, 32, 0.5, 1, 0.5, 0.1);
        world.playSound(safe, Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f);
    }

    private Location findSafeSpot(Enderman enderman, int radius) {
        World world = enderman.getWorld();
        Location base = enderman.getLocation();
        for (int i = 0; i < 10; i++) {
            int dx = random.nextInt(radius * 2 + 1) - radius;
            int dz = random.nextInt(radius * 2 + 1) - radius;
            int x = base.getBlockX() + dx;
            int z = base.getBlockZ() + dz;
            int y = world.getHighestBlockYAt(x, z) + 1;
            Location loc = new Location(world, x + 0.5, y, z + 0.5);
            if (loc.getBlock().getType().isAir()) return loc;
        }
        return null;
    }
}
