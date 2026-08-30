package ru.dplus.hardmode.listeners;

import org.bukkit.World;
import org.bukkit.entity.ZombieVillager;
import org.bukkit.scheduler.BukkitRunnable;
import ru.dplus.hardmode.HardMode;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Делает лечение зомби-жителя сложнее: как только начинается конверсия
 * (isConverting() == true), время до превращения обратно в жителя
 * увеличивается. Публичного события "конверсия началась" нет, поэтому
 * отслеживание идёт периодической проверкой (раз в секунду) — этого более
 * чем достаточно, учитывая, что сам процесс лечения длится минуты.
 */
public class ZombieVillagerCureTask extends BukkitRunnable {

    private final HardMode plugin;
    private final Set<UUID> alreadyExtended = new HashSet<>();

    public ZombieVillagerCureTask(HardMode plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        double multiplier = plugin.getConfig().getDouble("zombie-villager-cure-time-multiplier", 1.8);

        for (World world : plugin.getServer().getWorlds()) {
            for (ZombieVillager zv : world.getEntitiesByClass(ZombieVillager.class)) {
                UUID id = zv.getUniqueId();

                if (!zv.isConverting()) {
                    alreadyExtended.remove(id);
                    continue;
                }
                if (alreadyExtended.contains(id)) continue;

                int currentTime = zv.getConversionTime();
                zv.setConversionTime((int) (currentTime * multiplier));
                alreadyExtended.add(id);
            }
        }
    }
}
