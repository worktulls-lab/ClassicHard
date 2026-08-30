package ru.dplus.hardmode.listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Door;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Zombie;
import org.bukkit.scheduler.BukkitRunnable;
import ru.dplus.hardmode.HardMode;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Ванильные зомби ломают двери только на сложности Hard, и делают это очень
 * медленно (таймер зашит в закрытый AI, публичного доступа к нему нет).
 * Реализован независимый, более быстрый механизм: если зомби с целью стоит
 * рядом с закрытой деревянной дверью, счётчик прогресса растёт, и по
 * достижении порога дверь ломается (удаляется) напрямую — работает на любой
 * сложности сервера и значительно быстрее ванильного поведения.
 */
public class ZombieDoorBreakTask extends BukkitRunnable {

    private final HardMode plugin;
    private final Map<String, Integer> doorProgress = new HashMap<>();

    public ZombieDoorBreakTask(HardMode plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        if (!plugin.getConfig().getBoolean("zombie-door-break-enabled", true)) return;

        int thresholdTicks = plugin.getConfig().getInt("zombie-door-break-ticks", 60);
        int checkPeriod = plugin.getConfig().getInt("zombie-door-break-check-period-ticks", 10);

        Set<String> stillContested = new HashSet<>();

        for (World world : plugin.getServer().getWorlds()) {
            for (Zombie zombie : world.getEntitiesByClass(Zombie.class)) {
                LivingEntity target = zombie.getTarget();
                if (target == null) continue;

                Block door = findAdjacentWoodenDoor(zombie);
                if (door == null) continue;

                String key = key(door);
                stillContested.add(key);
                int progress = doorProgress.getOrDefault(key, 0) + checkPeriod;

                if (progress >= thresholdTicks) {
                    breakDoor(door);
                    doorProgress.remove(key);
                    stillContested.remove(key);
                } else {
                    doorProgress.put(key, progress);
                }
            }
        }

        // двери, у которых в этом цикле не было ни одного зомби рядом,
        // больше не отслеживаем — иначе карта прогресса растёт бесконечно
        doorProgress.keySet().retainAll(stillContested);
    }

    private Block findAdjacentWoodenDoor(Zombie zombie) {
        Location loc = zombie.getLocation();
        World world = loc.getWorld();
        int bx = loc.getBlockX();
        int by = loc.getBlockY();
        int bz = loc.getBlockZ();

        int[][] offsets = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        for (int[] offset : offsets) {
            Block block = world.getBlockAt(bx + offset[0], by, bz + offset[1]);
            if (isClosedWoodenDoor(block)) return block;
            Block blockAbove = world.getBlockAt(bx + offset[0], by + 1, bz + offset[1]);
            if (isClosedWoodenDoor(blockAbove)) return blockAbove;
        }
        return null;
    }

    private boolean isClosedWoodenDoor(Block block) {
        if (block.getType() == Material.IRON_DOOR) return false;
        if (!(block.getBlockData() instanceof Door door)) return false;
        return !door.isOpen();
    }

    private void breakDoor(Block block) {
        BlockData data = block.getBlockData();
        if (!(data instanceof Door door)) return;

        Block bottom = door.getHalf() == Bisected.Half.BOTTOM ? block : block.getRelative(0, -1, 0);
        Block top = door.getHalf() == Bisected.Half.TOP ? block : block.getRelative(0, 1, 0);

        block.getWorld().playSound(block.getLocation(), Sound.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, 1f, 1f);
        bottom.setType(Material.AIR, false);
        top.setType(Material.AIR, false);
    }

    private String key(Block block) {
        return block.getWorld().getName() + ";" + block.getX() + ";" + block.getY() + ";" + block.getZ();
    }
}
