package com.omegajak.powerdrop.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Vec3;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;

public class DropMessage implements IMessage {

	private double chargeFactor;
	
	public DropMessage() {
		chargeFactor = 1.0;
	}
	
	public DropMessage(double chargeFactor) {
		this.chargeFactor = chargeFactor != 0 ? chargeFactor : 1.0;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		this.chargeFactor = buf.readDouble();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeDouble(chargeFactor);
	}
	
	public static class Handler implements IMessageHandler<DropMessage, DropMessage> {
		@Override
		public DropMessage onMessage(DropMessage message, MessageContext ctx) {
			if (ctx.side == Side.SERVER) {
				////System.out.println("ChargeFactor is: " + message.chargeFactor);
				EntityPlayerMP player = ctx.getServerHandler().playerEntity;
				
				if (player.inventory.getCurrentItem() != null) {
					ItemStack currentItem = player.inventory.getCurrentItem().copy();
					currentItem.stackSize = 1;
					EntityItem dropped = new EntityItem(player.worldObj, player.posX, player.posY + player.eyeHeight - 0.39, player.posZ, currentItem);
					dropped.delayBeforeCanPickup = 20; // Ticks
					
					double normalizer = 3.1;
					Vec3 lookVector = player.getLookVec();
					dropped.motionX = (lookVector.xCoord / normalizer) * message.chargeFactor;
					dropped.motionY = (lookVector.yCoord / normalizer) * message.chargeFactor + 0.12;
					dropped.motionZ = (lookVector.zCoord / normalizer) * message.chargeFactor;
					
					player.worldObj.spawnEntityInWorld(dropped);
					
					if (player.inventory.getCurrentItem().stackSize > 1)
						player.inventory.getCurrentItem().stackSize--;
					else
						player.inventory.mainInventory[player.inventory.currentItem] = null;
				}
				
				
				return message;
			} else {
				
			}
			return null;
		}
	}
}
