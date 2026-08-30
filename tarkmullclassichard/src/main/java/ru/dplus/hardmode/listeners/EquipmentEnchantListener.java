package ru.dplus.hardmode.listeners;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import ru.dplus.hardmode.HardMode;

import java.util.Random;

/**
 * Небольшой шанс, что зомби/скелет заспавнится с зачарованным (уровень I)
 * оружием. Шанс намеренно низкий — урон мобов и так уже увеличен x2.
 */
public class EquipmentEnchantListener implements Listener {

    private final HardMode plugin;
    private final Random random = new Random();

    public EquipmentEnchantListener(HardMode plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onSpawn(CreatureSpawnEvent event) {
        LivingEntity entity = event.getEntity();
        if (!(entity instanceof Skeleton) && !(entity instanceof Zombie)) return;

        double chance = plugin.getConfig().getDouble("mob-enchanted-gear-chance", 0.08);
        if (random.nextDouble() >= chance) return;

        EntityEquipment eq = entity.getEquipment();
        if (eq == null) return;

        ItemStack weapon = eq.getItemInMainHand();
        if (weapon == null || weapon.getType() == Material.AIR) return;

        Enchantment enchant = weapon.getType() == Material.BOW ? Enchantment.POWER : Enchantment.SHARPNESS;
        weapon.addUnsafeEnchantment(enchant, 1);
        eq.setItemInMainHand(weapon);
        eq.setItemInMainHandDropChance(0f);
    }
}
