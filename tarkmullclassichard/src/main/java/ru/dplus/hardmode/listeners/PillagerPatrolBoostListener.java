package ru.dplus.hardmode.listeners;

import org.bukkit.entity.Pillager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import ru.dplus.hardmode.HardMode;

import java.util.Random;

/**
 * Публичного контроля над частотой самих патрулей пиллагеров нет (это
 * внутренняя ежедневная проверка движка, без хука в Bukkit API), поэтому
 * здесь патрули становятся НЕЗАМЕТНО крупнее: небольшой шанс добавить ещё
 * одного пиллагера рядом, когда патруль уже спавнится (SpawnReason.PATROL).
 * Частоту самих патрулей это не увеличивает, только их размер, и то редко.
 */
public class PillagerPatrolBoostListener implements Listener {

    private final HardMode plugin;
    private final Random random = new Random();

    public PillagerPatrolBoostListener(HardMode plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPatrolSpawn(CreatureSpawnEvent event) {
        if (event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.PATROL) return;
        if (!(event.getEntity() instanceof Pillager)) return;

        double chance = plugin.getConfig().getDouble("pillager-patrol-extra-chance", 0.15);
        if (random.nextDouble() >= chance) return;

        event.getLocation().getWorld().spawn(event.getLocation(), Pillager.class);
    }
}
