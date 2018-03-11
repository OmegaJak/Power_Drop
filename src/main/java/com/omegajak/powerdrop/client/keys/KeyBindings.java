package com.omegajak.powerdrop.client.keys;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class KeyBindings {
	public static KeyBinding drop;
	
	public static void init() {
		drop = new KeyBinding("key.powerdrop", Keyboard.KEY_Q, "key.categories.gameplay");
		ClientRegistry.registerKeyBinding(drop);
	}
}
