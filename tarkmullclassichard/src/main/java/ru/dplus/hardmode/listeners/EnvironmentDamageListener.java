package ru.dplus.hardmode.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import ru.dplus.hardmode.HardMode;

/**
 * Увеличивает урон от порошкового снега (замерзание), от контакта
 * с магматическими блоками, от кактуса/колючего куста (CONTACT) и от
 * голодания (STARVATION).
 */
public class EnvironmentDamageListener implements Listener {

    private final HardMode plugin;

    public EnvironmentDamageListener(HardMode plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEnvironmentDamage(EntityDamageEvent event) {
        double multiplier;
        switch (event.getCause()) {
            case FREEZE -> multiplier = plugin.getConfig().getDouble("freeze-damage-multiplier", 1.5);
            case HOT_FLOOR -> multiplier = plugin.getConfig().getDouble("magma-damage-multiplier", 1.5);
            case CONTACT -> multiplier = plugin.getConfig().getDouble("contact-damage-multiplier", 1.5);
            case STARVATION -> multiplier = plugin.getConfig().getDouble("starvation-damage-multiplier", 1.5);
            default -> {
                return;
            }
        }
        event.setDamage(event.getDamage() * multiplier);
    }
}
