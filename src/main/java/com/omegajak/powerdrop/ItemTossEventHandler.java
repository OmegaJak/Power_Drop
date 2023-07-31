package com.omegajak.powerdrop;

import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.HashMap;
import java.util.UUID;

public class ItemTossEventHandler {
    private static final HashMap<UUID, Double> PLAYER_DROP_STRENGTHS = new HashMap<>();

    public static void setDropStrength(UUID playerUuid, double throw_strength) {
        PLAYER_DROP_STRENGTHS.put(playerUuid, throw_strength);
    }

    @SubscribeEvent
    public void onPlayerTossEvent(ItemTossEvent event) {
        Vector3d currentDelta = event.getEntity().getDeltaMovement();
        Double drop_strength = PLAYER_DROP_STRENGTHS.remove(event.getPlayer().getUUID());
        if (drop_strength != null) {
            Vector3d newDelta = currentDelta.multiply(drop_strength, drop_strength, drop_strength);
            event.getEntity().setDeltaMovement(newDelta);
        }
    }
}
