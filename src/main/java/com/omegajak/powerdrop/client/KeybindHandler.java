package com.omegajak.powerdrop.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.omegajak.powerdrop.PowerDrop;
import com.omegajak.powerdrop.PowerDropConfig;
import com.omegajak.powerdrop.network.PowerDropMessage;
import com.omegajak.powerdrop.network.PowerDropPacketHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ComputeFovModifierEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public class KeybindHandler {
    public static final TimedKeyMapping POWER_DROP_KEY = new TimedKeyMapping(
            "powerdrop.key.drop",
            KeyConflictContext.IN_GAME,
            KeyModifier.SHIFT,
            InputConstants.Type.KEYSYM,
            InputConstants.KEY_Q,
            "key.categories.powerdrop"
    );

    public static final double MAX_CHARGE_FACTOR = 4.0;
    public static final long MIN_TIME_BEFORE_FOV_EFFECTS_MS = 150;

    private static float FOV_MULTIPLIER = 1.0f;
    private static boolean IS_FOV_RESETTING = false;
    private static boolean CTRL_PRESSED_INITIALLY = false;
    private static boolean ALREADY_EMITTED_MAX_POWER_MSG = false;
    private static boolean IS_CHARGING = false;

    @SubscribeEvent
    public static void handleKeyInputEvent(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;

        if (POWER_DROP_KEY.consumeHoldStart()) {
            CTRL_PRESSED_INITIALLY = Screen.hasControlDown();
            IS_CHARGING = true;
        }

        if (IS_CHARGING && POWER_DROP_KEY.isDown() && POWER_DROP_KEY.getTimeSinceKeyDown() > MIN_TIME_BEFORE_FOV_EFFECTS_MS) {
            FOV_MULTIPLIER = getFOVMultiplier(POWER_DROP_KEY.getTimeSinceKeyDown());

            if (getChargeFactor(POWER_DROP_KEY.getTimeSinceKeyDown()) >= MAX_CHARGE_FACTOR && !ALREADY_EMITTED_MAX_POWER_MSG && PowerDropConfig.INSTANCE.emitMaxPowerMsg.get()) {
                LocalPlayer player = Minecraft.getInstance().player;
                if (player != null) {
                    player.sendSystemMessage(Component.literal("MAXIMUM POWER ACHIEVED").setStyle(Style.EMPTY.withColor(ChatFormatting.GOLD)));
                    ALREADY_EMITTED_MAX_POWER_MSG = true;
                }
            }
        } else if (IS_FOV_RESETTING) {
            float diff = FOV_MULTIPLIER - 1.0f;
            if (diff < 0.008f) {
                FOV_MULTIPLIER = 1.0f;
                IS_FOV_RESETTING = false;
            } else {
                FOV_MULTIPLIER -= diff / 3.0f;
            }
        }

        avoidConflictWithVanillaDrop();

        if (POWER_DROP_KEY.consumeHoldEnd()) {
            double dropStrength = getChargeFactor(POWER_DROP_KEY.getLastReleaseHoldTimeMs());
            sendDropPacket(dropStrength);

            IS_FOV_RESETTING = true;
            ALREADY_EMITTED_MAX_POWER_MSG = false;
            IS_CHARGING = false;
        }
    }

    @SubscribeEvent
    public static void onGetFieldOfViewEvent(ComputeFovModifierEvent event) {
        if (PowerDropConfig.INSTANCE.adjustFOV.get()) {
            event.setNewFovModifier(event.getFovModifier() * FOV_MULTIPLIER);
        }
    }

    private static void sendDropPacket(double dropStrength) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null && !player.getInventory().getSelected().isEmpty()) {
            player.getInventory().removeFromSelected(CTRL_PRESSED_INITIALLY); // Mirrors LocalPlayer.drop, needed for client to update properly
            PowerDropPacketHandler.INSTANCE.sendToServer(new PowerDropMessage(CTRL_PRESSED_INITIALLY, dropStrength));
        }
    }

    private static void avoidConflictWithVanillaDrop() {
        Minecraft minecraft = Minecraft.getInstance();
        if (IS_CHARGING) {
            while (minecraft.options.keyDrop.consumeClick()) {
                // Do nothing, we just want to make sure we consumed them all to suppress the vanilla event
            }
        }
    }

    private static double getChargeFactor(long chargeTimeMs) {
        double x = chargeTimeMs / 1000.0;
        double y = Math.atan(x) * 2.4 + 1.0; // Makes a nice curve that increases steeply initially, levels off and hits 4 after about 3 seconds

        y = Math.min(y, MAX_CHARGE_FACTOR);

        return y;
    }

    private static float getFOVMultiplier(long chargeTimeMs) {
        double chargeFactor = getChargeFactor(chargeTimeMs);
        chargeFactor -= 1.0; // Base charge factor is 1, we want it to be 0
        return 1.0F + (float) (chargeFactor / 8.0);
    }

    @Mod.EventBusSubscriber(modid = PowerDrop.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ClientModBusEvents {
        @SubscribeEvent
        public static void registerKeybinds(RegisterKeyMappingsEvent event) {
            event.register(POWER_DROP_KEY);
        }
    }
}
