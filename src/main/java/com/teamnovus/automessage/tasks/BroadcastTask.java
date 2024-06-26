package com.teamnovus.automessage.tasks;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.teamnovus.automessage.AutoMessage;
import com.teamnovus.automessage.models.MessageList;
import com.teamnovus.automessage.models.MessageLists;

public class BroadcastTask implements Runnable {
	private String name;
	private Random random = new Random();

	public BroadcastTask(String name) {
		this.name = name;
	}

	@Override
	public void run() {
		if (MessageLists.getExactList(name) != null && AutoMessage.getInstance().getConfig().getBoolean("settings.enabled")) {
			MessageList list = MessageLists.getExactList(name);

			if (list.isEnabled() && list.hasMessages() && !(list.isExpired()) && (Bukkit.getServer().getOnlinePlayers().size() >= AutoMessage.getInstance().getConfig().getInt("settings.min-players"))) {
				int index = list.isRandom() ? random.nextInt(list.getMessages().size()) : list.getCurrentIndex();

				for (Player p : Bukkit.getServer().getOnlinePlayers()) {
					if (p.hasPermission("automessage.receive." + name)) {
						list.broadcastTo(index, p);
					}
				}

				if (AutoMessage.getInstance().getConfig().getBoolean("settings.log-to-console")) {
					list.broadcastTo(index, Bukkit.getConsoleSender());
				}

				list.setCurrentIndex(index + 1);
			}
		}
	}
}
