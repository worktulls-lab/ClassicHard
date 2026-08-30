package ru.dplus.hardmode.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import ru.dplus.hardmode.HardMode;

import java.util.List;
import java.util.Random;

/**
 * Снижает шанс на "сокровище" при рыбалке. Эти четыре предмета в ваниле
 * НИКОГДА не выпадают из таблиц "рыба"/"мусор" — только из "сокровища",
 * так что проверка по типу предмета надёжна на 100%. Если поймано одно из
 * них, есть шанс подменить улов на случайный "мусорный" предмет вместо него.
 */
public class FishingNerfListener implements Listener {

    private static final List<Material> TREASURE_ITEMS = List.of(
            Material.ENCHANTED_BOOK, Material.NAME_TAG, Material.NAUTILUS_SHELL, Material.SADDLE
    );

    private static final List<Material> JUNK_ITEMS = List.of(
            Material.LEATHER_BOOTS, Material.BOWL, Material.STICK, Material.STRING,
            Material.ROTTEN_FLESH, Material.BONE, Material.INK_SAC, Material.TRIPWIRE_HOOK
    );

    private final HardMode plugin;
    private final Random random = new Random();

    public FishingNerfListener(HardMode plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onFish(PlayerFishEvent event) {
        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) return;
        if (!(event.getCaught() instanceof Item caughtItem)) return;

        ItemStack stack = caughtItem.getItemStack();
        if (!TREASURE_ITEMS.contains(stack.getType())) return;

        double downgradeChance = plugin.getConfig().getDouble("fishing-treasure-downgrade-chance", 0.5);
        if (random.nextDouble() >= downgradeChance) return;

        Material junk = JUNK_ITEMS.get(random.nextInt(JUNK_ITEMS.size()));
        caughtItem.setItemStack(new ItemStack(junk, 1));
    }
}
