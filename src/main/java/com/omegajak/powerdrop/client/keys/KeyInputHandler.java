package com.omegajak.powerdrop.client.keys;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;

import com.omegajak.powerdrop.PowerDrop;
import com.omegajak.powerdrop.network.DropMessage;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

public class KeyInputHandler {
	
	boolean previousQState = false;
	long keyDownTime = 0;
	boolean wasCtrlDown = false;
	
	double lastChargeFactor = 0;
	
	float fovMultiplier = 1.0F;
	float originalFOV;
	boolean isFOVResetting = false;
	
	public double convertChargeTimeToFactor(long chargeTime) {
		//return 1.0;
		/*double chargeFactor = 3.6;
		if (chargeTime > 100) { // Gotta be over this threshold to be worth changing
			chargeFactor -= ((double)chargeTime * 1.3) / 1000.0;
		}
		if (chargeFactor < 1.5) chargeFactor = 1.5;
		return chargeFactor;*/
		double x = chargeTime/1000.0;
		//if (x < 0) x = 0;
		//double y = Math.pow(x - 2.0, 1.0/3.0) + Math.pow(2.0, 1.0/3.0) + 1.0;
		double y = Math.atan(x/2.0)* 2.75 + 1.0;
		
		if (y > 4.0) y = 4;
		
		return y;
	}
	
	@SubscribeEvent
	public void onClientTickEvent(TickEvent.ClientTickEvent event) {
		GameSettings settings = Minecraft.getMinecraft().gameSettings; // Just so I don't have to keep typing this
		if (KeyBindings.drop.getIsKeyPressed()) {
			double chargeFactor = convertChargeTimeToFactor(System.currentTimeMillis() - keyDownTime);
			if (chargeFactor == 4.0 && lastChargeFactor != 4.0) {
				EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
				player.addChatMessage(new ChatComponentText("MAXIMUM POWER ACHIEVED").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GOLD)));
				lastChargeFactor = 4.0;
			}
			
			chargeFactor -= 1.0;
			Minecraft.getMinecraft().gameSettings.fovSetting /= fovMultiplier;
			fovMultiplier = 1.0F + (float)(chargeFactor / 8.0);
			//fovMultiplier = fovMultiplier < 1.0F ? 1.0F : fovMultiplier; // Floor
			System.out.println("Multiplier: " + fovMultiplier);
			
			if (settings.fovSetting * fovMultiplier < originalFOV * 1.5)
				settings.fovSetting *= fovMultiplier;
			else
				settings.fovSetting = originalFOV * 1.5F;
		} else if (isFOVResetting) {
			/*float diff = fovMultiplier - 1.0F;
			if (diff < 0.001F) {
				fovMultiplier = 1.0F;
			} else {
				fovMultiplier = fovMultiplier - (diff / 8.0F);
			}
			Minecraft.getMinecraft().gameSettings.fovSetting /= fovMultiplier;*/
			
			float diff = settings.fovSetting - originalFOV;
			
			if (diff / 8.0F < 0.001F) {
				settings.fovSetting = originalFOV;
				isFOVResetting = false;
				fovMultiplier = 1.0F;
				System.out.println("Reset");
			} else {
				settings.fovSetting -= diff / 8.0F;
				fovMultiplier = originalFOV / settings.fovSetting;
				System.out.println("fovSetting: " + settings.fovSetting);
			}
		}
	}
	
	@SubscribeEvent
	public void onKeyInput(InputEvent.KeyInputEvent event) {
		//MinecraftForge.EVENT_BUS.register(target);
		//System.out.println("Drop isPressed() = " + KeyBindings.drop.getIsKeyPressed() + " " + KeyBindings.drop.isPressed() + " " + previousQState);
		if (KeyBindings.drop.getIsKeyPressed() && KeyBindings.drop.isPressed() && !previousQState) {
			//System.out.println("Q Pressed Down");
			keyDownTime = System.currentTimeMillis();
			previousQState = true;
			
			wasCtrlDown = GuiScreen.isCtrlKeyDown();
			
			if (!isFOVResetting)
				originalFOV = Minecraft.getMinecraft().gameSettings.fovSetting;
			
			/*FOVUpdateEvent fovUpdateEvent = new FOVUpdateEvent(Minecraft.getMinecraft().thePlayer, 4.0F);
			MinecraftForge.EVENT_BUS.post(fovUpdateEvent);*/
		} else if (!KeyBindings.drop.getIsKeyPressed() && !KeyBindings.drop.isPressed() && previousQState) {
			long timeElapsed = System.currentTimeMillis() - keyDownTime;
			////System.out.println(timeElapsed + " milliseconds elapsed.");
			//System.out.println("Q Released");
			
			//EntityClientPlayerMP currentPlayer = Minecraft.getMinecraft().thePlayer;
			//currentPlayer.sendChatMessage("ChargeFactor: " + String.format("%.5g%n", convertChargeTimeToFactor(timeElapsed)) + ", timeElapsed: " + timeElapsed);
			
			PowerDrop.network.sendToServer(new DropMessage(convertChargeTimeToFactor(timeElapsed), wasCtrlDown));
			
			wasCtrlDown = false;
			
			//Minecraft.getMinecraft().thePlayer.inventory.mainInventory
			
			previousQState = false;
			lastChargeFactor = 0.0;
			
			isFOVResetting = true;
			/*Minecraft.getMinecraft().gameSettings.fovSetting /= fovMultiplier;
			fovMultiplier = 1.0F;*/
			/*FOVUpdateEvent fovUpdateEvent = new FOVUpdateEvent(Minecraft.getMinecraft().thePlayer, 2.0F);
			MinecraftForge.EVENT_BUS.post(fovUpdateEvent);*/
		}
	}
}
