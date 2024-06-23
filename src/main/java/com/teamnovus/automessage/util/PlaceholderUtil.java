package com.teamnovus.automessage.util;

import java.util.Calendar;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import com.teamnovus.automessage.AutoMessage;

import net.kyori.adventure.text.minimessage.MiniMessage;

public class PlaceholderUtil {
	private static final String[] consoleSearch = new String[] {"{NAME}", "{DISPLAY_NAME}", "{WORLD}", "{BIOME}"};
	private static final String[] consoleReplacements = new String[] {"CONSOLE", "CONSOLE", "UNKNOWN", "UNKNOWN"};
	
	private PlaceholderUtil() {
	}

	public static String fill(String message, CommandSender sender) {
		MiniMessage miniMsg = AutoMessage.getInstance().getMiniMessageProvider().getMiniMessage(sender);

		if (sender instanceof Player player) {
			if (message.contains("{NAME}"))
				message = message.replace("{NAME}", player.getName());
			if (message.contains("{DISPLAY_NAME}"))
				message = message.replace("{DISPLAY_NAME}", miniMsg.serialize(player.displayName()));
			if (message.contains("{WORLD}"))
				message = message.replace("{WORLD}", player.getWorld().getName());
			if (message.contains("{BIOME}"))
				message = message.replace("{BIOME}", player.getLocation().getBlock().getBiome().toString());
		} else if (sender instanceof ConsoleCommandSender) {
			message = StringUtils.replaceEach(message, consoleSearch, consoleReplacements);
		}

		if (message.contains("{ONLINE}"))
			message = message.replace("{ONLINE}", Integer.toString(Bukkit.getServer().getOnlinePlayers().size()));
		if (message.contains("{MAX_ONLINE}"))
			message = message.replace("{MAX_ONLINE}", Integer.toString(Bukkit.getServer().getMaxPlayers()));
		if (message.contains("{UNIQUE_PLAYERS}"))
			message = message.replace("{UNIQUE_PLAYERS}", Integer.toString(Bukkit.getServer().getOfflinePlayers().length));

		if (message.contains("{YEAR}"))
			message = message.replace("{YEAR}",
					Integer.toString(Calendar.getInstance().get(Calendar.YEAR)));
		if (message.contains("{MONTH}"))
			message = message.replace("{MONTH}",
					Integer.toString(Calendar.getInstance().get(Calendar.MONTH)));
		if (message.contains("{WEEK_OF_MONTH}"))
			message = message.replace("{WEEK_OF_MONTH}",
					Integer.toString(Calendar.getInstance().get(Calendar.WEEK_OF_MONTH)));
		if (message.contains("{WEEK_OF_YEAR}"))
			message = message.replace("{WEEK_OF_YEAR}",
					Integer.toString(Calendar.getInstance().get(Calendar.WEEK_OF_YEAR)));
		if (message.contains("{DAY_OF_WEEK}"))
			message = message.replace("{DAY_OF_WEEK}",
					Integer.toString(Calendar.getInstance().get(Calendar.DAY_OF_WEEK)));
		if (message.contains("{DAY_OF_MONTH}"))
			message = message.replace("{DAY_OF_MONTH}",
					Integer.toString(Calendar.getInstance().get(Calendar.DAY_OF_MONTH)));
		if (message.contains("{DAY_OF_YEAR}"))
			message = message.replace("{DAY_OF_YEAR}",
					Integer.toString(Calendar.getInstance().get(Calendar.DAY_OF_YEAR)));
		if (message.contains("{HOUR}"))
			message = message.replace("{HOUR}",
					Integer.toString(Calendar.getInstance().get(Calendar.HOUR)));
		if (message.contains("{HOUR_OF_DAY}"))
			message = message.replace("{HOUR_OF_DAY}",
					Integer.toString(Calendar.getInstance().get(Calendar.HOUR_OF_DAY)));
		if (message.contains("{MINUTE}"))
			message = message.replace("{MINUTE}",
					Integer.toString(Calendar.getInstance().get(Calendar.MINUTE)));
		if (message.contains("{SECOND}"))
			message = message.replace("{SECOND}",
					Integer.toString(Calendar.getInstance().get(Calendar.SECOND)));

		return message;
	}
}
