package com.omegajak.powerdrop.common;

import java.io.File;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Config {
	
	public static Configuration config;
	public static File configFile;

	public static boolean maxPower;
	public static boolean adjustFOV;
	
	public static void init(File file) {
		configFile = file;
		config = new Configuration(configFile);
		
		config.load();
		load();
		
		MinecraftForge.EVENT_BUS.register(new Config.EventHandler());
	}
	
	public static void load() {
		maxPower = config.get(Configuration.CATEGORY_GENERAL, "showMaxPower", true, "Set this to false to disable the \"MAXIMUM POWER ACHIEVED\" message.").getBoolean();
		adjustFOV = config.get(Configuration.CATEGORY_GENERAL, "adjustFOV", true, "Set to to disable FOV adjustment when charging for a power drop.").getBoolean();
		
		config.save();
	}
	
	public static class EventHandler {
		@SubscribeEvent
		public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
			System.out.println("OnConfigChangedEvent");
			if (event.getModID().equals(PowerDrop.MODID)) {
				load();
			}
		}
	}
}
