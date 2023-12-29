package com.shnupbups.tooltiptooltips;

import folk.sisby.kaleido.api.WrappedConfig;
import folk.sisby.kaleido.lib.quiltconfig.api.annotations.Comment;

public class Config extends WrappedConfig {
    @Comment("Whether to always show the information provided by Tooltip Tool Tips\ninstead of requiring SHIFT to be held")
    public final Boolean always_show = false;

    @Comment("Options that apply to armor, tools, and weapons")
    public final ArmorTools armor_tools = new ArmorTools();
    public static final class ArmorTools implements Section {
        @Comment("Whether to show the item's (current/max) durability")
        public final Boolean durability = true;

        @Comment("Whether to show the item's enchantability")
        public final Boolean enchantability = true;
    }

    @Comment("Options that apply to tools")
    public final Tools tools = new Tools();
    public static final class Tools implements Section {
        @Comment("Whether to show the tool's harvest level")
        public final Boolean harvest_level = true;

        @Comment("Whether to show the tool's harvest speed")
        public final Boolean harvest_speed = true;
    }

    @Comment("Options that apply to food")
    public final Food food = new Food();
    public static final class Food implements Section {
        @Comment("Whether to show the amount of hunger the food restores")
        public final Boolean hunger = true;

        @Comment("Whether to show the amount of saturation the food restores")
        public final Boolean saturation = true;
    }
}
