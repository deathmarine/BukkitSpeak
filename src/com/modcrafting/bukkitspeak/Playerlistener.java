package com.modcrafting.bukkitspeak;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerQuitEvent;

import de.stefan1200.jts3serverquery.JTS3ServerQuery;

public class Playerlistener extends PlayerListener{
	BukkitSpeak plugin;
	public Playerlistener(BukkitSpeak bukkitspeak){
		this.plugin = bukkitspeak;
	}
	
	public void onPlayerChat(PlayerChatEvent event){
	        if(event.isCancelled()) return;
	        if(!plugin.query.isConnected()) return;
			YamlConfiguration config = (YamlConfiguration) plugin.getConfig();
			String sname = config.getString("DisplayName", "MC");
	        String msg = event.getMessage();
	        String name = event.getPlayer().getName();
	        String format = "[" + sname + "]<" + name + "> :" + msg;
	        if (plugin.query.sendTextMessage(JTS3ServerQuery.EVENT_MODE_TEXTSERVER, plugin.channel, format)) return;
	}
	public void onPlayerJoin(PlayerJoinEvent event){
			if(!plugin.query.isConnected()) plugin.reconnect();
			YamlConfiguration config = (YamlConfiguration) plugin.getConfig();
			String sname = config.getString("DisplayName", "MC");
			String name = event.getPlayer().getName();
			String format = "[" + sname + "]Player <" + name + "> joined game.";
	        if (plugin.query.sendTextMessage(JTS3ServerQuery.EVENT_MODE_TEXTSERVER, plugin.channel, format)) return;
	}
	public void onPlayerQuit(PlayerQuitEvent event){
			if(!plugin.query.isConnected()) plugin.reconnect();
			YamlConfiguration config = (YamlConfiguration) plugin.getConfig();
			String sname = config.getString("DisplayName", "MC");
			String name = event.getPlayer().getName();
			String format = "[" + sname + "]Player <" + name + "> quit game.";
			if (plugin.query.sendTextMessage(JTS3ServerQuery.EVENT_MODE_TEXTSERVER, plugin.channel, format)) return;
		
	}
	

}
