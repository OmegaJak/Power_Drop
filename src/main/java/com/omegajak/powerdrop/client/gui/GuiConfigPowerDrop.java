package com.omegajak.powerdrop.client.gui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;

import com.omegajak.powerdrop.common.Config;
import com.omegajak.powerdrop.common.PowerDrop;

import cpw.mods.fml.client.config.GuiConfig;

public class GuiConfigPowerDrop extends GuiConfig {
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public GuiConfigPowerDrop(GuiScreen parent) {
		super(parent,
				new ConfigElement(Config.config.getCategory(Configuration.CATEGORY_GENERAL)).getChildElements(),
				PowerDrop.MODID,
				false,
				false,
				GuiConfig.getAbridgedConfigPath(Config.configFile.getAbsolutePath()));
		//titleLine2 = Config.config
	}
}
