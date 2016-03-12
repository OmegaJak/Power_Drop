package com.omegajak.powerdrop.proxies;

import com.omegajak.powerdrop.client.keys.KeyBindings;
import com.omegajak.powerdrop.client.keys.KeyInputHandler;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;

public class ClientProxy extends CommonProxy {
	@Override
	public void init(FMLInitializationEvent event) {
		FMLCommonHandler.instance().bus().register(new KeyInputHandler());
		KeyBindings.init();
	}
}
