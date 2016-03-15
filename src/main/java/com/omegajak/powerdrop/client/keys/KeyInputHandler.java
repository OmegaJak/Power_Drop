package com.omegajak.powerdrop.client.keys;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;

import com.omegajak.powerdrop.common.Config;
import com.omegajak.powerdrop.common.PowerDrop;
import com.omegajak.powerdrop.network.DropMessage;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

public class KeyInputHandler {
	
	boolean previousQState = false;
	long keyDownTime = 0;
	long finalChargeTime = 0;
	boolean wasCtrlDown = false;
	
	double lastChargeFactor = 0;
	
	float fovMultiplier = 1.0F;
	float originalFOV;
	boolean isFOVResetting = false;
	
	/**
	 * Converts chargeTime into a multiplier used to determine throw distance
	 * @param chargeTime - Time that throw button was pressed down in ms
	 * @return A number between 1.0 and 4.0
	 */
	public double convertChargeTimeToFactor(long chargeTime) {
		double x = chargeTime/1000.0;
		double y = Math.atan(x/2.0)* 2.75 + 1.0; // Makes a nice curve that increases stteply initially, levels off
		
		if (y > 4.0) y = 4.0;
		
		return y;
	}
	
	@SubscribeEvent
	public void onClientTickEvent(TickEvent.ClientTickEvent event) {
		if (KeyBindings.drop.getIsKeyPressed() || isFOVResetting) {
			GameSettings settings = Minecraft.getMinecraft().gameSettings; // Just so I don't have to keep typing this
			
			// The second half of this statement is half trying to predict whether the next drop will be short or long so as to not make the FOV go in/out
			// 		and half making sure that if a long drop is attempted after short drops, the fov will catch up
			if (KeyBindings.drop.getIsKeyPressed() && (finalChargeTime > 120 || System.currentTimeMillis() - keyDownTime > 150)) {
				double chargeFactor = convertChargeTimeToFactor(System.currentTimeMillis() - keyDownTime);
				if (chargeFactor == 4.0 && lastChargeFactor != 4.0) { // If we just now reached the maximum power
					EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
					lastChargeFactor = 4.0; // So we don't keep spamming the max message
					
					if (Config.maxPower)
						player.addChatMessage(new ChatComponentText("MAXIMUM POWER ACHIEVED").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GOLD)));
				}
				
				chargeFactor -= 1.0;
				Minecraft.getMinecraft().gameSettings.fovSetting /= fovMultiplier; // Reset so we don't have cumulative multiplication (can get out of hand very fast)
				fovMultiplier = 1.0F + (float)(chargeFactor / 8.0);
				
				// Max chargeFactor is 4.0, so max fovMultiplier is 1.5
				if (settings.fovSetting * fovMultiplier < originalFOV * 1.5) // If we haven't reached the max fov yet
					settings.fovSetting *= fovMultiplier;
				else
					settings.fovSetting = originalFOV * 1.5F; // Hard cap at multiplying by 1.5F
			} else if (isFOVResetting) {			
				float diff = settings.fovSetting - originalFOV;
				
				if (diff / 8.0F < 0.001F) { // Gotta stop at some point
					settings.fovSetting = originalFOV; // Reset
					isFOVResetting = false;
					fovMultiplier = 1.0F;
				} else {
					settings.fovSetting -= diff / 3.0F; // Produces a nice, smooth curve for resetting
					fovMultiplier = originalFOV / settings.fovSetting; // This is necessary for when the reset isn't allowed to finish
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onKeyInput(InputEvent.KeyInputEvent event) {
		if (KeyBindings.drop.getIsKeyPressed() && KeyBindings.drop.isPressed() && !previousQState) {
			keyDownTime = System.currentTimeMillis();
			previousQState = true;
			
			wasCtrlDown = GuiScreen.isCtrlKeyDown();
			
			if (!isFOVResetting)
				originalFOV = Minecraft.getMinecraft().gameSettings.fovSetting;
			
		} else if (!KeyBindings.drop.getIsKeyPressed() && !KeyBindings.drop.isPressed() && previousQState) {
			finalChargeTime = System.currentTimeMillis() - keyDownTime;
			
			PowerDrop.network.sendToServer(new DropMessage(convertChargeTimeToFactor(finalChargeTime), wasCtrlDown));
			
			wasCtrlDown = false;
			
			previousQState = false;
			lastChargeFactor = 0.0;
			
			isFOVResetting = true;
		}
	}
}
