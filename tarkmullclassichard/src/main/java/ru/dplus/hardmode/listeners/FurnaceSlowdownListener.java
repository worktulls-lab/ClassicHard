package ru.dplus.hardmode.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.FurnaceStartSmeltEvent;
import ru.dplus.hardmode.HardMode;

/**
 * Замедляет готовку во всех печах (обычная печь, доменная печь, коптильня —
 * все они используют одно и то же событие начала готовки).
 */
public class FurnaceSlowdownListener implements Listener {

    private final HardMode plugin;

    public FurnaceSlowdownListener(HardMode plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onSmeltStart(FurnaceStartSmeltEvent event) {
        double multiplier = plugin.getConfig().getDouble("furnace-slowdown-multiplier", 1.5);
        int newTime = (int) (event.getTotalCookTime() * multiplier);
        event.setTotalCookTime(newTime);
    }
}
