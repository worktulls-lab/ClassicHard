package ru.dplus.hardmode.listeners;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Hoglin;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import ru.dplus.hardmode.HardMode;

/**
 * Усиливает "классических" мирных мобов (коров, овец, свиней, курей и т.п.):
 * HP и скорость передвижения. Хоглины сюда намеренно не входят — они хоть
 * и относятся к Animals, но в плагине уже отдельно усилены как нейтральный
 * моб (HoglinAggroListener), а не как мирный.
 */
public class PassiveMobListener implements Listener {

    private final HardMode plugin;

    public PassiveMobListener(HardMode plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onSpawn(CreatureSpawnEvent event) {
        LivingEntity entity = event.getEntity();
        if (!(entity instanceof Animals)) return;
        if (entity instanceof Hoglin) return;

        double healthMult = plugin.getConfig().getDouble("passive-mob-health-multiplier", 2.0);
        double speedMult = plugin.getConfig().getDouble("passive-mob-speed-multiplier", 1.15);

        AttributeInstance maxHealth = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (maxHealth != null) {
            maxHealth.setBaseValue(maxHealth.getBaseValue() * healthMult);
            entity.setHealth(maxHealth.getValue());
        }

        AttributeInstance speed = entity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
        if (speed != null) {
            speed.setBaseValue(speed.getBaseValue() * speedMult);
        }
    }
}
