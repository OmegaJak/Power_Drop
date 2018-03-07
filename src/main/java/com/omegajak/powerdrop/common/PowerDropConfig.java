package com.omegajak.powerdrop.common;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = PowerDrop.MODID)
public class PowerDropConfig {

	@Config.Comment("Set this to false to disable the \\\"MAXIMUM POWER ACHIEVED\\\" message.")
	public static boolean maxPower = false;
	
	@Config.Comment("Set to false to disable FOV adjustment when charging for a power drop.")
	public static boolean adjustFOV = true;
	
	@Mod.EventBusSubscriber(modid = PowerDrop.MODID)
	private static class EventHandler {
		@SubscribeEvent
		public static void onConfigChanged(final ConfigChangedEvent.OnConfigChangedEvent event) {
			if (event.getModID().equals(PowerDrop.MODID)) {
				ConfigManager.sync(PowerDrop.MODID, Config.Type.INSTANCE);
			}
		}
	}
	
}
