package com.omegajak.powerdrop.common;

import com.omegajak.powerdrop.network.DropMessage;
import com.omegajak.powerdrop.proxies.CommonProxy;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = PowerDrop.MODID,
		name = PowerDrop.MODNAME,
		version = PowerDrop.VERSION,
		guiFactory = "com.omegajak." + PowerDrop.MODID + ".client.gui.GuiFactoryPowerDrop")
public class PowerDrop {
	
	public static final String MODID = "powerdrop";
	public static final String MODNAME = "Power Drop";
	public static final String VERSION = "1.0.1";
	
	public static SimpleNetworkWrapper network;
	
	@Instance
	public static PowerDrop idnstance = new PowerDrop();
	
	@SidedProxy(clientSide="com.omegajak.powerdrop.proxies.ClientProxy", serverSide="com.omegajak.powerdrop.proxies.CommonProxy")
	public static CommonProxy proxy;
	
	@EventHandler
    public void preInit(FMLPreInitializationEvent e) {
		proxy.preInit(e);
		
		network = NetworkRegistry.INSTANCE.newSimpleChannel("PowerDrop");
		network.registerMessage(DropMessage.Handler.class, DropMessage.class, 0, Side.SERVER);
		
		Config.init(e.getSuggestedConfigurationFile());
    }
        
    @EventHandler
    public void init(FMLInitializationEvent e) {
    	proxy.init(e);
    }
        
    @EventHandler
    public void postInit(FMLPostInitializationEvent e) {
    	proxy.postInit(e);
    }
}
