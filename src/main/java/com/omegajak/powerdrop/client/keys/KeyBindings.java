package com.omegajak.powerdrop.client.keys;

import cpw.mods.fml.client.registry.ClientRegistry;
import net.minecraft.client.settings.KeyBinding;

public class KeyBindings {
	public static KeyBinding drop;
	
	public static void init() {
		drop = new KeyBinding("key.powerDrop", 16, "key.categories.gameplay");
		
		ClientRegistry.registerKeyBinding(drop);
	}
}
