package ru.dplus.hardmode.listeners;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Phantom;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import ru.dplus.hardmode.HardMode;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

/**
 * В ваниле фантомы спавнятся только после 3+ дней без сна. Эта задача
 * периодически проверяет каждого игрока и с шансом спавнит фантомов
 * каждую ночь, независимо от статистики сна.
 */
public class PhantomEveryNightTask extends BukkitRunnable {

    private final HardMode plugin;
    private final Random random = new Random();
    // игрок -> "день" (world.getFullTime() / 24000), для которого уже спавнили этой ночью
    private final Map<UUID, Long> lastNightSpawned = new HashMap<>();

    public PhantomEveryNightTask(HardMode plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        if (!plugin.getConfig().getBoolean("phantom-every-night-enabled", true)) return;

        double chance = plugin.getConfig().getDouble("phantom-every-night-chance", 0.7);
        int min = plugin.getConfig().getInt("phantom-every-night-min", 1);
        int max = plugin.getConfig().getInt("phantom-every-night-max", 2);

        for (Player player : plugin.getServer().getOnlinePlayers()) {
            if (player.getGameMode() != GameMode.SURVIVAL && player.getGameMode() != GameMode.ADVENTURE) continue;

            World world = player.getWorld();
            long time = world.getTime();
            boolean isNight = time >= 13000 && time < 23000;
            if (!isNight) continue;

            long day = world.getFullTime() / 24000L;
            Long lastDay = lastNightSpawned.get(player.getUniqueId());
            if (lastDay != null && lastDay == day) continue;

            // помечаем сразу, чтобы не спавнить повторно в ту же ночь, даже если чек не сработает
            lastNightSpawned.put(player.getUniqueId(), day);

            if (random.nextDouble() >= chance) continue;
            if (!hasOpenSky(player)) continue;

            int count = min + (max > min ? random.nextInt(max - min + 1) : 0);
            for (int i = 0; i < count; i++) {
                Location spawnLoc = player.getLocation().clone().add(0, 20 + random.nextInt(10), 0);
                world.spawn(spawnLoc, Phantom.class);
            }
        }
    }

    private boolean hasOpenSky(Player player) {
        Location loc = player.getLocation();
        return loc.getWorld().getHighestBlockYAt(loc.getBlockX(), loc.getBlockZ()) <= loc.getBlockY();
    }
}
