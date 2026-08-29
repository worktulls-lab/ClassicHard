package ru.dplus.hardmode.listeners;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import ru.dplus.hardmode.HardMode;

/**
 * Мобы, заспавненные рейдом (SpawnReason.RAID), уже получили обычный
 * mob-health/damage-multiplier от MobStatsListener. Здесь накидывается
 * ещё один множитель СВЕРХУ, специально для рейдов.
 */
public class RaidListener implements Listener {

    private final HardMode plugin;

    public RaidListener(HardMode plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onRaidSpawn(CreatureSpawnEvent event) {
        if (event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.RAID) return;

        LivingEntity entity = event.getEntity();
        double multiplier = plugin.getConfig().getDouble("raid-mob-extra-multiplier", 2.5);

        multiply(entity, Attribute.GENERIC_MAX_HEALTH, multiplier);
        AttributeInstance maxHealth = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (maxHealth != null) {
            entity.setHealth(maxHealth.getValue());
        }

        multiply(entity, Attribute.GENERIC_ATTACK_DAMAGE, multiplier);
    }

    private void multiply(LivingEntity entity, Attribute attribute, double multiplier) {
        AttributeInstance instance = entity.getAttribute(attribute);
        if (instance == null) return;
        instance.setBaseValue(instance.getBaseValue() * multiplier);
    }
}
