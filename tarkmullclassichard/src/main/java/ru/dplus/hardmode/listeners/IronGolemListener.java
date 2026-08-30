package ru.dplus.hardmode.listeners;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import ru.dplus.hardmode.HardMode;

/**
 * Железный голем не входит ни в одну из общих категорий плагина (не Monster,
 * не Animals), поэтому усиливается отдельно. Только HP — через обычный
 * атрибут, гарантированно работает.
 */
public class IronGolemListener implements Listener {

    private final HardMode plugin;

    public IronGolemListener(HardMode plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onSpawn(CreatureSpawnEvent event) {
        LivingEntity entity = event.getEntity();
        if (!(entity instanceof IronGolem)) return;

        double healthMult = plugin.getConfig().getDouble("iron-golem-health-multiplier", 2.0);
        AttributeInstance maxHealth = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (maxHealth != null) {
            maxHealth.setBaseValue(maxHealth.getBaseValue() * healthMult);
            entity.setHealth(maxHealth.getValue());
        }
    }
}
