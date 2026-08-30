package ru.dplus.hardmode.listeners;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;
import ru.dplus.hardmode.HardMode;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

/**
 * В ваниле разозлённый (после взгляда в глаза) эндермен добирается до игрока
 * постепенно, рывками телепортируясь во время погони. Здесь в момент, когда
 * он впервые нацеливается на игрока, сразу телепортируется вплотную —
 * никакой "постепенной злости", сразу оказывается рядом.
 *
 * Отдельного события "эндермен разозлился от взгляда" в публичном API нет,
 * поэтому используется общее EntityTargetEvent — оно срабатывает при любой
 * смене цели, что для эндермена практически всегда и означает этот момент.
 */
public class EndermanAggroTeleportListener implements Listener {

    private final HardMode plugin;
    private final Random random = new Random();
    private final Map<UUID, Long> lastTeleport = new HashMap<>();

    public EndermanAggroTeleportListener(HardMode plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onTarget(EntityTargetEvent event) {
        if (!plugin.getConfig().getBoolean("enderman-instant-teleport-on-aggro", true)) return;
        if (!(event.getEntity() instanceof Enderman enderman)) return;
        if (!(event.getTarget() instanceof Player player)) return;

        long cooldownMs = plugin.getConfig().getLong("enderman-instant-teleport-cooldown-ms", 1000L);
        long now = System.currentTimeMillis();

        if (lastTeleport.size() > 500) {
            // попутная очистка — эндермены исчезают/умирают, а без этого
            // карта кулдаунов росла бы бесконечно на долгоживущем сервере
            lastTeleport.values().removeIf(ts -> now - ts > cooldownMs);
        }

        Long last = lastTeleport.get(enderman.getUniqueId());
        if (last != null && now - last < cooldownMs) return;

        int radius = plugin.getConfig().getInt("enderman-instant-teleport-radius", 2);
        Location spot = findSpotNear(player, radius);
        if (spot == null) return;

        World world = enderman.getWorld();
        world.spawnParticle(Particle.PORTAL, enderman.getLocation(), 32, 0.5, 1, 0.5, 0.1);
        enderman.teleport(spot);
        world.spawnParticle(Particle.PORTAL, spot, 32, 0.5, 1, 0.5, 0.1);
        world.playSound(spot, Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f);

        lastTeleport.put(enderman.getUniqueId(), now);
    }

    private Location findSpotNear(Player player, int radius) {
        World world = player.getWorld();
        Location base = player.getLocation();
        for (int i = 0; i < 8; i++) {
            int dx = random.nextInt(radius * 2 + 1) - radius;
            int dz = random.nextInt(radius * 2 + 1) - radius;
            int x = base.getBlockX() + dx;
            int z = base.getBlockZ() + dz;
            int y = base.getBlockY();
            Location loc = new Location(world, x + 0.5, y, z + 0.5);
            if (loc.getBlock().getType().isAir()
                    && loc.clone().add(0, 1, 0).getBlock().getType().isAir()) {
                return loc;
            }
        }
        return null;
    }
}
