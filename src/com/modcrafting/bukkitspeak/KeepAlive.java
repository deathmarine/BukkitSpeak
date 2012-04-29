package com.modcrafting.bukkitspeak;

import java.util.HashMap;

public class KeepAlive implements Runnable{
	BukkitSpeak plugin;
	private boolean kill = false;
	public KeepAlive(BukkitSpeak instance){
		this.plugin = instance;
	}

	public void run(){
		HashMap<String, String> hmIn;
		try {
			double time = System.currentTimeMillis();
			while(!kill){
				if((System.currentTimeMillis()-time) >=300000){//keep-alive every 60s
					hmIn = plugin.query.doCommand("clientupdate");
					if (!hmIn.get("msg").equalsIgnoreCase("ok")){
						plugin.reconnect();
					}
					time = System.currentTimeMillis();
				}
				Thread.sleep(1000);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void kill(){
		kill = true;
	}
}

