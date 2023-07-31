package com.omegajak.powerdrop.network;

import com.omegajak.powerdrop.PowerDrop;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class PowerDropPacketHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(PowerDrop.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void init() {
        INSTANCE.registerMessage(0, PowerDropMessage.class, PowerDropMessage::encode, PowerDropMessage::decode, PowerDropMessage::handle);
    }
}
