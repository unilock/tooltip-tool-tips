package com.shnupbups.tooltiptooltips;

import folk.sisby.kaleido.api.ReflectiveConfig;
import folk.sisby.kaleido.lib.quiltconfig.api.annotations.Comment;
import folk.sisby.kaleido.lib.quiltconfig.api.values.TrackedValue;

public class ModConfig extends ReflectiveConfig {
	@Comment(value = """
	Config values can be one of the following:
	- "FALSE" (never show)
	- "TRUE" (show when holding shift)
	- "ALWAYS" (always show)
	""")

	@Comment("Options that apply to armor, tools, and weapons")
	public final ArmorTools armorTools = new ArmorTools();
	public static final class ArmorTools extends Section {
		@Comment("Whether to show the item's (current/max) durability")
		public final TrackedValue<TriState> durability = value(TriState.TRUE);

		@Comment("Whether to show the item's enchantability")
		public final TrackedValue<TriState> enchantability = value(TriState.TRUE);

		@Comment("Whether to show the item's repair cost")
		public final TrackedValue<TriState> repairCost = value(TriState.TRUE);
	}

	@Comment("Options that apply to tools and weapons")
	public final Tools tools = new Tools();
	public static final class Tools extends Section {
		@Comment("Whether to show the tool's harvest level or inverse tag")
		public final TrackedValue<TriState> harvestLevel = value(TriState.TRUE);

		@Comment("The regex pattern for converting an inverse tag to a harvest level; matches the named capturing group \"tier\"")
		public final TrackedValue<String> harvestLevelPattern = value("^(?:incorrect_for|needs)_(?<tier>.*?)_tools?$");

		@Comment("Whether to show the tool's harvest speed")
		public final TrackedValue<TriState> harvestSpeed = value(TriState.TRUE);
	}

	@Comment("Options that apply to food")
	public final Food food = new Food();
	public static final class Food extends Section {
		@Comment("Whether to show the amount of hunger the food restores")
		public final TrackedValue<TriState> hunger = value(TriState.TRUE);

		@Comment("Whether to show the amount of saturation the food restores")
		public final TrackedValue<TriState> saturation = value(TriState.TRUE);
	}

	public enum TriState {
		FALSE,
		TRUE,
		ALWAYS;

		public boolean enabled() {
			return this == TRUE || this == ALWAYS;
		}
	}
}
