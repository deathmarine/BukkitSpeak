package com.modcrafting.bukkitspeak;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import de.stefan1200.jts3serverquery.JTS3ServerQuery;

public class Commands implements CommandExecutor {
	BukkitSpeak plugin;
	public Commands(BukkitSpeak bukkitSpeak) {
		this.plugin = bukkitSpeak;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
    	boolean auth = true;
		Player player = null;
		
		if (sender instanceof Player){
			player = (Player)sender;
		/*	if (plugin.setupPermissions()){
				if (plugin.permission.has(player, "identify.use")) auth = true;
			}else{ */
				if (player.isOp()){
				 auth = true;
				//}
			 }
		}else{
			auth = true;
		}
		if(!auth){
			sender.sendMessage("No Permission");
		}
		if(auth){
			if (args.length < 1) return false;
			if(args[0].equalsIgnoreCase("List")){
				if(args.length < 2){
					//Disable help message
					return true;
				}
				if(args[1].equalsIgnoreCase("clients")){
					YamlConfiguration config = (YamlConfiguration) plugin.getConfig();
					String admin = config.getString("ServerAdminUsername","serveradmin");
					Vector<HashMap<String, String>> dataClientList = plugin.query.getList(JTS3ServerQuery.LISTMODE_CLIENTLIST, "-info,-times");
					if (dataClientList != null){
						for (HashMap<String, String> hashMap : dataClientList){
							String tsclient = hashMap.remove("client_nickname");
							tsclient = tsclient.replace(" from ", " ip: ");
							tsclient = tsclient.replace(admin, "BukkitSpeak");
							sender.sendMessage("[TS3] " + tsclient);
						}
					}else{
						echoError(player);
					}
					return true;
				}
				if(args[1].equalsIgnoreCase("channels")){
					Vector<HashMap<String, String>> dataClientList = plugin.query.getList(JTS3ServerQuery.LISTMODE_CHANNELLIST, "-info,-times");
					if (dataClientList != null){
						StringBuffer sb = new StringBuffer();
						for (HashMap<String, String> hashMap : dataClientList){
							outputHashMap(hashMap, player);
							sb.append(hashMap.get("channel_name"));
							return true;
						}
					}else{
					echoError(player);
					}
				return true;
				}
				if (args[1].equalsIgnoreCase("logview")){
					Vector<HashMap<String, String>> dataLogList = plugin.query.getLogEntries(4, false, false, 0);
					if (dataLogList != null)
					{
						StringBuffer sb = new StringBuffer();
						for (HashMap<String, String> hashMap : dataLogList)
						{
							outputHashMap(hashMap, player);
							if (sb.length() > 0)
							{
								sb.append("\n");
							}
							sb.append(hashMap.get("l"));
						}
					}else{
						echoError(player);
					}
				}
				if(args[1].equalsIgnoreCase("serverinfo")){
					HashMap<String, String> dataServerInfo = plugin.query.getInfo(JTS3ServerQuery.INFOMODE_SERVERINFO, 0);
					outputHashMap(dataServerInfo, player);
					return true;
				}
			}
		}
		return false;
	}
	void outputHashMap(HashMap<String, String> hm, Player sender)
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
	void echoError(Player player)
	{
		String error = plugin.query.getLastError();
		if (error != null)
		{
			System.out.println(error);
			if (plugin.query.getLastErrorPermissionID() != -1)
			{
				HashMap<String, String> permInfo = plugin.query.getPermissionInfo(plugin.query.getLastErrorPermissionID());
				if (permInfo != null)
				{
					System.out.println("Missing Permission: " + permInfo.get("permname"));
				}
			}
		}
	}
}
