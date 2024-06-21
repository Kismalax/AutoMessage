package com.teamnovus.automessage.models;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;

import com.teamnovus.automessage.AutoMessage;
import com.teamnovus.automessage.tasks.BroadcastTask;

public class MessageLists {
	private static Map<String, MessageList> lists = new HashMap<>();
	
	private MessageLists() { }

	public static Map<String, MessageList> getMessageLists() {
		return lists;
	}

	public static void setMessageLists(Map<String, MessageList> messageLists) {
		lists = messageLists;
	}

	public static MessageList getExactList(String name) {
		return lists.get(name);
	}

	public static MessageList getBestList(String name) {
		for (Entry<String, MessageList> entry : lists.entrySet()) {
			if (entry.getKey().startsWith(name)) {
				return entry.getValue();
			}
		}
		
		return null;
	}

	public static String getBestKey(String name) {
		for (String key : lists.keySet()) {
			if (key.startsWith(name)) {
				return key;
			}
		}

		return null;
	}

	public static void setList(String key, MessageList value) {
		if (value == null) {
			lists.remove(key);
		} else {
			lists.put(key, value);
		}

		schedule();
	}

	public static void clear() {
		lists.clear();
	}

	public static void schedule() {
		unschedule();

		for (Entry<String, MessageList> entry : lists.entrySet()) {
			MessageList list = lists.get(entry.getKey());

			Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(AutoMessage.plugin, new BroadcastTask(entry.getKey()), 20L * list.getInterval(), 20L * list.getInterval());
		}
	}

	public static void unschedule() {
		Bukkit.getScheduler().cancelTasks(AutoMessage.plugin);
	}
}
