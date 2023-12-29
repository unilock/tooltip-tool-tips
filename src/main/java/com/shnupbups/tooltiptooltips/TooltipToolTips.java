package com.shnupbups.tooltiptooltips;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class TooltipToolTips implements ClientModInitializer {
    public static final Config CONFIG = Config.createToml(FabricLoader.getInstance().getConfigDir(), "tooltiptooltips", "config", Config.class);

    @Override
    public void onInitializeClient() {
        // this is just to initialize the config
    }
}
