package ru.dplus.hardmode.listeners;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Wither;
import org.bukkit.entity.WitherSkull;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.WitherShootSkullEvent;
import org.bukkit.util.Vector;
import ru.dplus.hardmode.HardMode;

import java.util.Random;

/**
 * Не увеличивает урон черепов напрямую (это уже покрыто boss-damage-multiplier
 * в IndirectDamageListener), а добавляет шанс, что Иссушитель выпустит
 * дополнительный череп впридачу к обычному — атакует чаще и "гуще".
 * Также ускоряет лечение Дракона Края от кристаллов.
 */
public class BossSpectacleListener implements Listener {

    private final HardMode plugin;
    private final Random random = new Random();

    public BossSpectacleListener(HardMode plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onWitherShootSkull(WitherShootSkullEvent event) {
        double chance = plugin.getConfig().getDouble("wither-extra-skull-chance", 0.35);
        if (random.nextDouble() >= chance) return;

        Wither wither = event.getWither();
        LivingEntity target = event.getTarget();
        if (target == null) return;

        Location from = wither.getEyeLocation();
        Vector direction = target.getEyeLocation().toVector()
                .subtract(from.toVector())
                .normalize();

        shootSkull(wither, from, direction, event.isCharged());

        double doubleChance = plugin.getConfig().getDouble("wither-double-skull-chance", 0.25);
        if (random.nextDouble() < doubleChance) {
            Vector spread = rotateAroundY(direction, 15.0);
            shootSkull(wither, from, spread, event.isCharged());
        }
    }

    @EventHandler
    public void onDragonCrystalHeal(EntityRegainHealthEvent event) {
        if (event.getRegainReason() != EntityRegainHealthEvent.RegainReason.ENDER_CRYSTAL) return;

        double multiplier = plugin.getConfig().getDouble("dragon-crystal-heal-multiplier", 2.0);
        event.setAmount(event.getAmount() * multiplier);
    }

    private void shootSkull(Wither wither, Location from, Vector direction, boolean charged) {
        WitherSkull skull = wither.getWorld().spawn(from, WitherSkull.class);
        skull.setShooter(wither);
        skull.setDirection(direction);
        skull.setCharged(charged);
    }

    private Vector rotateAroundY(Vector vector, double degrees) {
        double rad = Math.toRadians(degrees);
        double cos = Math.cos(rad);
        double sin = Math.sin(rad);
        double x = vector.getX() * cos - vector.getZ() * sin;
        double z = vector.getX() * sin + vector.getZ() * cos;
        return new Vector(x, vector.getY(), z);
    }
}
