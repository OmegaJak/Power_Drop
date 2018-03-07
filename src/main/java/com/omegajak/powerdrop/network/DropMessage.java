package com.omegajak.powerdrop.network;

import com.omegajak.powerdrop.common.PowerDrop;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class DropMessage implements IMessage {

	private double chargeFactor;
	private boolean isCtrlDown;
	
	public DropMessage() {
		chargeFactor = 1.0;
	}
	
	public DropMessage(double chargeFactor, boolean isCtrlDown) {
		this.chargeFactor = chargeFactor != 0 ? chargeFactor : 1.0;
		this.isCtrlDown = isCtrlDown;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		this.chargeFactor = buf.readDouble();
		this.isCtrlDown = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeDouble(chargeFactor);
		buf.writeBoolean(isCtrlDown);
	}
	
	public static class Handler implements IMessageHandler<DropMessage, DropMessage> {
		@Override
		public DropMessage onMessage(final DropMessage message, final MessageContext ctx) {
			if (ctx.side == Side.SERVER) {
				IThreadListener thread = PowerDrop.proxy.getListener(ctx);
				thread.addScheduledTask(new Runnable() {
					@Override
					public void run() {
						EntityPlayerMP player = ctx.getServerHandler().playerEntity;
						
						if (player.inventory.getCurrentItem() != null) {
							ItemStack currentItem = player.inventory.getCurrentItem().copy();
							
							if (message.isCtrlDown) {
								currentItem.stackSize = player.inventory.getCurrentItem().stackSize;
							} else {
								currentItem.stackSize = 1;
							}
							
							EntityItem dropped = new EntityItem(player.worldObj, player.posX, player.posY + player.eyeHeight - 0.39, player.posZ, currentItem);
							dropped.setPickupDelay(40); // Ticks until it can be picked up again
							
							
							double normalizer = 3.1;
							Vec3d lookVector = player.getLookVec();
							dropped.motionX = (lookVector.xCoord / normalizer) * message.chargeFactor;
							dropped.motionY = (lookVector.yCoord / normalizer) * message.chargeFactor + 0.12;
							dropped.motionZ = (lookVector.zCoord / normalizer) * message.chargeFactor;
							
							player.worldObj.spawnEntityInWorld(dropped);
							
							if (player.inventory.getCurrentItem().stackSize > 1 && !message.isCtrlDown) // If it was just a normal throw
								player.inventory.getCurrentItem().stackSize--;
							else // If it was either the last item of the stack or they held control
								player.inventory.mainInventory[player.inventory.currentItem] = null; // Either way, the stack should be no more
							
							MinecraftForge.EVENT_BUS.post(new ItemTossEvent(dropped, player));
						}
					}
				});
			} else {
				
			}
			return null;
		}
	}
}
