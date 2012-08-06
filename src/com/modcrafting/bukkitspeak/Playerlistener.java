package com.modcrafting.bukkitspeak;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import de.stefan1200.jts3serverquery.JTS3ServerQuery;

public class Playerlistener implements Listener{
	BukkitSpeak plugin;
	public Playerlistener(BukkitSpeak bukkitspeak){
		this.plugin = bukkitspeak;
	}
	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerChat(AsyncPlayerChatEvent event){
	        if(event.isCancelled()) return;
	        if(!plugin.query.isConnected()) return;
			YamlConfiguration config = (YamlConfiguration) plugin.getConfig();
			String sname = config.getString("DisplayName", "MC");
	        String msg = event.getMessage();
	        String name = event.getPlayer().getName();
	        String format = "[" + sname + "]<" + name + "> :" + msg;
	        //String format = config.getString("MessageFormat", "[%DisplayName%]<%PlayerName%>:%Message%");
	        //format.replaceAll("%DisplayName%", sname);
	        //format.replaceAll("%PlayerName%", name);
	        //format.replaceAll("%Message%", msg);
	        plugin.query.sendTextMessage(JTS3ServerQuery.EVENT_MODE_TEXTSERVER, JTS3ServerQuery.TEXTMESSAGE_TARGET_GLOBAL, convertToTeamspeak(format, true, true));
	}
	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerJoin(PlayerJoinEvent event){
			if(!plugin.query.isConnected()) plugin.reconnect();
			YamlConfiguration config = (YamlConfiguration) plugin.getConfig();
			String sname = config.getString("DisplayName", "MC");
	        String msg = event.getJoinMessage();
	        String name = event.getPlayer().getName();
	        String format = "[" + sname + "]<" + name + "> :" + msg;
	        //String format = config.getString("MessageFormat", "[%DisplayName%]<%PlayerName%>:%Message%");
	        //format.replaceAll("%DisplayName%", sname);
	        //format.replaceAll("%PlayerName%", name);
	        //format.replaceAll("%Message%", msg);
	        plugin.query.sendTextMessage(JTS3ServerQuery.EVENT_MODE_TEXTSERVER, JTS3ServerQuery.TEXTMESSAGE_TARGET_GLOBAL, convertToTeamspeak(format, true, true));
	}
	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerQuit(PlayerQuitEvent event){
			if(!plugin.query.isConnected()) plugin.reconnect();
			YamlConfiguration config = (YamlConfiguration) plugin.getConfig();
			String sname = config.getString("DisplayName", "MC");
	        String msg = event.getQuitMessage();
	        String name = event.getPlayer().getName();
	        String format = "[" + sname + "]<" + name + "> :" + msg;
	        //String format = config.getString("MessageFormat", "[%DisplayName%]<%PlayerName%>:%Message%");
	        //format.replaceAll("%DisplayName%", sname);
	        //format.replaceAll("%PlayerName%", name);
	        //format.replaceAll("%Message%", msg);
	        plugin.query.sendTextMessage(JTS3ServerQuery.EVENT_MODE_TEXTSERVER, JTS3ServerQuery.TEXTMESSAGE_TARGET_GLOBAL, convertToTeamspeak(format, true, true));
	}
	public static String[] COLORS = {
		"",					// 0
		"[color=#0000AA]",	// 1
		"[color=#00AA00]",	// 2
		"[color=#00AAAA]",	// 3
		"[color=#AA0000]",	// 4
		"[color=#AA00AA]",	// 5
		"[color=#EEAA00]",	// 6
		"[color=#999999]",	// 7
		"[color=#555555]",	// 8
		"[color=#4444FF]",	// 9
		"[color=#44DD44]",	// 10
		"[color=#3399FF]",	// 11
		"[color=#FF3333]",	// 12
		"[color=#FF33FF]",	// 13
		"[color=#DDBB00]",	// 14
		"[color=#FFFFFF]",	// 15
		"[b]",				// 16
		"[u]",				// 17
		"[i]",				// 18
		""};				// 19
	public static String convertToTeamspeak(String input, Boolean color, Boolean links) {
		if (input != null) {
			Boolean colored = false, bold = false, underlined = false, italics = false;
			String s = input;
			if (color) {
				s = s.replaceAll("((&|$)([a-fk-orA-FK-OR0-9]))", "§$3");
				while (s.matches(".*§[a-fk-orA-FK-OR0-9].*")) {
					Matcher m = Pattern.compile("(§([a-fk-orA-FK-OR0-9]))").matcher(s);
					if (!m.find()) break;
					Integer i = m.start();
					Integer j;
					if (i + 1 < s.length()) {
						j = getIndex(s.charAt(i + 1));
					} else {
						break;
					}
					if (j <= 15) {
						if (colored) {
							s = s.substring(0, i) + "[/color]" + s.substring(i);
							colored = false;
						}
						s = s.replaceFirst("§([a-fA-F0-9])", COLORS[j]);
						if (j != 0) colored = true;
					} else if (j == 16) {
						if (bold) {
							s = s.substring(0, i) + "[/b]" + s.substring(i);
						}
						s = s.replaceFirst("§[lL]", COLORS[j]);
						bold = true;
					} else if (j == 17) {
						if (underlined) {
							s = s.substring(0, i) + "[/u]" + s.substring(i);
						}
						s = s.replaceFirst("§[nN]", COLORS[j]);
						underlined = true;
					} else if (j == 18) {
						if (italics) {
							s = s.substring(0, i) + "[/i]" + s.substring(i);
						}
						s = s.replaceFirst("§[oO]", COLORS[j]);
						italics = true;
					} else {
						s = s.replaceFirst("§r", "");
						if (colored) s = s.substring(0, i) + "[/color]" + s.substring(i);
						if (bold) s = s.substring(0, i) + "[/b]" + s.substring(i);
						if (italics) s = s.substring(0, i) + "[/i]" + s.substring(i);
						if (underlined) s = s.substring(0, i) + "[/u]" + s.substring(i);
						colored = false;
						bold = false;
						italics = false;
						underlined = false;
					}
				}
				if (colored) s += "[/color]";
				if (bold) s += "[/b]";
				if (italics) s += "[/i]";
				if (underlined) s += "[/u]";
				
			} else {
				s = s.replaceAll("((&|$|§)([a-fk-orA-FK-OR0-9]))", "");
			}
			if (links) {
				s = s.replaceAll("(?i)((http://|ftp://).*\\.?.+\\..+(/.*)?)", "\\[URL]$1\\[/URL]");
			} else {
				s = s.replaceAll("(?i)((http://|ftp://).*\\.?.+\\..+(/.*)?)", "");
			}
			return s;
		}
		return null;
	}

	private static Integer getIndex(char c) {
		String s = String.valueOf(c);
		if (s.matches("[0-9]")) {
			return Integer.valueOf(s);
		} else if (s.matches("[a-fA-F]")) {
			return s.toLowerCase().getBytes()[0] - 87;
		} else if (s.equalsIgnoreCase("l")) {
			return 16;
		} else if (s.equalsIgnoreCase("n")) {
			return 17;
		} else if (s.equalsIgnoreCase("o")) {
			return 18;
		} else {
			return 19;
		}
	}
}
