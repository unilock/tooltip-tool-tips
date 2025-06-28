package com.shnupbups.tooltiptooltips;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TooltipToolTips implements ClientModInitializer {
	public static final String MOD_ID = "tooltiptooltips";
	public static final ModConfig CONFIG = ModConfig.createToml(FabricLoader.getInstance().getConfigDir(), "", MOD_ID, ModConfig.class);
	private static Pattern harvestLevelPattern;

	@Override
	public void onInitializeClient() {
		CONFIG.registerCallback(config -> {
			harvestLevelPattern = null;
		});
	}

	public static Matcher getMatcher(String input) {
		if (harvestLevelPattern == null) {
			harvestLevelPattern = Pattern.compile(CONFIG.tools.harvestLevelPattern.value());
		}

		return harvestLevelPattern.matcher(input);
	}
}
