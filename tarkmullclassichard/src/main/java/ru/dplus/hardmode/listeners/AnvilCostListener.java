package ru.dplus.hardmode.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import ru.dplus.hardmode.HardMode;

/**
 * Делает работу с наковальней дороже: стоимость операции в уровнях опыта
 * умножается. Вместе с уменьшенным получением опыта (ExperienceNerfListener)
 * это заметно замедляет ремонт и зачарование.
 */
public class AnvilCostListener implements Listener {

    private final HardMode plugin;

    public AnvilCostListener(HardMode plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPrepareAnvil(PrepareAnvilEvent event) {
        if (event.getResult() == null) return;

        double multiplier = plugin.getConfig().getDouble("anvil-cost-multiplier", 1.5);
        int currentCost = event.getInventory().getRepairCost();
        if (currentCost <= 0) return;

        event.getInventory().setRepairCost((int) Math.ceil(currentCost * multiplier));
    }
}
