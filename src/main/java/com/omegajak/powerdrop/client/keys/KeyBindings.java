package com.omegajak.powerdrop.client.keys;

import cpw.mods.fml.client.registry.ClientRegistry;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.StatCollector;

public class KeyBindings {
	public static KeyBinding drop;
	
	public static void init() {
		drop = new KeyBinding(StatCollector.translateToLocal("key.powerdrop"), 16, "key.categories.gameplay");
		
		ClientRegistry.registerKeyBinding(drop);
	}
}
