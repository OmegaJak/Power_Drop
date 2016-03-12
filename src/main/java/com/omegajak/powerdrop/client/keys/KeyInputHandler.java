package com.omegajak.powerdrop.client.keys;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
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
		if (KeyBindings.drop.getIsKeyPressed()) {
			double chargeFactor = convertChargeTimeToFactor(System.currentTimeMillis() - keyDownTime);
			if (chargeFactor == 4.0 && lastChargeFactor != 4.0) {
				EntityPlayer player = Minecraft.getMinecraft().thePlayer;
				player.addChatMessage(new ChatComponentText("MAXIMUM POWER ACHIEVED").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GOLD)));
				lastChargeFactor = 4.0;
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
		}
	}
}
