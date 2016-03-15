package com.omegajak.powerdrop.common;

import java.io.File;

import net.minecraftforge.common.config.Configuration;
import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class Config {
	
	public static Configuration config;
	public static File configFile;

	public static boolean maxPower;
	
	public static void init(File file) {
		configFile = file;
		config = new Configuration(configFile);
		
		config.load();
		load();
		
		FMLCommonHandler.instance().bus().register(new Config.EventHandler());
	}
	
	public static void load() {
		maxPower = config.get(Configuration.CATEGORY_GENERAL, "showMaxPower", true, "Set this to false to disable the \"MAXIMUM POWER ACHIEVED\" message.").getBoolean();
		System.out.println("Show Max Power = " + maxPower);
		
		config.save();
	}
	
	public static class EventHandler {
		@SubscribeEvent
		public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
			System.out.println("OnConfigChangedEvent");
			if (event.modID.equals(PowerDrop.MODID)) {
				load();
			}
		}
	}
}
