package cc.unilock.tooltiptooltips;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class TooltipToolTips implements ClientModInitializer {
	public static final String MOD_ID = "tooltiptooltips";
	public static final ModConfig CONFIG = ModConfig.createToml(FabricLoader.getInstance().getConfigDir(), "", MOD_ID, ModConfig.class);

	@Override
	public void onInitializeClient() {
		// NO-OP
	}
}
