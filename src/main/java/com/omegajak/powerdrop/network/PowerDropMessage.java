package com.omegajak.powerdrop.network;

import com.omegajak.powerdrop.ItemTossEventHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PowerDropMessage {
    private final boolean ctrlPressed;
    private final double dropStrength;

    public PowerDropMessage(boolean ctrlPressed, double dropStrength) {
        this.ctrlPressed = ctrlPressed;
        this.dropStrength = dropStrength;
    }

    public static void encode(PowerDropMessage msg, FriendlyByteBuf byteBuf) {
        byteBuf.writeBoolean(msg.ctrlPressed);
        byteBuf.writeDouble(msg.dropStrength);
    }

    public static PowerDropMessage decode(FriendlyByteBuf byteBuf) {
        return new PowerDropMessage(byteBuf.readBoolean(), byteBuf.readDouble());
    }

    public static void handle(PowerDropMessage msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();

            if (player != null) {
                player.resetLastActionTime();
                if (!player.isSpectator()) {
                    //TODO: Investigate whether this is thread safe. Definitely risky...
                    ItemTossEventHandler.setDropStrength(player.getUUID(), msg.dropStrength);
                    player.drop(msg.ctrlPressed);
                }
            }
        });

        ctx.get().setPacketHandled(true);
    }
}
