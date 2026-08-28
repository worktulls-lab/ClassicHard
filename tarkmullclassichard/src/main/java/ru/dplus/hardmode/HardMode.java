package ru.dplus.hardmode;

import org.bukkit.plugin.java.JavaPlugin;
import ru.dplus.hardmode.listeners.BlazeExtraShotTask;
import ru.dplus.hardmode.listeners.BossSpectacleListener;
import ru.dplus.hardmode.listeners.CreeperListener;
import ru.dplus.hardmode.listeners.DragonBreathTask;
import ru.dplus.hardmode.listeners.DragonFightListener;
import ru.dplus.hardmode.listeners.DurabilityListener;
import ru.dplus.hardmode.listeners.EffectAmplifyListener;
import ru.dplus.hardmode.listeners.EndBedListener;
import ru.dplus.hardmode.listeners.EndermanTeleportListener;
import ru.dplus.hardmode.listeners.EnderPearlListener;
import ru.dplus.hardmode.listeners.EquipmentEnchantListener;
import ru.dplus.hardmode.listeners.FallDamageListener;
import ru.dplus.hardmode.listeners.HoglinAggroListener;
import ru.dplus.hardmode.listeners.IndirectDamageListener;
import ru.dplus.hardmode.listeners.MobStatsListener;
import ru.dplus.hardmode.listeners.OreScarcityListener;
import ru.dplus.hardmode.listeners.PhantomEveryNightTask;
import ru.dplus.hardmode.listeners.PiglinBruteBoostListener;
import ru.dplus.hardmode.listeners.RaidListener;
import ru.dplus.hardmode.listeners.RavagerShieldListener;
import ru.dplus.hardmode.listeners.SculkShriekerListener;
import ru.dplus.hardmode.listeners.SkeletonSniperListener;
import ru.dplus.hardmode.listeners.SpawnBoostListener;
import ru.dplus.hardmode.listeners.SurvivalHarshnessListener;
import ru.dplus.hardmode.listeners.WeatherMobListener;
import ru.dplus.hardmode.listeners.WitchListener;

public final class HardMode extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();

        getServer().getPluginManager().registerEvents(new MobStatsListener(this), this);
        getServer().getPluginManager().registerEvents(new CreeperListener(this), this);
        getServer().getPluginManager().registerEvents(new DurabilityListener(this), this);
        getServer().getPluginManager().registerEvents(new IndirectDamageListener(this), this);
        getServer().getPluginManager().registerEvents(new SpawnBoostListener(this), this);
        getServer().getPluginManager().registerEvents(new EndBedListener(this), this);
        getServer().getPluginManager().registerEvents(new BossSpectacleListener(this), this);
        getServer().getPluginManager().registerEvents(new FallDamageListener(this), this);
        getServer().getPluginManager().registerEvents(new SurvivalHarshnessListener(this), this);
        getServer().getPluginManager().registerEvents(new WeatherMobListener(this), this);
        getServer().getPluginManager().registerEvents(new RaidListener(this), this);
        getServer().getPluginManager().registerEvents(new WitchListener(this), this);
        getServer().getPluginManager().registerEvents(new SkeletonSniperListener(this), this);
        getServer().getPluginManager().registerEvents(new EndermanTeleportListener(this), this);
        getServer().getPluginManager().registerEvents(new EffectAmplifyListener(this), this);
        getServer().getPluginManager().registerEvents(new EquipmentEnchantListener(this), this);
        getServer().getPluginManager().registerEvents(new PiglinBruteBoostListener(this), this);
        getServer().getPluginManager().registerEvents(new RavagerShieldListener(this), this);
        getServer().getPluginManager().registerEvents(new HoglinAggroListener(this), this);
        getServer().getPluginManager().registerEvents(new SculkShriekerListener(this), this);
        getServer().getPluginManager().registerEvents(new EnderPearlListener(this), this);
        getServer().getPluginManager().registerEvents(new DragonFightListener(this), this);
        getServer().getPluginManager().registerEvents(new OreScarcityListener(this), this);

        long dragonInterval = getConfig().getLong("dragon-breath-interval-ticks", 80L);
        new DragonBreathTask(this).runTaskTimer(this, dragonInterval, dragonInterval);

        new PhantomEveryNightTask(this).runTaskTimer(this, 1200L, 1200L);

        long blazeInterval = getConfig().getLong("blaze-extra-shot-interval-ticks", 40L);
        new BlazeExtraShotTask(this).runTaskTimer(this, blazeInterval, blazeInterval);

        getLogger().info("TarkMullClassicHard включен. Игра стала сложнее!");
    }

    @Override
    public void onDisable() {
        getLogger().info("TarkMullClassicHard выключен.");
    }
}
