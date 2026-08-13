package cc.unilock.tooltiptooltips;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.ToolMaterial;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class TooltipToolTips implements ClientModInitializer {
	public static final String MOD_ID = "tooltiptooltips";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final ModConfig CONFIG = ModConfig.createToml(FabricLoader.getInstance().getConfigDir(), "", MOD_ID, ModConfig.class);

	public static final Map<ToolMaterial, String> TOOL2TIER = new HashMap<>();

	@Override
	public void onInitializeClient() {
		// NO-OP
	}
}
