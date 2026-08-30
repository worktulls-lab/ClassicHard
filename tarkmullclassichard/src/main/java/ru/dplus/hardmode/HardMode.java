package ru.dplus.hardmode;

import org.bukkit.plugin.java.JavaPlugin;
import ru.dplus.hardmode.commands.TarkStatusCommand;
import ru.dplus.hardmode.listeners.AncientCityTrapListener;
import ru.dplus.hardmode.listeners.AnvilCostListener;
import ru.dplus.hardmode.listeners.BeeStingerListener;
import ru.dplus.hardmode.listeners.BlazeExtraShotTask;
import ru.dplus.hardmode.listeners.BlazeFireDurationListener;
import ru.dplus.hardmode.listeners.BossSpectacleListener;
import ru.dplus.hardmode.listeners.BreezeExtraShotTask;
import ru.dplus.hardmode.listeners.BreezeKnockbackListener;
import ru.dplus.hardmode.listeners.ComposterNerfListener;
import ru.dplus.hardmode.listeners.CreeperListener;
import ru.dplus.hardmode.listeners.CriticalHitListener;
import ru.dplus.hardmode.listeners.DragonBreathTask;
import ru.dplus.hardmode.listeners.DragonFightListener;
import ru.dplus.hardmode.listeners.DurabilityListener;
import ru.dplus.hardmode.listeners.EffectAmplifyListener;
import ru.dplus.hardmode.listeners.EndBedListener;
import ru.dplus.hardmode.listeners.EndermanAggroTeleportListener;
import ru.dplus.hardmode.listeners.EndermanTeleportListener;
import ru.dplus.hardmode.listeners.EnderPearlListener;
import ru.dplus.hardmode.listeners.EnvironmentDamageListener;
import ru.dplus.hardmode.listeners.EquipmentEnchantListener;
import ru.dplus.hardmode.listeners.EvokerListener;
import ru.dplus.hardmode.listeners.ExperienceNerfListener;
import ru.dplus.hardmode.listeners.FallDamageListener;
import ru.dplus.hardmode.listeners.FireDurationListener;
import ru.dplus.hardmode.listeners.FishingNerfListener;
import ru.dplus.hardmode.listeners.FurnaceSlowdownListener;
import ru.dplus.hardmode.listeners.HoglinAggroListener;
import ru.dplus.hardmode.listeners.IndirectDamageListener;
import ru.dplus.hardmode.listeners.IronGolemListener;
import ru.dplus.hardmode.listeners.MilkNerfListener;
import ru.dplus.hardmode.listeners.MobStatsListener;
import ru.dplus.hardmode.listeners.PassiveMobListener;
import ru.dplus.hardmode.listeners.PhantomEveryNightTask;
import ru.dplus.hardmode.listeners.PiglinBarterListener;
import ru.dplus.hardmode.listeners.PiglinBruteBoostListener;
import ru.dplus.hardmode.listeners.PillagerPatrolBoostListener;
import ru.dplus.hardmode.listeners.RaidListener;
import ru.dplus.hardmode.listeners.RandomTickSpeedListener;
import ru.dplus.hardmode.listeners.RavagerShieldListener;
import ru.dplus.hardmode.listeners.SculkShriekerListener;
import ru.dplus.hardmode.listeners.SkeletonExtraShotTask;
import ru.dplus.hardmode.listeners.SkeletonSniperListener;
import ru.dplus.hardmode.listeners.SlimeSplitListener;
import ru.dplus.hardmode.listeners.SpiderPoisonListener;
import ru.dplus.hardmode.listeners.StructureLootNerfListener;
import ru.dplus.hardmode.listeners.SurvivalHarshnessListener;
import ru.dplus.hardmode.listeners.WeatherMobListener;
import ru.dplus.hardmode.listeners.WitchListener;
import ru.dplus.hardmode.listeners.ZombieDoorBreakTask;
import ru.dplus.hardmode.listeners.ZombieVillagerCureTask;

/**
 * Точка входа плагина TarkMullClassicHard. onEnable регистрирует все
 * обработчики событий и запускает периодические задачи. Каждый Listener/Task
 * читает свои настройки из config.yml самостоятельно при срабатывании —
 * здесь только сборка и подключение.
 */
public final class HardMode extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();
        registerListeners();
        startTasks();
        registerCommands();
        getLogger().info("TarkMullClassicHard включен. Игра стала сложнее!");
    }

    @Override
    public void onDisable() {
        getLogger().info("TarkMullClassicHard выключен.");
    }

    private void registerListeners() {
        var pm = getServer().getPluginManager();

        pm.registerEvents(new AncientCityTrapListener(this), this);
        pm.registerEvents(new AnvilCostListener(this), this);
        pm.registerEvents(new BeeStingerListener(this), this);
        pm.registerEvents(new BlazeFireDurationListener(this), this);
        pm.registerEvents(new BossSpectacleListener(this), this);
        pm.registerEvents(new BreezeKnockbackListener(this), this);
        pm.registerEvents(new ComposterNerfListener(this), this);
        pm.registerEvents(new CreeperListener(this), this);
        pm.registerEvents(new CriticalHitListener(this), this);
        pm.registerEvents(new DragonFightListener(this), this);
        pm.registerEvents(new DurabilityListener(this), this);
        pm.registerEvents(new EffectAmplifyListener(this), this);
        pm.registerEvents(new EndBedListener(this), this);
        pm.registerEvents(new EndermanAggroTeleportListener(this), this);
        pm.registerEvents(new EndermanTeleportListener(this), this);
        pm.registerEvents(new EnderPearlListener(this), this);
        pm.registerEvents(new EnvironmentDamageListener(this), this);
        pm.registerEvents(new EquipmentEnchantListener(this), this);
        pm.registerEvents(new EvokerListener(this), this);
        pm.registerEvents(new ExperienceNerfListener(this), this);
        pm.registerEvents(new FallDamageListener(this), this);
        pm.registerEvents(new FireDurationListener(this), this);
        pm.registerEvents(new FishingNerfListener(this), this);
        pm.registerEvents(new FurnaceSlowdownListener(this), this);
        pm.registerEvents(new HoglinAggroListener(this), this);
        pm.registerEvents(new IndirectDamageListener(this), this);
        pm.registerEvents(new IronGolemListener(this), this);
        pm.registerEvents(new MilkNerfListener(this), this);
        pm.registerEvents(new MobStatsListener(this), this);
        pm.registerEvents(new PassiveMobListener(this), this);
        pm.registerEvents(new PiglinBarterListener(this), this);
        pm.registerEvents(new PiglinBruteBoostListener(this), this);
        pm.registerEvents(new PillagerPatrolBoostListener(this), this);
        pm.registerEvents(new RaidListener(this), this);
        pm.registerEvents(new RavagerShieldListener(this), this);
        pm.registerEvents(new SculkShriekerListener(this), this);
        pm.registerEvents(new SkeletonSniperListener(this), this);
        pm.registerEvents(new SlimeSplitListener(this), this);
        pm.registerEvents(new SpiderPoisonListener(this), this);
        pm.registerEvents(new StructureLootNerfListener(this), this);
        pm.registerEvents(new SurvivalHarshnessListener(this), this);
        pm.registerEvents(new WeatherMobListener(this), this);
        pm.registerEvents(new WitchListener(this), this);

        RandomTickSpeedListener randomTickSpeedListener = new RandomTickSpeedListener(this);
        pm.registerEvents(randomTickSpeedListener, this);
        randomTickSpeedListener.applyToAllWorlds();
    }

    private void startTasks() {
        long dragonInterval = getConfig().getLong("dragon-breath-interval-ticks", 80L);
        new DragonBreathTask(this).runTaskTimer(this, dragonInterval, dragonInterval);

        new PhantomEveryNightTask(this).runTaskTimer(this, 1200L, 1200L);

        long blazeInterval = getConfig().getLong("blaze-extra-shot-interval-ticks", 40L);
        new BlazeExtraShotTask(this).runTaskTimer(this, blazeInterval, blazeInterval);

        long breezeInterval = getConfig().getLong("breeze-extra-shot-interval-ticks", 40L);
        new BreezeExtraShotTask(this).runTaskTimer(this, breezeInterval, breezeInterval);

        long skeletonInterval = getConfig().getLong("skeleton-extra-shot-interval-ticks", 40L);
        new SkeletonExtraShotTask(this).runTaskTimer(this, skeletonInterval, skeletonInterval);

        long doorCheckPeriod = getConfig().getLong("zombie-door-break-check-period-ticks", 10L);
        new ZombieDoorBreakTask(this).runTaskTimer(this, doorCheckPeriod, doorCheckPeriod);

        new ZombieVillagerCureTask(this).runTaskTimer(this, 20L, 20L);
    }

    private void registerCommands() {
        var command = getCommand("tarkstatus");
        if (command != null) {
            command.setExecutor(new TarkStatusCommand(this));
        }
    }
}
