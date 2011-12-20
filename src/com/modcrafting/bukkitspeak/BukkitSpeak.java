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
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import de.stefan1200.jts3serverquery.JTS3ServerQuery;

public class BukkitSpeak extends JavaPlugin{

	private final Playerlistener playerListener = new Playerlistener(this);
	private final TSListener tslistener = new TSListener(this);
	public JTS3ServerQuery query = new JTS3ServerQuery();
	public HashSet<String> loggedPlayers = new HashSet<String>();
	public final static Logger log = Logger.getLogger("Minecraft");
	public String maindir = "plugins/BukkitSpeak/";
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
		new File(maindir).mkdir();
		createDefaultConfiguration("config.yml"); //Swap for new setup
		loadCommands();
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Event.Type.PLAYER_CHAT, playerListener, Priority.Low, this);
		pm.registerEvent(Event.Type.PLAYER_LOGIN, playerListener, Priority.Monitor, this);
		pm.registerEvent(Event.Type.PLAYER_QUIT, playerListener, Priority.Monitor, this);
		String host = config.getString("Host","localhost");
		if (!query.connectTS3Query(host , 10011)){
			log.log(Level.INFO, "[" + pdfFile.getName() + "]" + " is unable to be enabled!");
			echoError();
			return;
		}
		String admin = config.getString("ServerAdminUsername","serveradmin");
		String pass = config.getString("ServerAdminPassword","password");
		query.loginTS3(admin, pass);
		log.log(Level.INFO, "[" + pdfFile.getName() + "]" + " Logging into Team Speak 3 Server!");
		query.setTeamspeakActionListener(tslistener);
		if (!query.selectVirtualServer(1))
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
