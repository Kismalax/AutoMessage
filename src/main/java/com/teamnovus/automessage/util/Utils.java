package com.teamnovus.automessage.util;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.google.gson.JsonParser;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class Utils {
	
	private Utils () { }

	public static boolean isInteger(String s) {
		try {
			Integer.valueOf(s);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	public static String concat(String[] s, int start, int end) {
		String[] args = Arrays.copyOfRange(s, start, end);
		return StringUtils.join(args, " ");
	}
	
	public static String convertToMiniMessage(String message) {
		if (message == null) {
			return null;
		} else if (message.contains("&")) {
			return MiniMessage.miniMessage().serialize(LegacyComponentSerializer.legacyAmpersand().deserialize(message));
		} else if (isJsonMessage(message)) {
			return MiniMessage.miniMessage().serialize(GsonComponentSerializer.gson().deserialize(message));
		} else {
			return message;
		}
	}

	private static boolean isJsonMessage(String message) {
		try {
			JsonParser.parseString(message).getAsJsonObject();
			
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static final long SECONDS = 60;
	public static final long MINUTES = 60;
	public static final long HOURS = 24;

	public static final long ONE_SECOND = 1000;
	public static final long ONE_MINUTE = ONE_SECOND * 60;
	public static final long ONE_HOUR = ONE_MINUTE * 60;
	public static final long ONE_DAY = ONE_HOUR * 24;

	public static String millisToLongDHMS(long duration) {
		final StringBuilder res = new StringBuilder();
		long temp = 0;
		if (duration >= ONE_SECOND) {
			temp = duration / ONE_DAY;
			if (temp > 0) {
				duration -= temp * ONE_DAY;
				res.append(temp).append(" day").append(temp > 1 ? "s" : "").append(duration >= ONE_MINUTE ? ", " : "");
			}

			temp = duration / ONE_HOUR;
			if (temp > 0) {
				duration -= temp * ONE_HOUR;
				res.append(temp).append(" hour").append(temp > 1 ? "s" : "").append(duration >= ONE_MINUTE ? ", " : "");
			}

			temp = duration / ONE_MINUTE;
			if (temp > 0) {
				duration -= temp * ONE_MINUTE;
				res.append(temp).append(" minute").append(temp > 1 ? "s" : "");
			}

			if (!res.toString().equals("") && (duration >= ONE_SECOND)) {
				res.append(" and ");
			}

			temp = duration / ONE_SECOND;
			if (temp > 0) {
				res.append(temp).append(" second").append(temp > 1 ? "s" : "");
			}
			return res.toString();
		} else {
			return "0 second";
		}

	}

	public static Long parseTime(final String timeString) throws NumberFormatException {
		long time;

		int weeks = 0;
		int days = 0;
		int hours = 0;
		int minutes = 0;
		int seconds = 0;

		final Pattern p = Pattern.compile("\\d+[a-z]");
		final Matcher m = p.matcher(timeString);
		boolean result = m.find();

		while (result) {
			final String argument = m.group();

			if (argument.endsWith("w")) {
				weeks = Integer.parseInt(argument.substring(0, argument.length() - 1));
			} else if (argument.endsWith("d")) {
				days = Integer.parseInt(argument.substring(0, argument.length() - 1));
			} else if (argument.endsWith("h")) {
				hours = Integer.parseInt(argument.substring(0, argument.length() - 1));
			} else if (argument.endsWith("m")) {
				minutes = Integer.parseInt(argument.substring(0, argument.length() - 1));
			} else if (argument.endsWith("s")) {
				seconds = Integer.parseInt(argument.substring(0, argument.length() - 1));
			}

			result = m.find();
		}

		time = seconds;
		time += minutes * 60;
		time += hours * 3600;
		time += days * 86400;
		time += weeks * 604800;

		// convert to milliseconds
		time = time * 1000;

		return time;

	}
}
