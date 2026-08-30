package ru.dplus.hardmode.listeners;

import org.bukkit.Location;
import org.bukkit.entity.Evoker;
import org.bukkit.entity.EvokerFangs;
import org.bukkit.entity.Vex;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.util.Vector;
import ru.dplus.hardmode.HardMode;

import java.util.Random;

/**
 * Усиливает Иворкера тремя способами:
 * 1) Вексы живут дольше (Vex#hasLimitedLife()/setLimitedLifeTicks()).
 * 2) Шанс на доп. векса при призыве заклинанием (SpawnReason.SPELL).
 * 3) Атака шипами (EvokerFangs) шире — доп. шипы по бокам от линии атаки.
 *
 * Честная оговорка: пункт 3 использует EvokerFangs#getOwner()/setOwner() —
 * это менее "популярная" часть API, чем большинство остального в плагине,
 * и я не могу на 100% гарантировать точность сигнатур без компилятора под
 * рукой (в песочнице нет сети для Maven). Если это конкретно не
 * скомпилируется — компилятор укажет ровно на эти строки, остальной файл
 * не пострадает.
 */
public class EvokerListener implements Listener {

    private final HardMode plugin;
    private final Random random = new Random();
    private boolean spawningExtra = false;

    public EvokerListener(HardMode plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onVexSpawn(CreatureSpawnEvent event) {
        if (!(event.getEntity() instanceof Vex vex)) return;

        if (vex.hasLimitedLife()) {
            double lifeMultiplier = plugin.getConfig().getDouble("vex-life-multiplier", 1.6);
            vex.setLimitedLifeTicks((int) (vex.getLimitedLifeTicks() * lifeMultiplier));
        }

        if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.SPELL) {
            double extraChance = plugin.getConfig().getDouble("evoker-extra-vex-chance", 0.5);
            if (random.nextDouble() < extraChance) {
                event.getLocation().getWorld().spawn(event.getLocation(), Vex.class);
            }
        }
    }

    @EventHandler
    public void onFangSpawn(EntitySpawnEvent event) {
        if (spawningExtra) return;
        if (!(event.getEntity() instanceof EvokerFangs fang)) return;
        if (!(fang.getOwner() instanceof Evoker evoker)) return;

        double chance = plugin.getConfig().getDouble("evoker-fang-widen-chance", 0.5);
        if (random.nextDouble() >= chance) return;

        Vector direction = evoker.getLocation().getDirection();
        Vector perpendicular = new Vector(-direction.getZ(), 0, direction.getX()).normalize();
        double offset = plugin.getConfig().getDouble("evoker-fang-widen-offset", 1.0);

        spawningExtra = true;
        try {
            Location loc1 = fang.getLocation().clone().add(perpendicular.clone().multiply(offset));
            Location loc2 = fang.getLocation().clone().add(perpendicular.clone().multiply(-offset));

            EvokerFangs f1 = fang.getWorld().spawn(loc1, EvokerFangs.class);
            f1.setOwner(evoker);
            EvokerFangs f2 = fang.getWorld().spawn(loc2, EvokerFangs.class);
            f2.setOwner(evoker);
        } finally {
            spawningExtra = false;
        }
    }
}
