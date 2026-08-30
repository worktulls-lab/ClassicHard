package ru.dplus.hardmode.listeners;

import org.bukkit.entity.Slime;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.SlimeSplitEvent;
import ru.dplus.hardmode.HardMode;

/**
 * Слаймы и магма-кубы (MagmaCube — подтип Slime в Bukkit) при делении дают
 * больше осколков. Сами осколки уже автоматически получают обычный баф
 * (HP/урон) через MobStatsListener при своём спавне — тут только количество.
 */
public class SlimeSplitListener implements Listener {

    private final HardMode plugin;

    public SlimeSplitListener(HardMode plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onSplit(SlimeSplitEvent event) {
        if (!(event.getEntity() instanceof Slime)) return;

        int extra = plugin.getConfig().getInt("slime-split-extra-count", 2);
        event.setCount(event.getCount() + extra);
    }
}
