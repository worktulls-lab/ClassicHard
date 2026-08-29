package ru.dplus.hardmode.listeners;

import org.bukkit.Location;
import org.bukkit.entity.Endermite;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import ru.dplus.hardmode.HardMode;

import java.util.Random;

/**
 * В ваниле у эндер-жемчуга и так есть небольшой (~5%) шанс заспавнить
 * эндермита в точке приземления. Публичного способа проверить, сработал ли
 * этот ванильный шанс, нет, поэтому здесь добавляется НЕЗАВИСИМЫЙ
 * дополнительный шанс поверх ванильного.
 */
public class EnderPearlListener implements Listener {

    private final HardMode plugin;
    private final Random random = new Random();

    public EnderPearlListener(HardMode plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPearlTeleport(PlayerTeleportEvent event) {
        if (event.getCause() != PlayerTeleportEvent.TeleportCause.ENDER_PEARL) return;

        double chance = plugin.getConfig().getDouble("enderpearl-extra-endermite-chance", 0.15);
        if (random.nextDouble() >= chance) return;

        Player player = event.getPlayer();
        Location spawnLoc = event.getTo();
        if (spawnLoc == null) return;

        spawnLoc.getWorld().spawn(spawnLoc, Endermite.class);
    }
}
