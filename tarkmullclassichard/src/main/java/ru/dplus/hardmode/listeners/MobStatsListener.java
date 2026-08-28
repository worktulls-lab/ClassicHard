package ru.dplus.hardmode.listeners;

import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.MagmaCube;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Phantom;
import org.bukkit.entity.Piglin;
import org.bukkit.entity.PiglinBrute;
import org.bukkit.entity.Shulker;
import org.bukkit.entity.Silverfish;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Warden;
import org.bukkit.entity.Wither;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import ru.dplus.hardmode.HardMode;

import java.util.Random;

/**
 * Усиливает характеристики враждебных мобов при спавне:
 * HP, урон в ближнем бою, скорость, дальность преследования и сопротивление отдаче.
 * Боссы (Дракон, Иссушитель, Варден) получают отдельный, более жёсткий набор множителей.
 */
public class MobStatsListener implements Listener {

    private final HardMode plugin;
    private final Random random = new Random();

    public MobStatsListener(HardMode plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onSpawn(CreatureSpawnEvent event) {
        LivingEntity entity = event.getEntity();
        if (!isHostile(entity)) return;

        FileConfiguration cfg = plugin.getConfig();
        boolean isBoss = entity instanceof EnderDragon || entity instanceof Wither || entity instanceof Warden;
        boolean isCreeper = entity instanceof Creeper;

        double healthMult;
        double damageMult;

        if (isBoss) {
            healthMult = cfg.getDouble("boss-health-multiplier", 3.0);
            damageMult = cfg.getDouble("boss-damage-multiplier", 1.6);
        } else if (isCreeper) {
            // у крипера нет обычной атаки ближним боем, урон от взрыва множится отдельно
            healthMult = cfg.getDouble("creeper-health-multiplier", 1.5);
            damageMult = 1.0;
        } else {
            healthMult = cfg.getDouble("mob-health-multiplier", 2.0);
            damageMult = cfg.getDouble("mob-damage-multiplier", 2.0);
        }

        double speedMult = cfg.getDouble("mob-speed-multiplier", 1.3);
        double followMult = cfg.getDouble("mob-follow-range-multiplier", 1.8);
        double knockbackRes = cfg.getDouble("mob-knockback-resistance", 0.6);

        multiplyAttribute(entity, Attribute.GENERIC_MAX_HEALTH, healthMult);
        AttributeInstance maxHealthAttr = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (maxHealthAttr != null) {
            entity.setHealth(maxHealthAttr.getValue());
        }

        if (damageMult != 1.0) {
            multiplyAttribute(entity, Attribute.GENERIC_ATTACK_DAMAGE, damageMult);
        }

        multiplyAttribute(entity, Attribute.GENERIC_MOVEMENT_SPEED, speedMult);
        multiplyAttribute(entity, Attribute.GENERIC_FOLLOW_RANGE, followMult);
        raiseMinAttribute(entity, Attribute.GENERIC_KNOCKBACK_RESISTANCE, knockbackRes);

        if (entity instanceof Silverfish) {
            // сверх общего mob-speed-multiplier — быстрее находят и заражают блоки
            double extraSpeed = cfg.getDouble("silverfish-extra-speed-multiplier", 1.3);
            multiplyAttribute(entity, Attribute.GENERIC_MOVEMENT_SPEED, extraSpeed);
        }

        if (entity instanceof Zombie zombie && zombie.isBaby()) {
            // бэби-зомби (включая утопленников/husk/зомби-жителей) и так быстрее
            // взрослых в ваниле — добавляем ещё сверху общего множителя
            double extraSpeed = cfg.getDouble("baby-zombie-extra-speed-multiplier", 1.3);
            multiplyAttribute(entity, Attribute.GENERIC_MOVEMENT_SPEED, extraSpeed);
        }

        if (entity instanceof Piglin piglin && !(entity instanceof PiglinBrute)) {
            double chance = cfg.getDouble("piglin-armor-chance", 1.0);
            if (random.nextDouble() < chance) {
                equipGoldArmor(piglin);
            }
        }
    }

    private boolean isHostile(LivingEntity entity) {
        return entity instanceof Monster
                || entity instanceof EnderDragon
                || entity instanceof Wither
                || entity instanceof Warden
                || entity instanceof Ghast
                || entity instanceof Phantom
                || entity instanceof Shulker
                || entity instanceof Slime
                || entity instanceof MagmaCube;
    }

    private void multiplyAttribute(LivingEntity entity, Attribute attribute, double multiplier) {
        AttributeInstance instance = entity.getAttribute(attribute);
        if (instance == null) return;
        instance.setBaseValue(instance.getBaseValue() * multiplier);
    }

    private void raiseMinAttribute(LivingEntity entity, Attribute attribute, double minValue) {
        AttributeInstance instance = entity.getAttribute(attribute);
        if (instance == null) return;
        double clamped = Math.min(Math.max(minValue, 0.0), 1.0);
        if (instance.getBaseValue() < clamped) {
            instance.setBaseValue(clamped);
        }
    }

    private void equipGoldArmor(Piglin piglin) {
        EntityEquipment eq = piglin.getEquipment();
        if (eq == null) return;
        eq.setHelmet(new ItemStack(Material.GOLDEN_HELMET));
        eq.setChestplate(new ItemStack(Material.GOLDEN_CHESTPLATE));
        eq.setLeggings(new ItemStack(Material.GOLDEN_LEGGINGS));
        eq.setBoots(new ItemStack(Material.GOLDEN_BOOTS));
        eq.setHelmetDropChance(0f);
        eq.setChestplateDropChance(0f);
        eq.setLeggingsDropChance(0f);
        eq.setBootsDropChance(0f);
    }
}
