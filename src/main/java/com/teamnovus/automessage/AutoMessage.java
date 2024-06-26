package com.teamnovus.automessage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import com.teamnovus.automessage.commands.DefaultCommands;
import com.teamnovus.automessage.commands.PluginCommands;
import com.teamnovus.automessage.commands.common.BaseCommandExecutor;
import com.teamnovus.automessage.commands.common.CommandManager;
import com.teamnovus.automessage.models.Message;
import com.teamnovus.automessage.models.MessageList;
import com.teamnovus.automessage.models.MessageLists;
import com.teamnovus.automessage.util.MiniMessageProvider;

public class AutoMessage extends JavaPlugin {
	private static AutoMessage instance;
	private boolean isPapiAvailable = false;
	private MiniMessageProvider miniMessageProvider = null;

	@Override
	public void onEnable() {
		if (!isPaper()) {
			getLogger().severe("This plugin requies Paper or compatibile server.");
			getLogger().severe("Get it from https://papermc.io/");
			
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}
		
		isPapiAvailable = (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null);
		
		instance = this;

		// Setup the base command.
		getCommand("automessage").setExecutor(new BaseCommandExecutor());

		// Register additional commands.
		CommandManager.register(DefaultCommands.class);
		CommandManager.register(PluginCommands.class);

		// Load the configuration.
		if (loadConfig()) {
			miniMessageProvider = new MiniMessageProvider();
			getServer().getPluginManager().registerEvents(miniMessageProvider, this);
			
			// Start metrics.
		}
	}

	@Override
	public void onDisable() {
		HandlerList.unregisterAll(this);
		MessageLists.unschedule();

		miniMessageProvider = null;
		instance = null;
	}

	public boolean loadConfig() {
		if (!(new File(getDataFolder() + File.separator + "config.yml").exists())) {
			saveDefaultConfig();
		}


		try {
			new YamlConfiguration().load(new File(getDataFolder() + File.separator + "config.yml"));
		} catch (Exception e) {
			getLogger().severe("There was an error loading your configuration.");
			getLogger().severe("A detailed description of your error is shown below.");
			e.printStackTrace();
			Bukkit.getPluginManager().disablePlugin(this);

			return false;
		}

		reloadConfig();

		MessageLists.clear();

		for (String key : getConfig().getConfigurationSection("message-lists").getKeys(false)) {
			MessageList list = new MessageList();

			if (getConfig().contains("message-lists." + key + ".enabled"))
				list.setEnabled(getConfig().getBoolean("message-lists." + key + ".enabled"));

			if (getConfig().contains("message-lists." + key + ".interval"))
				list.setInterval(getConfig().getInt("message-lists." + key + ".interval"));

			if (getConfig().contains("message-lists." + key + ".expiry"))
				list.setExpiry(getConfig().getLong("message-lists." + key + ".expiry"));

			if (getConfig().contains("message-lists." + key + ".random"))
				list.setRandom(getConfig().getBoolean("message-lists." + key + ".random"));
			
			if (getConfig().contains("message-lists." + key + ".prefix"))
				list.setPrefix(getConfig().getString("message-lists." + key + ".prefix"));
			
			if (getConfig().contains("message-lists." + key + ".suffix"))
				list.setSuffix(getConfig().getString("message-lists." + key + ".suffix"));

			List<Message> finalMessages = new ArrayList<>();

			if (getConfig().contains("message-lists." + key + ".messages")) {
				List<?> messages = getConfig().getList("message-lists." + key + ".messages");

				for (Object entry : messages) {
					if (entry instanceof String message) {
						finalMessages.add(new Message(message));
					} else if (entry instanceof Map<?,?> message) {
						Set<?> keys = message.keySet();
						for (Object keyEntry : keys) {
							if (keyEntry instanceof String keyString) {
								finalMessages.add(new Message(keyString));
							}
						}
					}
				}
			}

			list.setMessages(finalMessages);

			MessageLists.setList(key, list);
		}

		MessageLists.schedule();

		// Saves any version changes to the disk
		saveConfiguration();

		return true;
	}

	public void saveConfiguration() {
		if (!(new File(getDataFolder() + File.separator + "config.yml").exists())) {
			saveDefaultConfig();
		}

		for (String key : getConfig().getConfigurationSection("message-lists").getKeys(false)) {
			getConfig().set("message-lists." + key, null);
		}

		for (String key : MessageLists.getMessageLists().keySet()) {
			MessageList list = MessageLists.getExactList(key);
			getConfig().set("message-lists." + key + ".enabled", list.isEnabled());
			getConfig().set("message-lists." + key + ".interval", list.getInterval());
			getConfig().set("message-lists." + key + ".expiry", list.getExpiry());
			getConfig().set("message-lists." + key + ".random", list.isRandom());
			getConfig().set("message-lists." + key + ".prefix", list.getPrefix());
			getConfig().set("message-lists." + key + ".suffix", list.getSuffix());
			
			List<String> messages = list.getMessages().stream().map(Message::getMessage).toList();

			getConfig().set("message-lists." + key + ".messages", messages);
		}

		saveConfig();
	}
	
	public boolean isPapiAvailable() {
		return isPapiAvailable;
	}
	
	public MiniMessageProvider getMiniMessageProvider() {
		return miniMessageProvider;
	}
	
    private static boolean hasClass(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
	
	private static boolean isPaper() {
		return hasClass("com.destroystokyo.paper.PaperConfig") || hasClass("io.papermc.paper.configuration.Configuration");
	}
	
	public static AutoMessage getInstance() {
		if (instance == null) throw new IllegalStateException("The plugin is not in initalized state");
		return instance;
	}
}
