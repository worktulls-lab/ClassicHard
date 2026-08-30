package ru.dplus.hardmode.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerExpChangeEvent;
import ru.dplus.hardmode.HardMode;

/**
 * Формула "сколько опыта нужно до левел-апа" зашита в движке и не
 * регулируется публичным API, поэтому вместо неё уменьшается получаемый
 * опыт — эффект для игрока тот же самый: прогрессия зачарований/лечения
 * через опыт становится медленнее.
 */
public class ExperienceNerfListener implements Listener {

    private final HardMode plugin;

    public ExperienceNerfListener(HardMode plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onExpChange(PlayerExpChangeEvent event) {
        double multiplier = plugin.getConfig().getDouble("experience-gain-multiplier", 0.7);
        event.setAmount((int) Math.max(0, event.getAmount() * multiplier));
    }
}
