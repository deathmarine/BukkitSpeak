package com.modcrafting.bukkitspeak;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.bukkit.ChatColor;
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
	/*
	 * Use this when I implement vault.
	 * 
	String use = "bukkitspeak.use";
	String list = "bukkitspeak.list";
	String listclients = "bukkitspeak.clientlist";
	String listchannels = "bukkitspeak.channellist";
	String listserverinfo = "bukkitspeak.serverinfo";
	String ban = "bukkitspeak.ban";
	String kick = "bukkitspeak.kick";
	String poke = "bukkitspeak.poke";
	String ipban = "bukkitspeak.ipban";
	String help = "bukkitspeak.help";
	String reload = "bukkitspeak.reload";
	*/
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
    	boolean auth = false;
		Player player = null;
		
		if (sender instanceof Player){
			player = (Player)sender;
		/*	if (plugin.setupPermissions()){
				if (plugin.permission.has(player, Example)) auth = true;
			}else{ */
				if (player.isOp()){
				 auth = true;
				//}
			 }
		}else{
			auth = true;
		}
		if(!auth){
			sender.sendMessage(ChatColor.RED + "No Permission");
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
						String tsclient = null;
						for (HashMap<String, String> hashMap : dataClientList){
							tsclient = hashMap.remove("client_nickname");
							tsclient = tsclient.replace(" from ", " ip: ");
							tsclient = tsclient.replace(admin, "BukkitSpeak");
							sender.sendMessage(ChatColor.GRAY + "[TS3] " + tsclient);
						}
					}else{
						echoError(player);
					}
					return true;
				}
				if(args[1].equalsIgnoreCase("channels")){
					Vector<HashMap<String, String>> dataChannelList = plugin.query.getList(JTS3ServerQuery.LISTMODE_CHANNELLIST, "-info,-times");
					if (dataChannelList != null){
						String tschannel = null;
						for (HashMap<String, String> hashMap : dataChannelList){
							tschannel = hashMap.remove("channel_name");
							sender.sendMessage(ChatColor.GRAY + "[TS3] " + tschannel);
						}
					}else{
					echoError(player);
					}
					return true;
				}
				if (args[1].equalsIgnoreCase("logview")){
					Vector<HashMap<String, String>> dataLogList = plugin.query.getLogEntries(4, false, false, 0);
					if (dataLogList != null){
						String tslog = null;
						for (HashMap<String, String> hashMap : dataLogList){
							tslog = hashMap.remove("l");
							sender.sendMessage(ChatColor.GRAY + "[TS3] " + tslog);
						}
					}
				}
				if(args[1].equalsIgnoreCase("serverinfo")){
					//Permissions
					if(plugin.query.isConnected()){
					HashMap<String, String> dataServerInfo = plugin.query.getInfo(JTS3ServerQuery.INFOMODE_SERVERINFO, 0);
					String name = dataServerInfo.remove("virtualserver_name");
					String port = dataServerInfo.remove("virtualserver_port");
					sender.sendMessage(ChatColor.GOLD + "[BukkitSpeak] Currently Connected:");
					sender.sendMessage(ChatColor.GOLD + "Name: " + name + " Port:" + port);
					return true;
					}else{
					sender.sendMessage(ChatColor.RED + "[BukkitSpeak] Not Connected");
					sender.sendMessage(ChatColor.GREEN + "Type: /ts reload");
					return true;
					}
					
				}
				if(args[1].equalsIgnoreCase("groups")){
					Vector<HashMap<String, String>> dataClientList = plugin.query.getList(JTS3ServerQuery.LISTMODE_SERVERGROUPLIST, "-info,-times");
					if (dataClientList != null){
						sender.sendMessage(ChatColor.GOLD + "TeamSpeak Groups:");
						String name = null;
						String id = null;
						for (HashMap<String, String> hashMap : dataClientList){
							//outputHashMap(hashMap, player);
							name = hashMap.remove("name");
							id = hashMap.remove("sgid");
							sender.sendMessage(ChatColor.GRAY + "[TS3]" + id + ": " + name);
						}
					}else{
					echoError(player);
					}
					return true;
				}
			}
			if(args[0].equalsIgnoreCase("ban")){
				//Permissions
				if (args.length < 1){
					sender.sendMessage(ChatColor.GRAY + "/ts ban {clientname} (reason)");
					return true;
				}				
				String name = args[1].toLowerCase();
				//Verify
					String banReason = "not sure";
					if(args.length > 3){
						banReason = combineSplit(2, args, " ");						
					}
					String newname = plugin.dquery.clientFind(name);
					if (plugin.dquery.banClient(name, banReason)){
						sender.sendMessage(ChatColor.GRAY + "[BukkitSpeak]" + newname + " was banned from TeamSpeak for " + banReason);
						return true;
					}
				
			}
			if(args[0].equalsIgnoreCase("kick")){
				//Permissions
				if (args.length < 1){
					sender.sendMessage(ChatColor.GRAY + "/ts kick {clientname} (reason)");
					return true;
				}				
				String name = args[1].toLowerCase();
				//Verify
				String kickReason = "not sure";
					if(args.length > 3){
						kickReason = combineSplit(2, args, " ");						
					}
					String newname = plugin.dquery.clientFind(name);
					int clientID = Integer.parseInt(plugin.dquery.clientFindID(name));
					if (plugin.query.kickClient(clientID, false, kickReason)){
						sender.sendMessage(ChatColor.GRAY + "[BukkitSpeak]" + newname + " was kicked from TeamSpeak for " + kickReason);
						return true;
					}
				
			}
			if(args[0].equalsIgnoreCase("poke")){
				//Permissions
				if (args.length < 1){
					sender.sendMessage(ChatColor.GRAY + "/ts poke {clientname} (message)");
					return true;
				}				
				String name = args[1].toLowerCase();
				//Verify
					String message = "Hey Wake Up!";
					if(args.length > 3){
						message = combineSplit(2, args, " ");						
					}
					//int clientID = Integer.parseInt(plugin.dquery.clientFindID(name));
					if (plugin.dquery.poke(name, message)){
						sender.sendMessage(ChatColor.AQUA + name + ChatColor.DARK_AQUA + " was poked in TeamSpeak");
						return true;
					}
				
			}
			if(args[0].equalsIgnoreCase("help")){
				sender.sendMessage(ChatColor.DARK_GRAY + "[BukkitSpeak]");
				sender.sendMessage(ChatColor.GRAY + "/ts list [clients/channels/poke/logview/serverinfo/groups]");
				sender.sendMessage(ChatColor.GRAY + "/ts ban {clientname} (reason)");
				sender.sendMessage(ChatColor.GRAY + "/ts kick {clientname} (reason)");
				sender.sendMessage(ChatColor.GRAY + "/ts poke {clientname} (message)");
				sender.sendMessage(ChatColor.GRAY + "/ts channel {name}");
				sender.sendMessage(ChatColor.GRAY + "-Changes Channels");
				sender.sendMessage(ChatColor.GRAY + "/ts group {groupid} {name}");
				sender.sendMessage(ChatColor.GRAY + "-Changes Group");
				sender.sendMessage(ChatColor.GRAY + "/ts reload");
				//Display Commands permissions
				
			}
			if(args[0].equalsIgnoreCase("group")){
				//Change player group
				if (args.length < 1){
					sender.sendMessage(ChatColor.GRAY + "/ts group {groupid} {name}");
					return true;
				}
				String groupId = args[1];
				String name = combineSplit(2, args, " ");
				
				String clid = plugin.dquery.clientFind(plugin.query.encodeTS3String(name));
				if (plugin.dquery.changeGroup(groupId, clid)){
					sender.sendMessage(ChatColor.GRAY + "Changed Client: " + name + " to Group: " + groupId + "!");
					return true;
				}else{
					sender.sendMessage(ChatColor.GRAY + "Unable to move Client: " + name + " to Group: " + groupId + "!");
					sender.sendMessage(ChatColor.GRAY + "Check the name, spelling and id!");
					return true;
				}
			}
			/*
			if(args[0].equalsIgnoreCase("name")){
				if (args.length < 1){
					sender.sendMessage(ChatColor.GREY + "/ts name {display}");
					return true;
				}
				
			}
			*/
			if(args[0].equalsIgnoreCase("channel")){
				if(args.length < 1){
					sender.sendMessage(ChatColor.GRAY + "/ts channel {name}");
					return true;
				}
				String name = combineSplit(1, args, " ");
				String cid = plugin.dquery.channelFind(plugin.query.encodeTS3String(name));
				plugin.channel = Integer.parseInt(cid);
				return true;
			}
			if(args[0].equalsIgnoreCase("reload")){
				if (args.length < 1){
					sender.sendMessage(ChatColor.GRAY + "/ts reload");
				}
				if(plugin.query.removeAllEvents()){
				}
				plugin.query.removeTeamspeakActionListener();
				plugin.query.closeTS3Connection();
				plugin.getServer().getPluginManager().disablePlugin(plugin);
				plugin.getServer().getPluginManager().enablePlugin(plugin);
				sender.sendMessage(ChatColor.GREEN + "BukkitSpeak Reloaded");
				return true;
			}
		}
		return false;
	}
	void outputHashMap(HashMap<String, String> hm, Player sender){
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
	void echoError(Player player){
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

	public String combineSplit(int startIndex, String[] string, String seperator) {
		StringBuilder builder = new StringBuilder();

		for (int i = startIndex; i < string.length; i++) {
			builder.append(string[i]);
			builder.append(seperator);
		}

		builder.deleteCharAt(builder.length() - seperator.length()); // remove
		return builder.toString();
	}
	
	public static boolean validIP(String ip) {
	    if (ip == null || ip.isEmpty()) return false;
	    ip = ip.trim();
	    if ((ip.length() < 6) & (ip.length() > 15)) return false;
	    try {
	        Pattern pattern = Pattern.compile("^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$");
	        Matcher matcher = pattern.matcher(ip);
	        return matcher.matches();
	    } catch (PatternSyntaxException ex) {
	        return false;
	    }
	}
}
