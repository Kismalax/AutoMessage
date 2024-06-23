package com.teamnovus.automessage.util;

import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import com.teamnovus.automessage.AutoMessage;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class MiniMessageProvider implements Listener {
	private final ConcurrentHashMap<Player, MiniMessage> miniMessageMap;
	private final MiniMessage miniMessage;
	
	public MiniMessageProvider() {
		if (AutoMessage.getInstance().isPapiAvailable()) {
			miniMessageMap = new ConcurrentHashMap<>();
			miniMessage = buildMiniMessage(null);
			
			for (Player player : Bukkit.getOnlinePlayers()) {
				miniMessageMap.put(player, getMiniMessage(player));
			}
		} else {
			miniMessageMap = null;
			miniMessage = MiniMessage.miniMessage();
		}
	}
	
	public MiniMessage getMiniMessage(final CommandSender sender) {
		if (sender == null || !(sender instanceof Player player) || miniMessageMap == null) {
			return miniMessage;
		} else {
			return miniMessageMap.computeIfAbsent(player, this::buildMiniMessage);
		}
	}
	
	private MiniMessage buildMiniMessage(final Player player) {
		return MiniMessage.builder().tags(TagResolver.resolver(StandardTags.defaults(), papiTag(player))).build();
	}
	
	/**
	* Creates a tag resolver capable of resolving PlaceholderAPI tags for a given player.
	*
	* @param player the player
	* @return the tag resolver
	*/
	private TagResolver papiTag(final Player player) {
	    return TagResolver.resolver("papi", (argumentQueue, context) -> {
	        // Get the string placeholder that they want to use.
	        final String papiPlaceholder = argumentQueue.popOr("papi tag requires an argument").value();

	        // Then get PAPI to parse the placeholder for the given player.
	        final String parsedPlaceholder = PlaceholderAPI.setPlaceholders(player, '%' + papiPlaceholder + '%');

	        // We need to turn this ugly legacy string into a nice component.
	        final Component componentPlaceholder = LegacyComponentSerializer.legacySection().deserialize(parsedPlaceholder);

	        // Finally, return the tag instance to insert the placeholder!
	        return Tag.selfClosingInserting(componentPlaceholder);
	    });
	}
	
	@EventHandler
	public void onPlayerQuit(final PlayerQuitEvent event) {
		miniMessageMap.remove(event.getPlayer());
	}
}
