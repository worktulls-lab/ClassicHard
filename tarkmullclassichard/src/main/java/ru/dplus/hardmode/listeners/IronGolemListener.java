package ru.dplus.hardmode.listeners;

import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.scheduler.BukkitRunnable;
import ru.dplus.hardmode.HardMode;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Железный голем раньше не входил ни в одну из категорий плагина (не Monster,
 * не Animals) и не получал никаких бафов вообще. Тут два эффекта:
 *
 * 1) HP — просто множится через атрибут, как у всех.
 *
 * 2) "Дальность удара" — у мобов в Bukkit нет публичного атрибута для
 * реальной дистанции ближней атаки (в отличие от дальности преследования/
 * обнаружения, ATTACK_KNOCKBACK тут не подходит — это сила отбрасывания,
 * а не дистанция, и у голема она по умолчанию около нуля, так что просто
 * умножать её бессмысленно). Вместо этого раз в полсекунды проверяется:
 * если цель голема чуть дальше обычной дистанции удара (~1.7 блока), но в
 * пределах увеличенного радиуса — голем всё равно наносит удар напрямую.
 * На практике ощущается как удар большей дальности, с кулдауном, чтобы не
 * бить чаще обычного.
 */
public class IronGolemListener extends BukkitRunnable implements Listener {

    private static final double VANILLA_REACH = 1.7;

    private final HardMode plugin;
    private final Map<UUID, Long> lastExtraHit = new HashMap<>();

    public IronGolemListener(HardMode plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onSpawn(CreatureSpawnEvent event) {
        LivingEntity entity = event.getEntity();
        if (!(entity instanceof IronGolem)) return;

        double healthMult = plugin.getConfig().getDouble("iron-golem-health-multiplier", 1.5);
        AttributeInstance maxHealth = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (maxHealth != null) {
            maxHealth.setBaseValue(maxHealth.getBaseValue() * healthMult);
            entity.setHealth(maxHealth.getValue());
        }
    }

    @Override
    public void run() {
        double reachMult = plugin.getConfig().getDouble("iron-golem-attack-range-multiplier", 1.2);
        double extendedReach = VANILLA_REACH * reachMult;
        long cooldownTicks = plugin.getConfig().getLong("iron-golem-extra-hit-cooldown-ticks", 20L);
        long cooldownMillis = cooldownTicks * 50L;
        long nowMillis = System.currentTimeMillis();

        for (World world : plugin.getServer().getWorlds()) {
            for (IronGolem golem : world.getEntitiesByClass(IronGolem.class)) {
                LivingEntity target = golem.getTarget();
                if (target == null || !target.isValid()) continue;

                double distance = golem.getLocation().distance(target.getLocation());
                if (distance <= VANILLA_REACH || distance > extendedReach) continue;

                UUID id = golem.getUniqueId();
                Long last = lastExtraHit.get(id);
                if (last != null && nowMillis - last < cooldownMillis) continue;

                AttributeInstance dmgAttr = golem.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE);
                double damage = dmgAttr != null ? dmgAttr.getValue() : 7.0;
                target.damage(damage, golem);
                lastExtraHit.put(id, nowMillis);
            }
        }
    }
}
