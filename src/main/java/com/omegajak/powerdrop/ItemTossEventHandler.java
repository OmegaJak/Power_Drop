package com.omegajak.powerdrop;

import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ItemTossEventHandler {
    @SubscribeEvent
    public void onPlayerTossEvent(ItemTossEvent event) {
        Vec3 currentDelta = event.getEntity().getDeltaMovement();
        Vec3 newDelta = currentDelta.multiply(10.0, 10.0, 10.0);
        event.getEntity().setDeltaMovement(newDelta);
    }
}
