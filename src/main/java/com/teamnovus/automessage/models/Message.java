package com.teamnovus.automessage.models;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonParser;
import com.teamnovus.automessage.AutoMessage;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class Message {
	private static final String SPLIT_REGEX = "(?<!\\\\)\\\\n";

	private String raw;
	private ArrayList<String> messageParts = new ArrayList<>();
	private ArrayList<String> commandParts = new ArrayList<>();

	public Message(String raw) {
		this.raw = raw;
		
		for (String part : raw.split(SPLIT_REGEX)) {
			if (part.startsWith("/")) {
				commandParts.add(part);
			} else {
				messageParts.add(convertToMiniMessage(part));
			}
		}
		
		if (AutoMessage.getInstance().getConfig().getBoolean("settings.convert-to-minimessage")) {
			if (!messageParts.isEmpty() && !commandParts.isEmpty()) {
				this.raw = String.join("\\n", String.join("\\n", messageParts), String.join("\\n", commandParts));
			} else if (!messageParts.isEmpty()) {
				this.raw = String.join("\\n", messageParts);
			} else if (!commandParts.isEmpty()) {
				this.raw = String.join("\\n", commandParts);
			}
		}
	}

	public String getMessage() {
		return raw;
	}

	public List<String> getMessages() {
		return messageParts;
	}

	public List<String> getCommands() {
		return commandParts;
	}
	
	private String convertToMiniMessage(String message) {
		if (message.contains("&")) {
			return MiniMessage.miniMessage().serialize(LegacyComponentSerializer.legacyAmpersand().deserialize(message));
		} else if (isJsonMessage(message)) {
			return MiniMessage.miniMessage().serialize(GsonComponentSerializer.gson().deserialize(message));
		} else {
			return message;
		}
	}

	private boolean isJsonMessage(String message) {
		try {
			JsonParser.parseString(message).getAsJsonObject();
			
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
