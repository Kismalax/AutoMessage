package com.teamnovus.automessage.commands.common;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.ChatColor;

public class CommandManager {
	private static ChatColor light = ChatColor.GREEN;
	private static ChatColor dark = ChatColor.DARK_GREEN;
	private static ChatColor neutral = ChatColor.WHITE;
	private static ChatColor highlight = ChatColor.AQUA;
	private static ChatColor extra = ChatColor.DARK_RED;
	private static ChatColor error = ChatColor.RED;
	private static ChatColor warning = ChatColor.YELLOW;

	private static HashMap<BaseCommand, Method> commands = new HashMap<>();
	
	private CommandManager() { }

	public static ChatColor getLight() {
		return light;
	}

	public static ChatColor getDark() {
		return dark;
	}

	public static ChatColor getNeutral() {
		return neutral;
	}

	public static ChatColor getHighlight() {
		return highlight;
	}

	public static ChatColor getExtra() {
		return extra;
	}

	public static ChatColor getError() {
		return error;
	}

	public static ChatColor getWarning() {
		return warning;
	}

	public static void register(Class<?> clazz) {
		Method[] methods = clazz.getMethods();

		for (Method method : methods) {
			if (method.isAnnotationPresent(BaseCommand.class)) {
				commands.put(method.getAnnotation(BaseCommand.class), method);
			}
		}
	}

	public static void unregister(Class<?> clazz) {
		Method[] methods = clazz.getMethods();

		for (Method method : methods) {
			if (method.isAnnotationPresent(BaseCommand.class)) {
				commands.remove(method.getAnnotation(BaseCommand.class));
			}
		}
	}

	public static void unregisterAll() {
		commands.clear();
	}

	public static List<BaseCommand> getCommands() {
		return new ArrayList<>(commands.keySet());
	}

	public static BaseCommand getCommand(String label) {
		for (BaseCommand command : commands.keySet()) {
			for (String alias : command.aliases()) {
				if (label.equalsIgnoreCase(alias)) {
					return command;
				}
			}
		}

		return null;
	}

	public static void execute(BaseCommand command, Object... args) {
		try {
			commands.get(command).invoke(commands.get(command).getDeclaringClass().getConstructor().newInstance(), args);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
