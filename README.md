# Tooltip Tool Tips 2L
> pronounced "to͞ol"

Continuation of Shnupbups' [Tooltip Tool Tips](https://github.com/Shnupbups/tooltip-tool-tips) with a funky config and support for newer Minecraft versions. Yes it uses the same license (LGPL-3.0-only)

Features support for:
- Armor / Tools:
  - Durability (current out of max)
  - Enchantability
  - Repair Cost
- Tools
  - Harvest Level (using horrible hacks to convert from the tool's inverse tag)
    - If your tool displays its inverse tag instead of its harvest level, add a translation for it in the format of `"harvest_level.<inverseTagNamespace>.<inverseTagPath>": "<vanillaHarvestLevel>"` ([examples](src/main/resources/assets/tooltiptooltips/lang/en_us.json))
  - Harvest Speed (with extra support for Efficiency...?)
- Food
  - Nutrition
  - Saturation

All information can be made to always display, only display when holding SHIFT, or never display.
