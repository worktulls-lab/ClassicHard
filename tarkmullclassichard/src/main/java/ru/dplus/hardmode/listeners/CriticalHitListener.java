package ru.dplus.hardmode.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffectType;
import ru.dplus.hardmode.HardMode;

/**
 * Слегка снижает бонус от критического удара (прыжковой атаки) игрока.
 * Публичный API не даёт напрямую узнать, был ли конкретный удар
 * критическим — здесь используется тот же набор условий, что и в самой
 * ванили для решения, засчитывать крит или нет (падение, не на земле, не
 * спринт, без слепоты/левитации, не в транспорте). Это приближение, а не
 * 100% точное определение — возможны редкие ложные срабатывания в обе
 * стороны.
 */
public class CriticalHitListener implements Listener {

    private final HardMode plugin;

    public CriticalHitListener(HardMode plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerAttack(EntityDamageByEntityEvent event) {
        if (event.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) return;
        if (!(event.getDamager() instanceof Player player)) return;

        boolean likelyCrit = player.getFallDistance() > 0.0f
                && !player.isOnGround()
                && !player.isSprinting()
                && !player.hasPotionEffect(PotionEffectType.BLINDNESS)
                && !player.hasPotionEffect(PotionEffectType.LEVITATION)
                && !player.isInsideVehicle();

        if (!likelyCrit) return;

        double reduction = plugin.getConfig().getDouble("critical-hit-reduction-multiplier", 0.9);
        event.setDamage(event.getDamage() * reduction);
    }
}
