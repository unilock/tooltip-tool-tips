package com.shnupbups.tooltiptooltips;

import me.fzzyhmstrs.fzzy_config.annotations.Action;
import me.fzzyhmstrs.fzzy_config.annotations.Comment;
import me.fzzyhmstrs.fzzy_config.annotations.RequiresAction;
import me.fzzyhmstrs.fzzy_config.config.Config;
import me.fzzyhmstrs.fzzy_config.config.ConfigSection;
import net.minecraft.util.Identifier;

public class ModConfig extends Config {
	public ModConfig() {
		super(Identifier.of(TooltipToolTips.MOD_ID, "config"));
	}

	@Comment("""
	Config values can be one of the following:
	- "FALSE" (never show)
	- "TRUE" (show when holding shift)
	- "ALWAYS" (always show)
	""")
	public boolean nothing = false;

	@Comment("Options that apply to armor, tools, and weapons")
	public ArmorTools armorTools = new ArmorTools();
	public static class ArmorTools extends ConfigSection {
		@Comment("Whether to show the item's (current/max) durability")
		public TriState durability = TriState.TRUE;

		@Comment("Whether to show the item's enchantability")
		public TriState enchantability = TriState.TRUE;
	}

	@Comment("Options that apply to tools and weapons")
	public Tools tools = new Tools();
	public static class Tools extends ConfigSection {
		@Comment("Whether to show the tool's harvest level or inverse tag")
		public TriState harvestLevel = TriState.TRUE;

		@Comment("The regex pattern for converting an inverse tag to a harvest level")
		@RequiresAction(action = Action.RELOG)
		public String harvestLevelPattern = "^(incorrect_for|needs)_(.*?)_tools?$";

		@Comment("Whether to show the tool's harvest speed")
		public TriState harvestSpeed = TriState.TRUE;
	}

	@Comment("Options that apply to food")
	public Food food = new Food();
	public static class Food extends ConfigSection {
		@Comment("Whether to show the amount of hunger the food restores")
		public TriState hunger = TriState.TRUE;

		@Comment("Whether to show the amount of saturation the food restores")
		public TriState saturation = TriState.TRUE;
	}

	public enum TriState {
		FALSE,
		TRUE,
		ALWAYS;

		public boolean isTrue() {
			return this == TRUE || this == ALWAYS;
		}
	}
}
