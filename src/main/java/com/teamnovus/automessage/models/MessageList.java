package com.teamnovus.automessage.models;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import com.teamnovus.automessage.util.Utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class MessageList {
	private boolean enabled = true;
	private int interval = 45;
	private long expiry = -1L;
	private boolean random = false;
	private String prefix = null;
	private String suffix = null;
	private List<Message> messages = new ArrayList<>();

	private int currentIndex = 0;

	public MessageList() {
		messages.add(new Message("First message in the list!"));
		messages.add(new Message("&aSecond message in the list with formatters!"));
		messages.add(new Message("&bThird message in the list with formatters and a \nnew line!"));
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public int getInterval() {
		return interval;
	}

	public void setInterval(int interval) {
		this.interval = interval;
	}

	public long getExpiry() {
		return expiry;
	}

	public void setExpiry(long expiry) {
		this.expiry = expiry;
	}

	public boolean isExpired() {
		return System.currentTimeMillis() >= expiry && expiry != -1;
	}

	public boolean isRandom() {
		return random;
	}

	public void setRandom(boolean random) {
		this.random = random;
	}
	
	public String getPrefix() {
		return prefix;
	}
	
	public void setPrefix(String prefix) {
		this.prefix = Utils.convertToMiniMessage(prefix);
	}
	
	public String getSuffix() {
		return suffix;
	}
	
	public void setSuffix(String suffix) {
		this.suffix = Utils.convertToMiniMessage(suffix);
	}

	public List<Message> getMessages() {
		return messages;
	}

	public void setMessages(List<Message> messages) {
		this.messages = messages;
	}

	public void addMessage(Message message) {
		this.messages.add(message);
	}

	public Message getMessage(Integer index) {
		try {
			return this.messages.get(index.intValue());
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}

	public void addMessage(Integer index, Message message) {
		try {
			this.messages.add(index.intValue(), message);
		} catch (IndexOutOfBoundsException e) {
			this.messages.add(message);
		}
	}

	public boolean editMessage(Integer index, Message message) {
		try {
			return this.messages.set(index.intValue(), message) != null;
		} catch (IndexOutOfBoundsException e) {
			return false;
		}
	}

	public boolean removeMessage(Integer index) {
		try {
			return this.messages.remove(index.intValue()) != null;
		} catch (IndexOutOfBoundsException e) {
			return false;
		}
	}

	public boolean hasMessages() {
		return !messages.isEmpty();
	}

	public void setCurrentIndex(int index) {
		this.currentIndex = index;

		if (currentIndex >= messages.size() || currentIndex < 0) {
			this.currentIndex = 0;
		}
	}

	public int getCurrentIndex() {
		return currentIndex;
	}

	public void broadcast(int index) {
		for (Player player : Bukkit.getOnlinePlayers()) {
			broadcastTo(index, player);
		}

		broadcastTo(index, Bukkit.getConsoleSender());
	}

	public void broadcastTo(int index, CommandSender to) {
		Message message = getMessage(index);

		if (message != null) {
			List<String> messagePieces = message.getMessages();
			List<String> commands = message.getCommands();
			
			for (String m : messagePieces) {
				if (to instanceof Player player) {
					if (m.contains("{NAME}"))
						m = m.replace("{NAME}", player.getName());
					if (m.contains("{DISPLAY_NAME}"))
						m = m.replace("{DISPLAY_NAME}", MiniMessage.miniMessage().serialize(player.displayName()));
					if (m.contains("{WORLD}"))
						m = m.replace("{WORLD}", player.getWorld().getName());
					if (m.contains("{BIOME}"))
						m = m.replace("{BIOME}", player.getLocation().getBlock().getBiome().toString());
				} else if (to instanceof ConsoleCommandSender) {
					if (m.contains("{NAME}"))
						m = m.replace("{NAME}", to.getName());
					if (m.contains("{DISPLAY_NAME}"))
						m = m.replace("{DISPLAY_NAME}", to.getName());
					if (m.contains("{WORLD}"))
						m = m.replace("{WORLD}", "UNKNOWN");
					if (m.contains("{BIOME}"))
						m = m.replace("{BIOME}", "UNKNOWN");
				}

				if (m.contains("{ONLINE}"))
					m = m.replace("{ONLINE}", Bukkit.getServer().getOnlinePlayers().size() + "");
				if (m.contains("{MAX_ONLINE}"))
					m = m.replace("{MAX_ONLINE}", Bukkit.getServer().getMaxPlayers() + "");
				if (m.contains("{UNIQUE_PLAYERS}"))
					m = m.replace("{UNIQUE_PLAYERS}", Bukkit.getServer().getOfflinePlayers().length + "");

				if (m.contains("{YEAR}"))
					m = m.replace("{YEAR}", Calendar.getInstance().get(Calendar.YEAR) + "");
				if (m.contains("{MONTH}"))
					m = m.replace("{MONTH}", Calendar.getInstance().get(Calendar.MONTH) + "");
				if (m.contains("{WEEK_OF_MONTH}"))
					m = m.replace("{WEEK_OF_MONTH}", Calendar.getInstance().get(Calendar.WEEK_OF_MONTH) + "");
				if (m.contains("{WEEK_OF_YEAR}"))
					m = m.replace("{WEEK_OF_YEAR}", Calendar.getInstance().get(Calendar.WEEK_OF_YEAR) + "");
				if (m.contains("{DAY_OF_WEEK}"))
					m = m.replace("{DAY_OF_WEEK}", Calendar.getInstance().get(Calendar.DAY_OF_WEEK) + "");
				if (m.contains("{DAY_OF_MONTH}"))
					m = m.replace("{DAY_OF_MONTH}", Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + "");
				if (m.contains("{DAY_OF_YEAR}"))
					m = m.replace("{DAY_OF_YEAR}", Calendar.getInstance().get(Calendar.DAY_OF_YEAR) + "");
				if (m.contains("{HOUR}"))
					m = m.replace("{HOUR}", Calendar.getInstance().get(Calendar.HOUR) + "");
				if (m.contains("{HOUR_OF_DAY}"))
					m = m.replace("{HOUR_OF_DAY}", Calendar.getInstance().get(Calendar.HOUR_OF_DAY) + "");
				if (m.contains("{MINUTE}"))
					m = m.replace("{MINUTE}", Calendar.getInstance().get(Calendar.MINUTE) + "");
				if (m.contains("{SECOND}"))
					m = m.replace("{SECOND}", Calendar.getInstance().get(Calendar.SECOND) + "");

				Component c = MiniMessage.miniMessage().deserializeOr(prefix, Component.empty())
						.append(MiniMessage.miniMessage().deserialize(m))
						.append(MiniMessage.miniMessage().deserializeOr(suffix, Component.empty()));
				to.sendMessage(c);
			}

			for (String command : commands) {
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replaceFirst("/", ""));
			}
		}
	}
}
