package ru.dplus.hardmode.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.potion.PotionEffect;
import ru.dplus.hardmode.HardMode;

import java.util.ArrayList;
import java.util.List;

/**
 * Молоко больше не снимает негативные эффекты полностью — вместо этого их
 * длительность сокращается вдвое.
 *
 * Как это работает: ванильное снятие эффектов происходит уже ПОСЛЕ события
 * потребления, поэтому здесь эффекты запоминаются до этого, а на следующий
 * тик (когда ваниль их уже сняла) возвращаются обратно с укороченной
 * длительностью. Возвращаются только негативные эффекты — полезные
 * (Сила, Скорость и т.п.) молоко по-прежнему снимает полностью, как в ваниле.
 */
public class MilkNerfListener implements Listener {

    private final HardMode plugin;

    public MilkNerfListener(HardMode plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onConsume(PlayerItemConsumeEvent event) {
        if (event.getItem().getType() != Material.MILK_BUCKET) return;
        if (!plugin.getConfig().getBoolean("milk-partial-cure-enabled", true)) return;

        Player player = event.getPlayer();
        double multiplier = plugin.getConfig().getDouble("milk-remaining-duration-multiplier", 0.5);

        List<PotionEffect> toRestore = new ArrayList<>();
        for (PotionEffect effect : player.getActivePotionEffects()) {
            if (!isNegative(effect)) continue;

            int newDuration = (int) (effect.getDuration() * multiplier);
            if (newDuration <= 0) continue;

            toRestore.add(new PotionEffect(effect.getType(), newDuration, effect.getAmplifier(),
                    effect.isAmbient(), effect.hasParticles(), effect.hasIcon()));
        }

        if (toRestore.isEmpty()) return;

        plugin.getServer().getScheduler().runTask(plugin, () -> {
            for (PotionEffect effect : toRestore) {
                player.addPotionEffect(effect);
            }
        });
    }

    private boolean isNegative(PotionEffect effect) {
        // Названия типов эффектов стабильны и не зависят от версии API так,
        // как сами объекты PotionEffectType, поэтому сравнение идёт по имени.
        String name = effect.getType().getKey().getKey().toUpperCase();
        return switch (name) {
            case "POISON", "WITHER", "SLOWNESS", "MINING_FATIGUE", "WEAKNESS",
                 "NAUSEA", "BLINDNESS", "HUNGER", "LEVITATION", "UNLUCK",
                 "DARKNESS", "INSTANT_DAMAGE", "BAD_OMEN", "GLOWING",
                 "INFESTED", "OOZING", "WEAVING", "WIND_CHARGED", "TRIAL_OMEN",
                 "RAID_OMEN" -> true;
            default -> false;
        };
    }
}
