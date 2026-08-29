package ru.dplus.hardmode.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Ravager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;
import ru.dplus.hardmode.HardMode;

public class RavagerShieldListener implements Listener {

    private final HardMode plugin;

    public RavagerShieldListener(HardMode plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onRavagerHit(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Ravager ravager)) return;
        if (!(event.getEntity() instanceof Player player)) return;
        if (!player.isBlocking()) return;

        double knockbackMult = plugin.getConfig().getDouble("ravager-shield-knockback-multiplier", 1.8);
        Vector push = player.getLocation().toVector()
                .subtract(ravager.getLocation().toVector())
                .normalize()
                .multiply(knockbackMult);
        push.setY(Math.max(push.getY(), 0.35));
        player.setVelocity(player.getVelocity().add(push));

        int extraDamage = plugin.getConfig().getInt("ravager-shield-extra-damage", 3);
        ItemStack mainHand = player.getInventory().getItemInMainHand();
        ItemStack shield = mainHand.getType() == Material.SHIELD ? mainHand : player.getInventory().getItemInOffHand();

        if (shield.getType() == Material.SHIELD) {
            ItemMeta meta = shield.getItemMeta();
            if (meta instanceof Damageable damageable) {
                damageable.setDamage(damageable.getDamage() + extraDamage);
                shield.setItemMeta(meta);
            }
        }
    }
}
