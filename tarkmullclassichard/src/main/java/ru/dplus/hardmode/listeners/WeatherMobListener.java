package ru.dplus.hardmode.listeners;

import org.bukkit.entity.Creeper;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import ru.dplus.hardmode.HardMode;

import java.util.Random;

public class WeatherMobListener implements Listener {

    private final HardMode plugin;
    private final Random random = new Random();

    public WeatherMobListener(HardMode plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onCreeperSpawn(CreatureSpawnEvent event) {
        LivingEntity entity = event.getEntity();
        if (!(entity instanceof Creeper creeper)) return;
        if (!creeper.getWorld().isThundering()) return;

        double chance = plugin.getConfig().getDouble("storm-charged-creeper-chance", 0.25);
        if (random.nextDouble() < chance) {
            creeper.setPowered(true);
        }
    }
}
