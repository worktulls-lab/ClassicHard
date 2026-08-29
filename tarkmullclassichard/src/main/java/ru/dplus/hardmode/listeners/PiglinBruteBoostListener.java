package ru.dplus.hardmode.listeners;

import org.bukkit.entity.PiglinBrute;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import ru.dplus.hardmode.HardMode;

import java.util.Random;

/**
 * Piglin Brute обычно спавнится по одному из спаунера в сокровищнице бастиона.
 * С шансом добавляем ещё одного рядом, когда это происходит.
 */
public class PiglinBruteBoostListener implements Listener {

    private final HardMode plugin;
    private final Random random = new Random();

    public PiglinBruteBoostListener(HardMode plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onSpawn(CreatureSpawnEvent event) {
        if (!(event.getEntity() instanceof PiglinBrute)) return;
        if (event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.SPAWNER) return;

        double chance = plugin.getConfig().getDouble("piglin-brute-extra-chance", 0.4);
        if (random.nextDouble() >= chance) return;

        event.getLocation().getWorld().spawn(event.getLocation(), PiglinBrute.class);
    }
}
