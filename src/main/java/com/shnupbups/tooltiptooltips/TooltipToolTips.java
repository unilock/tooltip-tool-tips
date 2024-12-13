package com.shnupbups.tooltiptooltips;

import me.fzzyhmstrs.fzzy_config.api.ConfigApiJava;
import me.fzzyhmstrs.fzzy_config.api.RegisterType;
import net.fabricmc.api.ClientModInitializer;

public class TooltipToolTips implements ClientModInitializer {
    public static final String MOD_ID = "tooltiptooltips";
    public static final ModConfig CONFIG = ConfigApiJava.registerAndLoadConfig(ModConfig::new, RegisterType.CLIENT);

    @Override
    public void onInitializeClient() {
        // this is just to initialize the config
    }
}
