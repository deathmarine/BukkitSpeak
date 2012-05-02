package com.modcrafting.bukkitspeak;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import de.stefan1200.jts3serverquery.JTS3ServerQuery;

public class BukkitSpeak extends JavaPlugin{

	private final Playerlistener playerListener = new Playerlistener(this);
	private final TSListener tslistener = new TSListener(this);
	public HashSet<String> loggedPlayers = new HashSet<String>();
	public JTS3ServerQuery query = new JTS3ServerQuery();
	public DTS3ServerQuery dquery = new DTS3ServerQuery(this);
	public KeepAlive keepalive = new KeepAlive(this);
	public final static Logger log = Logger.getLogger("Minecraft");
	public String maindir = "plugins/BukkitSpeak/";
	public int channel;
	protected void createDefaultConfiguration(String name) {
		File actual = new File(getDataFolder(), name);
		if (!actual.exists()) {
	
			InputStream input =
				this.getClass().getResourceAsStream("/" + name);
			if (input != null) {
				FileOutputStream output = null;
	
				try {
					output = new FileOutputStream(actual);
					byte[] buf = new byte[8192];
					int length = 0;
					while ((length = input.read(buf)) > 0) {
						output.write(buf, 0, length);
					}
	
					System.out.println(getDescription().getName()
							+ ": Default configuration file written: " + name);
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						if (input != null)
							input.close();
					} catch (IOException e) {}
	
					try {
						if (output != null)
							output.close();
					} catch (IOException e) {}
				}
			}
		}
	}

	public void onDisable() {
		loggedPlayers.clear();
		query.removeTeamspeakActionListener();
		query.closeTS3Connection();
	}
	
	public void onEnable() {
		YamlConfiguration config = (YamlConfiguration) this.getConfig();
		PluginDescriptionFile pdfFile = this.getDescription();
		//Write the default configuration stored
		new File(maindir).mkdir();
		createDefaultConfiguration("config.yml");
		
		String channelName = config.getString("channel", "default");
		if(channelName.equalsIgnoreCase("default")){
			channel = JTS3ServerQuery.TEXTMESSAGE_TARGET_GLOBAL;
		}else{
			channel = Integer.parseInt(dquery.channelFind(channelName));
		}
		loadCommands();
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(playerListener, this);
		String host = config.getString("Host","localhost");
		int vport = config.getInt("ServerPort", 9987);
		int port = config.getInt("QueryPort",10011);
		//String chID = config.getString("Channel", "default");
		if (!query.connectTS3Query(host , port)){
			log.log(Level.INFO, "[" + pdfFile.getName() + "]" + " is unable to be enabled!");
			echoError();
			return;
		}
		
		String admin = config.getString("ServerAdminUsername","serveradmin");
		String pass = config.getString("ServerAdminPassword","password");
		query.loginTS3(admin, pass);
		
		log.log(Level.INFO, "[" + pdfFile.getName() + "]" + " Logged into Team Speak 3 Server!");
		//Register JTS3ServerQuery Listeners
		query.setTeamspeakActionListener(tslistener);
		if (!query.selectVirtualServer(vport, true))
		{
			echoError();
			return;
		}
		if (!query.addEventNotify(JTS3ServerQuery.EVENT_MODE_TEXTSERVER, 0)){
			echoError();
			return;
		}
		if (!query.addEventNotify(JTS3ServerQuery.EVENT_MODE_TEXTCHANNEL, 0)){
			echoError();
			return;
		}
		
		log.log(Level.INFO, "[" + pdfFile.getName() + "]" + " is enabled!");
		String displayName = config.getString("DisplayName", "MC");
		if (query.setDisplayName(displayName)){
			log.log(Level.INFO, "[" + pdfFile.getName() + "]" + " Nickname changed to " + displayName + "!");
		}
		//Enable the Keep Alive
		getServer().getScheduler().scheduleAsyncRepeatingTask(this, this.keepalive, 300, 300);
	}
	void echoError(){
		String error = query.getLastError();
		if (error != null)
		{
			System.out.println(error);
			if (query.getLastErrorPermissionID() != -1)
			{
				HashMap<String, String> permInfo = query.getPermissionInfo(query.getLastErrorPermissionID());
				if (permInfo != null)
				{
					System.out.println("Missing Permission: " + permInfo.get("permname"));
				}
			}
		}
	}
	private void loadCommands() {
		getCommand("ts").setExecutor(new Commands(this));
		
	}
	public void reconnect(){
		YamlConfiguration config = (YamlConfiguration) this.getConfig();
		String host = config.getString("Host","localhost");
		if (!query.connectTS3Query(host , 10011)){
			echoError();
			return;
		}
		String admin = config.getString("ServerAdminUsername","serveradmin");
		String pass = config.getString("ServerAdminPassword","password");
		query.loginTS3(admin, pass);
	}
}
