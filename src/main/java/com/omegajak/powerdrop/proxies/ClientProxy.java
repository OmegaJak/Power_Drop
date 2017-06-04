package com.omegajak.powerdrop.proxies;

import com.omegajak.powerdrop.client.keys.KeyBindings;
import com.omegajak.powerdrop.client.keys.KeyInputHandler;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

public class ClientProxy extends CommonProxy {
	@Override
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(new KeyInputHandler());
		KeyBindings.init();
	}
}
