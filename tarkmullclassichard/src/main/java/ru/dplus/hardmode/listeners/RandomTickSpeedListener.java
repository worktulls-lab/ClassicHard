package ru.dplus.hardmode.listeners;

import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import ru.dplus.hardmode.HardMode;

/**
 * Замедляет рост урожая (и другие механики на "случайных тиках" — распад
 * листьев, таяние льда/снега, рост сахарного тростника и т.п.) через
 * настоящее ванильное гейм-правило randomTickSpeed. Ванильное значение по
 * умолчанию — 3; здесь ставится 2, что даёт примерно в 1.5 раза более
 * редкие случайные тики (и, соответственно, примерно во столько же раз
 * более медленный рост).
 *
 * Значение 1 замедлило бы сильнее (~3 раза), но это правило действует на
 * ВСЁ и ВСЕГДА, для всех игроков одновременно, в отличие от разовых стычек
 * с боссами — риск, что станет утомительно, а не интересно сложно, здесь
 * выше, поэтому выбрано более умеренное значение.
 */
public class RandomTickSpeedListener implements Listener {

    private final HardMode plugin;

    public RandomTickSpeedListener(HardMode plugin) {
        this.plugin = plugin;
    }

    public void applyToAllWorlds() {
        for (World world : plugin.getServer().getWorlds()) {
            apply(world);
        }
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        apply(event.getWorld());
    }

    private void apply(World world) {
        if (!plugin.getConfig().getBoolean("random-tick-speed-enabled", true)) return;
        int value = plugin.getConfig().getInt("random-tick-speed-value", 2);
        world.setGameRule(GameRule.RANDOM_TICK_SPEED, value);
    }
}
