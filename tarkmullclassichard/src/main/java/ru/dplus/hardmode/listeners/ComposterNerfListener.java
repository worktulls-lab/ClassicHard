package ru.dplus.hardmode.listeners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Levelled;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import ru.dplus.hardmode.HardMode;

import java.util.Random;

/**
 * Композтер требует больше отходов для повышения уровня. Публичного способа
 * снизить сам шанс повышения уровня нет, поэтому здесь после успешного
 * повышения (проверяется на следующий тик после клика) есть шанс откатить
 * уровень обратно на 1 — по факту заставляет докладывать больше отходов,
 * чтобы добиться того же результата.
 */
public class ComposterNerfListener implements Listener {

    private final HardMode plugin;
    private final Random random = new Random();

    public ComposterNerfListener(HardMode plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Block block = event.getClickedBlock();
        if (block == null || block.getType() != Material.COMPOSTER) return;
        if (!(block.getBlockData() instanceof Levelled levelled)) return;

        int levelBefore = levelled.getLevel();
        if (levelBefore >= levelled.getMaximumLevel()) return;

        double revertChance = plugin.getConfig().getDouble("composter-revert-chance", 0.35);

        plugin.getServer().getScheduler().runTask(plugin, () -> {
            if (!(block.getBlockData() instanceof Levelled after)) return;
            if (after.getLevel() <= levelBefore) return;

            if (random.nextDouble() < revertChance) {
                after.setLevel(after.getLevel() - 1);
                block.setBlockData(after);
            }
        });
    }
}
