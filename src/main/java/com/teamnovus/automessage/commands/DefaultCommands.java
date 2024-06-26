package com.teamnovus.automessage.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.teamnovus.automessage.AutoMessage;
import com.teamnovus.automessage.Permission;
import com.teamnovus.automessage.commands.common.BaseCommand;
import com.teamnovus.automessage.commands.common.CommandManager;
import com.teamnovus.automessage.util.Utils;

public class DefaultCommands {

	@BaseCommand(aliases = {"help", "?"}, desc = "View all commands and their info.", usage = "[Page]", permission = Permission.NONE, min = 0, max = 1, hidden = true)
	public void helpCmd(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		// help [Page]
		int maxLines = 6;

		if (args.length != 0 && (!(Utils.isInteger(args[0])) || Math.abs(Integer.valueOf(args[0])) * maxLines - maxLines >= CommandManager.getCommands().size())) {
			sender.sendMessage(ChatColor.RED + "The specified page was not found.");
			return;
		}

		int page = args.length == 0 ? 1 : Math.abs(Integer.valueOf(args[0]));
		int total = 0;
		sender.sendMessage(CommandManager.getExtra() + "__________________.[ " + CommandManager.getHighlight() + AutoMessage.getInstance().getName() + CommandManager.getExtra() + " ].__________________");

		sender.sendMessage(ChatColor.GRAY + "Required: < > Optional: [ ]");
		for (int i = maxLines * page - maxLines; i < CommandManager.getCommands().size() && total < maxLines - 1; i++) {
			BaseCommand command = CommandManager.getCommands().get(i);
			if (!(command.hidden()) && Permission.has(command.permission(), sender)) {
				sender.sendMessage(CommandManager.getExtra() + " - " + CommandManager.getDark() + "/" + commandLabel + " " + command.aliases()[0] + (!(command.usage().equals("")) ? " " + command.usage() : "") + ": " + CommandManager.getLight() + command.desc());
				total++;
			}
		}
		sender.sendMessage(CommandManager.getLight() + "For help type: " + CommandManager.getHighlight() + "/" + commandLabel + " help [Page]");
		sender.sendMessage(CommandManager.getExtra() + "---------------------------------------------------");
	}

}
