package com.omegajak.powerdrop.client.keys;

import com.omegajak.powerdrop.common.Config;
import com.omegajak.powerdrop.common.PowerDrop;
import com.omegajak.powerdrop.network.DropMessage;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

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
		double y = Math.atan(x/2.0)* 2.75 + 1.0; // Makes a nice curve that increases steeply initially, levels off
		
		if (y > 4.0) y = 4.0;
		
		return y;
	}
	
	@SubscribeEvent
	public void onClientTickEvent(TickEvent.ClientTickEvent event) {
		if (Config.adjustFOV || Config.maxPower) { // No reason to do any of this if neither FOV nor maxPower is true
			if (KeyBindings.drop.isKeyDown() && !previousQState) {
				keyDownTime = System.currentTimeMillis();
				previousQState = true;
				
				wasCtrlDown = GuiScreen.isCtrlKeyDown();
				
				if (!isFOVResetting)
					originalFOV = Minecraft.getMinecraft().gameSettings.fovSetting;
				
			} else if (!KeyBindings.drop.isKeyDown() && previousQState) {
				finalChargeTime = System.currentTimeMillis() - keyDownTime;
				
				PowerDrop.network.sendToServer(new DropMessage(convertChargeTimeToFactor(finalChargeTime), wasCtrlDown));
				
				wasCtrlDown = false;
				
				previousQState = false;
				lastChargeFactor = 0.0;
				
				isFOVResetting = true;
			}
			if (KeyBindings.drop.isKeyDown() || isFOVResetting) {
				GameSettings settings = Minecraft.getMinecraft().gameSettings; // Just so I don't have to keep typing this

				// The second half of this statement is half trying to predict whether the next drop will be short or long so as to not make the FOV go in/out
				// 		and half making sure that if a long drop is attempted after short drops, the fov will catch up
				if (KeyBindings.drop.isKeyDown() && (finalChargeTime > 120 || System.currentTimeMillis() - keyDownTime > 150)) {
					double chargeFactor = convertChargeTimeToFactor(System.currentTimeMillis() - keyDownTime);
					if (chargeFactor == 4.0 && lastChargeFactor != 4.0) { // If we just now reached the maximum power
						EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
						lastChargeFactor = 4.0; // So we don't keep spamming the max message
						if (Config.maxPower)
							player.addChatMessage(new TextComponentString("MAXIMUM POWER ACHIEVED").setStyle(new Style().setColor(TextFormatting.GOLD)));
					}

					if (Config.adjustFOV) { // If adjustFOV is false, we still want the rest to happen so we have the change to get the chat message
						chargeFactor -= 1.0;
						Minecraft.getMinecraft().gameSettings.fovSetting /= fovMultiplier; // Reset so we don't have cumulative multiplication (can get out of hand very fast)
						fovMultiplier = 1.0F + (float) (chargeFactor / 8.0);

						// Max chargeFactor is 4.0, so max fovMultiplier is 1.5
						if (settings.fovSetting * fovMultiplier < originalFOV * 1.5) {// If we haven't reached the max fov yet
							settings.fovSetting *= fovMultiplier;
						} else {
							settings.fovSetting = originalFOV * 1.5F; // Hard cap at multiplying by 1.5F
						}
					}
				} else if (isFOVResetting && Config.adjustFOV) {
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
	}
}
