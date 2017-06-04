package com.omegajak.powerdrop.client.keys;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class KeyBindings {
	public static KeyBinding drop;
	
	public static void init() {
		drop = new KeyBinding("key.powerdrop", KeyConflictContext.IN_GAME, Keyboard.KEY_Q, "key.categories.gameplay");
		Minecraft.getMinecraft().gameSettings.keyBindDrop.setKeyConflictContext(KeyConflictContext.GUI);
		ClientRegistry.registerKeyBinding(drop);
	}
}
