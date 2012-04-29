package com.modcrafting.bukkitspeak;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import de.stefan1200.jts3serverquery.JTS3ServerQuery;

public class Playerlistener implements Listener{
	BukkitSpeak plugin;
	public Playerlistener(BukkitSpeak bukkitspeak){
		this.plugin = bukkitspeak;
	}
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerChat(PlayerChatEvent event){
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
	        if (plugin.query.sendTextMessage(JTS3ServerQuery.EVENT_MODE_TEXTSERVER, plugin.channel, format)) return;
	}
	@EventHandler(priority = EventPriority.MONITOR)
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
	        if (plugin.query.sendTextMessage(JTS3ServerQuery.EVENT_MODE_TEXTSERVER, plugin.channel, format)) return;
	}
	@EventHandler(priority = EventPriority.MONITOR)
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
			if (plugin.query.sendTextMessage(JTS3ServerQuery.EVENT_MODE_TEXTSERVER, plugin.channel, format)) return;
		
	}
	

}
