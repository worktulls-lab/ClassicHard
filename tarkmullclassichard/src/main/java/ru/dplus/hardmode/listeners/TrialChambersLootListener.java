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
 * Урезает лут из сундуков/бочек Trial Chambers (испытательных камер) — по
 * тому же принципу, что и бартер пиглинов: часть добычи пропадает целиком,
 * а то, что остаётся, выдаётся в меньшем количестве.
 *
 * Особые блоки-хранилища (Vault), которые выдают награду за Trial Key — сюда
 * НЕ входят. У их механизма награды нет проверенного стабильного публичного
 * хука (насколько я могу судить без доступа к актуальной документации в
 * этой песочнице), и лезть туда наугад — то же самое, что привело к
 * поломке сборки с WitherShootSkullEvent раньше. Если вейлты тоже нужно
 * урезать — скажите, попробую отдельно и явно помечу как менее надёжное.
 */
public class TrialChambersLootListener implements Listener {

    private final HardMode plugin;
    private final Random random = new Random();

    public TrialChambersLootListener(HardMode plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onLootGenerate(LootGenerateEvent event) {
        LootTable table = event.getLootTable();
        if (table == null) return;
        if (!table.getKey().getKey().contains("trial_chambers")) return;

        double failChance = plugin.getConfig().getDouble("trial-chambers-loot-fail-chance", 0.25);
        double amountMult = plugin.getConfig().getDouble("trial-chambers-loot-amount-multiplier", 0.6);

        List<ItemStack> reduced = new ArrayList<>();
        for (ItemStack item : event.getLoot()) {
            if (random.nextDouble() < failChance) continue; // предмет вообще пропадает из добычи

            ItemStack copy = item.clone();
            int newAmount = Math.max(1, (int) Math.floor(copy.getAmount() * amountMult));
            copy.setAmount(newAmount);
            reduced.add(copy);
        }
        event.setLoot(reduced);
    }
}
