package ru.dplus.hardmode.listeners;

import org.bukkit.World;
import org.bukkit.entity.Piglin;
import org.bukkit.entity.PiglinBrute;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import ru.dplus.hardmode.HardMode;

/**
 * В ваниле пиглины игнорируют игрока, если на нём есть хотя бы один предмет
 * золотой брони — эта проверка зашита в их AI, публичного способа её
 * отключить нет. Обходной путь: периодически принудительно назначаем
 * ближайшего игрока целью пиглина через Mob#setTarget(). Работает в
 * большинстве случаев, но не является 100% гарантией — если внутренний AI
 * пиглина сам сбросит цель в следующем тике из-за золота, атака может
 * прерваться до следующей проверки этой задачи.
 */
public class PiglinAggroTask extends BukkitRunnable {

    private final HardMode plugin;

    public PiglinAggroTask(HardMode plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        if (!plugin.getConfig().getBoolean("piglin-ignore-gold-armor", true)) return;

        double radius = plugin.getConfig().getDouble("piglin-aggro-radius", 16.0);

        for (World world : plugin.getServer().getWorlds()) {
            for (Piglin piglin : world.getEntitiesByClass(Piglin.class)) {
                if (piglin instanceof PiglinBrute) continue; // громилы и так всегда агрессивны
                if (piglin.getTarget() != null) continue;

                Player nearest = nearestPlayer(piglin, radius);
                if (nearest != null) {
                    piglin.setTarget(nearest);
                }
            }
        }
    }

    private Player nearestPlayer(Piglin piglin, double radius) {
        Player nearest = null;
        double nearestDistSq = radius * radius;
        for (Player player : piglin.getWorld().getPlayers()) {
            double distSq = player.getLocation().distanceSquared(piglin.getLocation());
            if (distSq < nearestDistSq) {
                nearestDistSq = distSq;
                nearest = player;
            }
        }
        return nearest;
    }
}
