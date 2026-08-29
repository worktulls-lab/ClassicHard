package ru.dplus.hardmode.listeners;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Warden;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.potion.PotionEffectType;
import ru.dplus.hardmode.HardMode;

import java.util.Random;

/**
 * "Тьма" применяется только Варденом (рёв) или Sculk Shrieker'ом. Если рядом
 * нет Вардена — считаем, что сработал шрайкер, и добавляем свой шанс призвать
 * Вардена сверху ванильного (у самого шрайкера он довольно низкий).
 */
public class SculkShriekerListener implements Listener {

    private final HardMode plugin;
    private final Random random = new Random();

    public SculkShriekerListener(HardMode plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onDarkness(EntityPotionEffectEvent event) {
        if (event.getNewEffect() == null || event.getNewEffect().getType() != PotionEffectType.DARKNESS) return;
        if (!(event.getEntity() instanceof Player player)) return;

        boolean wardenNearby = player.getWorld()
                .getNearbyEntities(player.getLocation(), 24, 24, 24)
                .stream().anyMatch(e -> e instanceof Warden);
        if (wardenNearby) return; // это рёв вардена, не шрайкер

        double chance = plugin.getConfig().getDouble("shrieker-extra-warden-chance", 0.15);
        if (random.nextDouble() >= chance) return;

        Location spot = findSafeSpot(player, 8);
        if (spot != null) {
            player.getWorld().spawnEntity(spot, EntityType.WARDEN);
        }
    }

    private Location findSafeSpot(Player player, int radius) {
        World world = player.getWorld();
        Location base = player.getLocation();
        for (int i = 0; i < 8; i++) {
            int dx = random.nextInt(radius * 2 + 1) - radius;
            int dz = random.nextInt(radius * 2 + 1) - radius;
            int x = base.getBlockX() + dx;
            int z = base.getBlockZ() + dz;
            int y = base.getBlockY();
            Location loc = new Location(world, x + 0.5, y, z + 0.5);
            if (loc.getBlock().getType().isAir()) return loc;
        }
        return null;
    }
}
