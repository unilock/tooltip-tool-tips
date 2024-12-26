package com.shnupbups.tooltiptooltips;

import me.fzzyhmstrs.fzzy_config.api.ConfigApiJava;
import me.fzzyhmstrs.fzzy_config.api.RegisterType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TooltipToolTips implements ClientModInitializer {
    public static final String MOD_ID = "tooltiptooltips";
    public static final ModConfig CONFIG = ConfigApiJava.registerAndLoadConfig(ModConfig::new, RegisterType.CLIENT);
    private static Pattern harvestLevelPattern;

    @Override
    public void onInitializeClient() {
        ClientPlayConnectionEvents.INIT.register((a, b) -> harvestLevelPattern = null);
    }

    public static Matcher getMatcher(String input) {
        if (harvestLevelPattern == null) {
            harvestLevelPattern = Pattern.compile(CONFIG.tools.harvestLevelPattern);
        }

        return harvestLevelPattern.matcher(input);
    }
}
