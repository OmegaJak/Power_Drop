package com.omegajak.powerdrop;

import com.mojang.logging.LogUtils;
import com.omegajak.powerdrop.client.KeybindHandler;
import com.omegajak.powerdrop.network.PowerDropPacketHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(PowerDrop.MODID)
public class PowerDrop
{
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final String MODID = "powerdrop";

    public PowerDrop()
    {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, PowerDropConfig.SPEC);

        final IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register the setup method for modloading
        modBus.addListener(this::setup);
        modBus.addListener(this::clientSetup);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new ItemTossEventHandler());
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        PowerDropPacketHandler.init();
    }

    private void clientSetup(FMLClientSetupEvent event) {
        MinecraftForge.EVENT_BUS.register(KeybindHandler.class);
    }
}
