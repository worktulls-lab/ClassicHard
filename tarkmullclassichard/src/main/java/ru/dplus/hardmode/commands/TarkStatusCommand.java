package ru.dplus.hardmode.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import ru.dplus.hardmode.HardMode;

/**
 * Публичная команда без побочных эффектов — ничего не меняет и не спавнит,
 * просто подтверждает, что плагин загружен и работает. Доступна всем на
 * сервере, никакого права не требует.
 */
public class TarkStatusCommand implements CommandExecutor {

    private final HardMode plugin;

    public TarkStatusCommand(HardMode plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String version = plugin.getDescription().getVersion();
        sender.sendMessage(ChatColor.GREEN + "[TarkMullClassicHard] " + ChatColor.WHITE
                + "плагин версии " + version + " загружен и работает.");
        return true;
    }
}
