package ru.dplus.hardmode.listeners;

import org.bukkit.entity.CaveSpider;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Spider;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.dplus.hardmode.HardMode;

import java.util.Random;

/**
 * В ваниле яд при укусе есть только у пещерного паука. Здесь такой же шанс
 * появляется и у обычного паука (пещерного не трогаем — у него это и так есть).
 */
public class SpiderPoisonListener implements Listener {

    private final HardMode plugin;
    private final Random random = new Random();

    public SpiderPoisonListener(HardMode plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onSpiderHit(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Spider spider)) return;
        if (spider instanceof CaveSpider) return;
        if (!(event.getEntity() instanceof LivingEntity target)) return;

        double chance = plugin.getConfig().getDouble("spider-poison-chance", 0.5);
        if (random.nextDouble() >= chance) return;

        int duration = plugin.getConfig().getInt("spider-poison-duration-ticks", 100);
        int amplifier = plugin.getConfig().getInt("spider-poison-amplifier", 0);
        target.addPotionEffect(new PotionEffect(PotionEffectType.POISON, duration, amplifier));
    }
}
