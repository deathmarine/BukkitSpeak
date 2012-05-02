package com.modcrafting.bukkitspeak;

import java.util.HashMap;

public class KeepAlive implements Runnable{
	BukkitSpeak plugin;
	public KeepAlive(BukkitSpeak instance){
		this.plugin = instance;
	}

	public void run(){
		HashMap<String, String> hmIn;
		try {
			hmIn = plugin.query.doCommand("clientupdate");
			if (!hmIn.get("msg").equalsIgnoreCase("ok")){
				plugin.reconnect();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

