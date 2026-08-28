package ru.dplus.hardmode.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import ru.dplus.hardmode.HardMode;

/**
 * Ослабляет природную регенерацию (от сытости) и ускоряет падение голода,
 * чтобы сложнее было "пересидеть" на еде.
 */
public class SurvivalHarshnessListener implements Listener {

    private final HardMode plugin;

    public SurvivalHarshnessListener(HardMode plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onNaturalRegen(EntityRegainHealthEvent event) {
        if (event.getRegainReason() != EntityRegainHealthEvent.RegainReason.SATIATED) return;
        if (!(event.getEntity() instanceof Player)) return;

        double multiplier = plugin.getConfig().getDouble("natural-regen-multiplier", 0.3);
        event.setAmount(event.getAmount() * multiplier);
    }

    @EventHandler
    public void onFoodLoss(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        int current = player.getFoodLevel();
        int incoming = event.getFoodLevel();
        if (incoming >= current) return; // игрок ест/восстанавливает голод — не трогаем

        double multiplier = plugin.getConfig().getDouble("hunger-loss-multiplier", 1.3);
        int drop = current - incoming;
        int extraDrop = (int) Math.ceil(drop * multiplier);
        int newLevel = Math.max(0, current - extraDrop);
        event.setFoodLevel(newLevel);
    }
}
