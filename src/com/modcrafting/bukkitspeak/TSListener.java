package com.modcrafting.bukkitspeak;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import de.stefan1200.jts3serverquery.JTS3ServerQuery;
import de.stefan1200.jts3serverquery.TeamspeakActionListener;

public class TSListener implements TeamspeakActionListener{
	BukkitSpeak plugin;
	JTS3ServerQuery query;
	
	public TSListener(BukkitSpeak bukkitSpeak) {
		this.plugin = bukkitSpeak;
	}

	@Override
	public void teamspeakActionPerformed(String eventType, HashMap<String, String> eventInfo) {

		if(eventType.equalsIgnoreCase("notifycliententerview")||eventType.equalsIgnoreCase("notifyclientleftview")){
			String msg = null;
			//Setup for custom format.


			String name = eventInfo.remove("client_nickname");
			if(eventType.equalsIgnoreCase("notifycliententerview")){
	      	 	msg = " Joined Teamspeak.";
			}	
			/*
			if(eventType.equalsIgnoreCase("notifyclientleftview")){
	      	 	msg = " Left Teamspeak.";
				name = plugin.dquery.clientFindName(eventInfo.remove("clid").trim());
			}
			*/
			if(!name.contains("Unknown from"))
			plugin.getServer().broadcastMessage(ChatColor.BLUE + "[TS3]" + ChatColor.GRAY + "[" + name + "]: " + msg);
		}
			String msg = eventInfo.remove("msg");
			YamlConfiguration config = (YamlConfiguration) plugin.getConfig();
			String sname = config.getString("DisplayName", "MC");
			if (msg.contains("[" + sname + "]")){
				//Double chat disable
				return;
			}
			//Add Team Speak Commands ingame commands.
			if(msg.contains("!playerlist")){
				Player[] pl = plugin.getServer().getOnlinePlayers();
				for (int i=0; i<pl.length; i++){
					String name = pl[i].getName();
					String format = "[" + sname + "] " + name;
			        if (plugin.query.sendTextMessage(JTS3ServerQuery.EVENT_MODE_TEXTSERVER, JTS3ServerQuery.TEXTMESSAGE_TARGET_GLOBAL, format)){
			        	
			        }
				}
				return;
			}
			if(msg.contains("!reload")){
				if(plugin.query.removeAllEvents()){
				}
				plugin.query.removeTeamspeakActionListener();
				plugin.query.closeTS3Connection();
				plugin.getServer().getPluginManager().disablePlugin(plugin);
				plugin.getServer().getPluginManager().enablePlugin(plugin);
				if (plugin.query.sendTextMessage(JTS3ServerQuery.EVENT_MODE_TEXTSERVER, JTS3ServerQuery.TEXTMESSAGE_TARGET_GLOBAL, "BukkitSpeak Reloaded")){
		        	
		        }
				return;
			}
	}
	void outputHashMap(HashMap<String, String> hm)
	{
		if (hm == null)
		{
			return;
		}
		
	    Collection<String> cValue = hm.values();
	    Collection<String> cKey = hm.keySet();
	    Iterator<String> itrValue = cValue.iterator();
	    Iterator<String> itrKey = cKey.iterator();
		
		while (itrValue.hasNext() && itrKey.hasNext())
		{
			System.out.println(itrKey.next() + ": " + itrValue.next());
				
		}
	}
}
