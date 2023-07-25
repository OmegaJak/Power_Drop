package com.omegajak.powerdrop.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.omegajak.powerdrop.PowerDrop;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class KeybindHandler {
    public static final KeyMapping POWER_DROP_KEY = new KeyMapping("powerdrop.key.drop", KeyConflictContext.IN_GAME, KeyModifier.SHIFT, InputConstants.Type.KEYSYM.getOrCreate(InputConstants.KEY_Q), "key.categories.inventory");

    public static void register(FMLClientSetupEvent event) {
        MinecraftForge.EVENT_BUS.addListener(KeybindHandler::handleKeyInputEvent);
        event.enqueueWork(() -> {
           ClientRegistry.registerKeyBinding(POWER_DROP_KEY);
        });
    }

    public static void handleKeyInputEvent(TickEvent.ClientTickEvent event) {
        if (POWER_DROP_KEY.consumeClick()) {
            PowerDrop.LOGGER.debug("Pressed the power drop key!");
        }
    }
}
