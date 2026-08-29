package ru.dplus.hardmode.listeners;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Hoglin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import ru.dplus.hardmode.HardMode;

public class HoglinAggroListener implements Listener {

    private final HardMode plugin;

    public HoglinAggroListener(HardMode plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onSpawn(CreatureSpawnEvent event) {
        if (!(event.getEntity() instanceof Hoglin hoglin)) return;

        double multiplier = plugin.getConfig().getDouble("hoglin-follow-range-multiplier", 2.0);
        AttributeInstance follow = hoglin.getAttribute(Attribute.GENERIC_FOLLOW_RANGE);
        if (follow != null) {
            follow.setBaseValue(follow.getBaseValue() * multiplier);
        }
    }
}
