package ru.dplus.hardmode.listeners;

import org.bukkit.entity.Bee;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import ru.dplus.hardmode.HardMode;

/**
 * В ваниле пчела теряет жало после укуса и вскоре погибает, не имея
 * возможности ужалить снова. Здесь сразу после укуса состояние "ужалила"
 * сбрасывается — пчела может жалить многократно и не умирает от этого.
 */
public class BeeStingerListener implements Listener {

    private final HardMode plugin;

    public BeeStingerListener(HardMode plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onSting(EntityDamageByEntityEvent event) {
        if (!plugin.getConfig().getBoolean("bee-keep-stinger", true)) return;
        if (!(event.getDamager() instanceof Bee bee)) return;

        bee.setHasStung(false);
    }
}
