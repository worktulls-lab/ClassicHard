package ru.dplus.hardmode.listeners;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.entity.Witch;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.potion.PotionEffect;
import ru.dplus.hardmode.HardMode;

/**
 * Скорость броска зелий у ведьмы не регулируется публичным API (это часть
 * захардкоженного AI моба), поэтому вместо "чаще кидает" усилены сами зелья:
 * дольше действуют негативные эффекты и больше урон от Зелья Урона.
 */
public class WitchListener implements Listener {

    private final HardMode plugin;

    public WitchListener(HardMode plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPotionSplash(PotionSplashEvent event) {
        ThrownPotion potion = event.getPotion();
        if (!(potion.getShooter() instanceof Witch)) return;

        double multiplier = plugin.getConfig().getDouble("witch-potion-duration-multiplier", 1.5);
        for (LivingEntity affected : event.getAffectedEntities()) {
            for (PotionEffect effect : potion.getEffects()) {
                int newDuration = (int) (effect.getDuration() * multiplier);
                affected.addPotionEffect(new PotionEffect(
                        effect.getType(), newDuration, effect.getAmplifier(),
                        effect.isAmbient(), effect.hasParticles(), effect.hasIcon()));
            }
        }
    }

    @EventHandler
    public void onHarmingDamage(EntityDamageByEntityEvent event) {
        if (event.getCause() != EntityDamageEvent.DamageCause.MAGIC) return;
        if (!(event.getDamager() instanceof ThrownPotion potion)) return;
        if (!(potion.getShooter() instanceof Witch)) return;

        double multiplier = plugin.getConfig().getDouble("witch-harming-damage-multiplier", 1.5);
        event.setDamage(event.getDamage() * multiplier);
    }
}
