package ru.dplus.hardmode.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import ru.dplus.hardmode.HardMode;

/**
 * /tark ore on|off — включает/выключает OreScarcityListener на лету, без
 * перезапуска сервера, и сохраняет значение в config.yml, чтобы оно
 * сохранилось после рестарта.
 */
public class TarkCommand implements CommandExecutor {

    private final HardMode plugin;

    public TarkCommand(HardMode plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 2 || !args[0].equalsIgnoreCase("ore")
                || !(args[1].equalsIgnoreCase("on") || args[1].equalsIgnoreCase("off"))) {
            sender.sendMessage(ChatColor.YELLOW + "Использование: /tark ore <on|off>");
            return true;
        }

        boolean enable = args[1].equalsIgnoreCase("on");
        plugin.getConfig().set("ore-scarcity-enabled", enable);
        plugin.saveConfig();

        String status = enable
                ? ChatColor.GREEN + "включён"
                : ChatColor.RED + "выключен";
        sender.sendMessage(ChatColor.GRAY + "Дефицит руды: " + status + ChatColor.GRAY + ".");
        return true;
    }
}
