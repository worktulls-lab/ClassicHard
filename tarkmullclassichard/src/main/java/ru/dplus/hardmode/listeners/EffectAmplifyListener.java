package ru.dplus.hardmode.listeners;

import org.bukkit.entity.ElderGuardian;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Shulker;
import org.bukkit.entity.Warden;
import org.bukkit.entity.WitherSkeleton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.dplus.hardmode.HardMode;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Продлевает "Тьму" (Darkness), которую применяет рёв Вардена, усиливает
 * "Усталость горняка" (Mining Fatigue) от старейшего стража рядом с монументом,
 * продлевает "Левитацию" (Levitation) от снаряда шалкера, и усиливает
 * "Иссушение" (Wither) от удара витер-скелета.
 * Все эффекты распознаются не по причине применения (это не всегда стабильно
 * доступно в публичном API), а по факту, что нужный моб есть рядом с целью.
 */
public class EffectAmplifyListener implements Listener {

    private final HardMode plugin;
    private final Set<UUID> reapplying = new HashSet<>();

    public EffectAmplifyListener(HardMode plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEffectApplied(EntityPotionEffectEvent event) {
        PotionEffect newEffect = event.getNewEffect();
        if (newEffect == null) return;
        if (!(event.getEntity() instanceof LivingEntity target)) return;

        UUID id = target.getUniqueId();
        if (reapplying.contains(id)) {
            reapplying.remove(id);
            return;
        }

        if (newEffect.getType() == PotionEffectType.DARKNESS) {
            handleDarkness(target, newEffect);
        } else if (newEffect.getType() == PotionEffectType.MINING_FATIGUE && target instanceof Player) {
            handleFatigue((Player) target, newEffect);
        } else if (newEffect.getType() == PotionEffectType.LEVITATION) {
            handleLevitation(target, newEffect);
        } else if (newEffect.getType() == PotionEffectType.WITHER) {
            handleWither(target, newEffect);
        }
    }

    private void handleDarkness(LivingEntity target, PotionEffect effect) {
        boolean wardenNearby = target.getWorld()
                .getNearbyEntities(target.getLocation(), 24, 24, 24)
                .stream().anyMatch(e -> e instanceof Warden);
        if (!wardenNearby) return;

        double multiplier = plugin.getConfig().getDouble("warden-roar-darkness-multiplier", 1.6);
        reapplyLater(target, effect, multiplier, 0);
    }

    private void handleFatigue(Player player, PotionEffect effect) {
        boolean elderNearby = player.getWorld()
                .getNearbyEntities(player.getLocation(), 32, 32, 32)
                .stream().anyMatch(e -> e instanceof ElderGuardian);
        if (!elderNearby) return;

        double multiplier = plugin.getConfig().getDouble("guardian-fatigue-multiplier", 1.5);
        reapplyLater(player, effect, multiplier, 1);
    }

    private void handleLevitation(LivingEntity target, PotionEffect effect) {
        boolean shulkerNearby = target.getWorld()
                .getNearbyEntities(target.getLocation(), 24, 24, 24)
                .stream().anyMatch(e -> e instanceof Shulker);
        if (!shulkerNearby) return;

        double multiplier = plugin.getConfig().getDouble("shulker-levitation-multiplier", 1.7);
        reapplyLater(target, effect, multiplier, 0);
    }

    private void handleWither(LivingEntity target, PotionEffect effect) {
        boolean witherSkeletonNearby = target.getWorld()
                .getNearbyEntities(target.getLocation(), 8, 8, 8)
                .stream().anyMatch(e -> e instanceof WitherSkeleton);
        if (!witherSkeletonNearby) return;

        double multiplier = plugin.getConfig().getDouble("wither-skeleton-effect-multiplier", 1.7);
        reapplyLater(target, effect, multiplier, 1);
    }

    private void reapplyLater(LivingEntity target, PotionEffect effect, double durationMultiplier, int extraAmplifier) {
        UUID id = target.getUniqueId();
        int newDuration = (int) (effect.getDuration() * durationMultiplier);
        int newAmplifier = effect.getAmplifier() + extraAmplifier;

        reapplying.add(id);
        plugin.getServer().getScheduler().runTask(plugin, () ->
                target.addPotionEffect(new PotionEffect(effect.getType(), newDuration, newAmplifier, true, true)));
    }
}
