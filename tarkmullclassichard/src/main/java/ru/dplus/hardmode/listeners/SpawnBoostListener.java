package ru.dplus.hardmode.listeners;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import ru.dplus.hardmode.HardMode;

import java.util.Random;

/**
 * Немного увеличивает плотность спавна враждебных мобов: при обычном (NATURAL)
 * спавне есть шанс заспавнить ещё одного моба того же типа рядом.
 */
public class SpawnBoostListener implements Listener {

    private final HardMode plugin;
    private final Random random = new Random();

    public SpawnBoostListener(HardMode plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onNaturalSpawn(CreatureSpawnEvent event) {
        if (event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.NATURAL) return;
        if (!(event.getEntity() instanceof Monster)) return;

        double chance = plugin.getConfig().getDouble("extra-spawn-chance", 0.35);
        if (random.nextDouble() >= chance) return;

        Location base = event.getLocation();
        World world = base.getWorld();
        if (world == null) return;

        int dx = random.nextInt(7) - 3;
        int dz = random.nextInt(7) - 3;
        int x = base.getBlockX() + dx;
        int z = base.getBlockZ() + dz;
        int y = world.getHighestBlockYAt(x, z) + 1;

        Location spawnLoc = new Location(world, x + 0.5, y, z + 0.5);
        // spawnEntity вызывает CreatureSpawnEvent с причиной, отличной от NATURAL,
        // поэтому бесконечной цепочки спавнов не будет.
        Entity ignored = world.spawnEntity(spawnLoc, event.getEntityType());
    }
}
