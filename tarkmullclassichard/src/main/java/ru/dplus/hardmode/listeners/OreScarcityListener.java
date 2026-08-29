package ru.dplus.hardmode.listeners;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import ru.dplus.hardmode.HardMode;

import java.util.EnumMap;
import java.util.Map;
import java.util.Random;

/**
 * При первой генерации чанка с некоторым шансом заменяет часть руды обратно
 * на обычный камень/делеслейт/незеррак — руды в мире становится заметно
 * меньше.
 *
 * ВАЖНО: срабатывает ТОЛЬКО для чанков, которые генерируются заново
 * (event.isNewChunk() == true). Это НЕ ретроактивная фича — уже
 * сгенерированные до установки плагина чанки (например, спавн и всё, что
 * игроки уже успели исследовать на старом мире) не трогаются вообще никогда.
 * Если тестировать возле спавна на существующем мире — разницы не будет
 * видно, потому что эти чанки уже не "новые". Для проверки нужно либо
 * создать новый мир, либо лететь/бежать в ещё не сгенерированную местность.
 *
 * Сканирует чанк блок за блоком синхронно по всей высоте мира (раньше было
 * искусственно обрезано до y=200 — из-за этого золотая руда в бэдлендах
 * (генерируется до y=256) и изумрудная в горах (до y=320) вообще не
 * попадали под замену). При быстрой генерации большого количества чанков
 * подряд (полёт на элитре по новой местности) возможны небольшие просадки —
 * если это станет проблемой, снизьте ore-scarcity-chance.
 * Ancient Debris намеренно не тронут — он и так достаточно редкий.
 */
public class OreScarcityListener implements Listener {

    private final HardMode plugin;
    private final Random random = new Random();

    private static final Map<Material, Material> ORE_REPLACEMENTS = new EnumMap<>(Material.class);

    static {
        ORE_REPLACEMENTS.put(Material.COAL_ORE, Material.STONE);
        ORE_REPLACEMENTS.put(Material.DEEPSLATE_COAL_ORE, Material.DEEPSLATE);
        ORE_REPLACEMENTS.put(Material.IRON_ORE, Material.STONE);
        ORE_REPLACEMENTS.put(Material.DEEPSLATE_IRON_ORE, Material.DEEPSLATE);
        ORE_REPLACEMENTS.put(Material.COPPER_ORE, Material.STONE);
        ORE_REPLACEMENTS.put(Material.DEEPSLATE_COPPER_ORE, Material.DEEPSLATE);
        ORE_REPLACEMENTS.put(Material.GOLD_ORE, Material.STONE);
        ORE_REPLACEMENTS.put(Material.DEEPSLATE_GOLD_ORE, Material.DEEPSLATE);
        ORE_REPLACEMENTS.put(Material.REDSTONE_ORE, Material.STONE);
        ORE_REPLACEMENTS.put(Material.DEEPSLATE_REDSTONE_ORE, Material.DEEPSLATE);
        ORE_REPLACEMENTS.put(Material.LAPIS_ORE, Material.STONE);
        ORE_REPLACEMENTS.put(Material.DEEPSLATE_LAPIS_ORE, Material.DEEPSLATE);
        ORE_REPLACEMENTS.put(Material.DIAMOND_ORE, Material.STONE);
        ORE_REPLACEMENTS.put(Material.DEEPSLATE_DIAMOND_ORE, Material.DEEPSLATE);
        ORE_REPLACEMENTS.put(Material.EMERALD_ORE, Material.STONE);
        ORE_REPLACEMENTS.put(Material.DEEPSLATE_EMERALD_ORE, Material.DEEPSLATE);
        ORE_REPLACEMENTS.put(Material.NETHER_GOLD_ORE, Material.NETHERRACK);
        ORE_REPLACEMENTS.put(Material.NETHER_QUARTZ_ORE, Material.NETHERRACK);
    }

    public OreScarcityListener(HardMode plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        if (!event.isNewChunk()) return;
        if (!plugin.getConfig().getBoolean("ore-scarcity-enabled", true)) return;

        double chance = plugin.getConfig().getDouble("ore-scarcity-chance", 0.4);
        boolean debugLog = plugin.getConfig().getBoolean("ore-scarcity-debug-log", false);

        Chunk chunk = event.getChunk();
        World world = event.getWorld();
        int minY = world.getMinHeight();
        int maxY = world.getMaxHeight(); // сканируем всю колонку, без искусственной обрезки

        int totalOre = 0;
        int removed = 0;

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = minY; y < maxY; y++) {
                    Block block = chunk.getBlock(x, y, z);
                    Material replacement = ORE_REPLACEMENTS.get(block.getType());
                    if (replacement == null) continue;

                    totalOre++;
                    if (random.nextDouble() < chance) {
                        block.setType(replacement, false);
                        removed++;
                    }
                }
            }
        }

        if (debugLog && totalOre > 0) {
            plugin.getLogger().info(String.format(
                    "[OreScarcity] Чанк (%d, %d) в %s: убрано %d из %d блоков руды (%.0f%%)",
                    chunk.getX(), chunk.getZ(), world.getName(), removed, totalOre,
                    100.0 * removed / totalOre));
        }
    }
}

