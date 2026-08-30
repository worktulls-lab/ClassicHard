package ru.dplus.hardmode.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootTable;
import ru.dplus.hardmode.HardMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Урезает лут из сундуков/бочек в "подземельных" структурах — Trial
 * Chambers, Ancient City, End City, руины бастиона, Nether Fortress (в
 * ваниле её лут-таблица исторически называется "nether_bridge" — старое имя
 * с бета-версий, отсюда оба варианта в списке), Woodland Mansion, пустынные/
 * джунглевые храмы, кораблекрушения, затопленные руины, закопанные
 * сокровища. По тому же принципу, что и бартер пиглинов: часть добычи
 * пропадает целиком, а то, что остаётся — выдаётся в меньшем количестве.
 *
 * Проверка идёт по вхождению подстроки в путь лут-таблицы, поэтому если я
 * ошибся в точном названии какой-то одной таблицы — остальные всё равно
 * продолжат работать, это не ломает сборку и не роняет остальные структуры.
 *
 * Особые блоки-хранилища (Vault) в Trial Chambers сюда НЕ входят — под их
 * механизм награды нет проверенного стабильного публичного хука.
 */
public class StructureLootNerfListener implements Listener {

    private static final List<String> NERFED_STRUCTURE_KEYS = List.of(
            "trial_chambers", "ancient_city", "end_city", "bastion",
            "nether_bridge", "nether_fortress", "woodland_mansion",
            "desert_pyramid", "jungle_temple", "shipwreck", "underwater_ruin",
            "buried_treasure"
    );

    private final HardMode plugin;
    private final Random random = new Random();

    public StructureLootNerfListener(HardMode plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onLootGenerate(LootGenerateEvent event) {
        LootTable table = event.getLootTable();
        if (table == null) return;

        String key = table.getKey().getKey();
        boolean matches = NERFED_STRUCTURE_KEYS.stream().anyMatch(key::contains);
        if (!matches) return;

        double failChance = plugin.getConfig().getDouble("structure-loot-fail-chance", 0.25);
        double amountMult = plugin.getConfig().getDouble("structure-loot-amount-multiplier", 0.6);

        List<ItemStack> reduced = new ArrayList<>();
        for (ItemStack item : event.getLoot()) {
            if (random.nextDouble() < failChance) continue;

            ItemStack copy = item.clone();
            int newAmount = Math.max(1, (int) Math.floor(copy.getAmount() * amountMult));
            copy.setAmount(newAmount);
            reduced.add(copy);
        }
        event.setLoot(reduced);
    }
}
