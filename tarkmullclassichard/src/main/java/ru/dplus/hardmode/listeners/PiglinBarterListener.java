package ru.dplus.hardmode.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PiglinBarterEvent;
import org.bukkit.inventory.ItemStack;
import ru.dplus.hardmode.HardMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Пиглины начинают торговать хуже за золотые слитки. Публичный API отдаёт
 * только уже выбранный результат обмена (список предметов), а не всю таблицу
 * лута с весами редкости — различить "хороший" и "плохой" предмет внутри
 * уже выбранного результата нельзя. Поэтому вместо точечного снижения шанса
 * именно на редкие предметы здесь снижается общий шанс получить что-либо
 * (часть обменов проходит впустую) и урезается количество полученного,
 * когда обмен всё же успешен — в сумме ощущается как "торгуют похуже".
 */
public class PiglinBarterListener implements Listener {

    private final HardMode plugin;
    private final Random random = new Random();

    public PiglinBarterListener(HardMode plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBarter(PiglinBarterEvent event) {
        double failChance = plugin.getConfig().getDouble("piglin-barter-fail-chance", 0.3);
        if (random.nextDouble() < failChance) {
            event.setOutcome(Collections.emptyList());
            return;
        }

        double amountMult = plugin.getConfig().getDouble("piglin-barter-amount-multiplier", 0.6);
        List<ItemStack> reduced = new ArrayList<>();
        for (ItemStack item : event.getOutcome()) {
            ItemStack copy = item.clone();
            int newAmount = Math.max(1, (int) Math.floor(copy.getAmount() * amountMult));
            copy.setAmount(newAmount);
            reduced.add(copy);
        }
        event.setOutcome(reduced);
    }
}
