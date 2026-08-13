package cc.unilock.tooltiptooltips.mixin;

import cc.unilock.tooltiptooltips.ModConfig;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MiningToolItem;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ToolItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static cc.unilock.tooltiptooltips.TooltipToolTips.CONFIG;
import static cc.unilock.tooltiptooltips.TooltipToolTips.LOGGER;
import static cc.unilock.tooltiptooltips.TooltipToolTips.TOOL2TIER;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
	@ModifyReturnValue(method = "getTooltip(Lnet/minecraft/item/Item$TooltipContext;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/item/tooltip/TooltipType;)Ljava/util/List;", at = @At("RETURN"))
	private List<Text> getTooltip(List<Text> tooltip, Item.TooltipContext context, @Nullable PlayerEntity player, TooltipType type) {
		final ItemStack stack = (ItemStack) (Object) this;
		boolean shift = false;

		if (CONFIG.armorTools.durability.value().enabled() && stack.isDamageable()) {
			shift |= add(CONFIG.armorTools.durability.value(), type, tooltip, Text.translatable("tooltiptooltips.durability", stack.getMaxDamage() - stack.getDamage(), stack.getMaxDamage()).formatted(Formatting.GRAY));
		}

		if (stack.getItem() instanceof ToolItem tool) {
			ToolMaterial material = tool.getMaterial();

			if (CONFIG.armorTools.enchantability.value().enabled()) {
				shift |= add(CONFIG.armorTools.enchantability.value(), type, tooltip, Text.translatable("tooltiptooltips.enchantability", material.getEnchantability()).formatted(Formatting.GRAY));
			}

			if (CONFIG.armorTools.repairCost.value().enabled() && stack.contains(DataComponentTypes.REPAIR_COST)) {
				shift |= add(CONFIG.armorTools.repairCost.value(), type, tooltip, Text.translatable("tooltiptooltips.repair_cost", stack.get(DataComponentTypes.REPAIR_COST)).formatted(Formatting.GRAY));
			}

			if (tool instanceof MiningToolItem) {
				if (CONFIG.tools.harvestLevel.value().enabled()) {
					if (tool instanceof PickaxeItem) {
						String tier = TOOL2TIER.computeIfAbsent(material, m -> {
							for (Map.Entry<String, String> entry : CONFIG.tools.harvestLevelBlocks.value()) {
								var blockId = Identifier.tryParse(entry.getValue());
								if (blockId != null) {
									var block = Registries.BLOCK.get(blockId);
									if (!block.getRegistryEntry().isIn(m.getInverseTag())) {
										return entry.getKey();
									}
								}
							}

							LOGGER.error("Failed to compute harvest level for ToolMaterial with inverse tag #{}", m.getInverseTag().id().toString());
							return "[error]";
						});

						shift |= add(CONFIG.tools.harvestLevel.value(), type, tooltip, Text.translatable("tooltiptooltips.harvest_level", tier).formatted(Formatting.GRAY));
					}
//					else
//					{
//						TagKey<Block> inverseTag = material.getInverseTag();
//
//						if (inverseTag != null) {
//							Identifier id = inverseTag.id();
//							String key = id.toTranslationKey("harvest_level");
//
//							if (I18n.hasTranslation(key)) {
//								shift |= add(CONFIG.tools.harvestLevel.value(), type, tooltip, Text.translatable("tooltiptooltips.harvest_level", I18n.translate(key)).formatted(Formatting.GRAY));
//							} else {
//								// TODO: this only works on singleplayer...
//								String tier = HarvestLevelManager.getTier(id);
//								if (tier != null) {
//									shift |= add(CONFIG.tools.harvestLevel.value(), type, tooltip, Text.translatable("tooltiptooltips.harvest_level", tier).formatted(Formatting.GRAY));
//								} else {
//									shift |= add(CONFIG.tools.harvestLevel.value(), type, tooltip, Text.translatable("tooltiptooltips.inverse_tag", id.toString()).formatted(Formatting.GRAY));
//								}
//							}
//						}
//					}
				}

				if (CONFIG.tools.harvestSpeed.value().enabled()) {
					MutableText speedText = Text.translatable("tooltiptooltips.harvest_speed", material.getMiningSpeedMultiplier());

					// Thanks Mojang
					int efficiency = Optional.ofNullable(context.getRegistryLookup()).flatMap(registries -> registries.getOptionalWrapper(RegistryKeys.ENCHANTMENT).flatMap(registry -> registry.getOptional(Enchantments.EFFICIENCY).map(enchantment -> EnchantmentHelper.getEnchantments(stack).getLevel(enchantment)))).orElse(0);
					if (efficiency > 0) {
						speedText.append(Text.literal(" ")).append(Text.translatable("tooltiptooltips.efficiency_modifier", efficiency * efficiency + 1));
					}

					shift |= add(CONFIG.tools.harvestSpeed.value(), type, tooltip, speedText.formatted(Formatting.GRAY));
				}
			}
		} else if (stack.getItem() instanceof ArmorItem armor) {
			if (CONFIG.armorTools.enchantability.value().enabled()) {
				ArmorMaterial material = armor.getMaterial().value();
				shift |= add(CONFIG.armorTools.enchantability.value(), type, tooltip, Text.translatable("tooltiptooltips.enchantability", material.enchantability()).formatted(Formatting.GRAY));
			}
		}

		if (stack.get(DataComponentTypes.FOOD) instanceof FoodComponent foodComponent) {
			if (CONFIG.food.nutrition.value().enabled()) {
				shift |= add(CONFIG.food.nutrition.value(), type, tooltip, Text.translatable("tooltiptooltips.nutrition", foodComponent.nutrition()).formatted(Formatting.GRAY));
			}

			if (CONFIG.food.saturation.value().enabled()) {
				shift |= add(CONFIG.food.saturation.value(), type, tooltip, Text.translatable("tooltiptooltips.saturation", foodComponent.saturation()).formatted(Formatting.GRAY));
			}
		}

		if (shift && !(Screen.hasShiftDown() || type.isAdvanced())) {
			tooltip.add(Text.translatable("tooltiptooltips.press_shift").formatted(Formatting.GRAY));
		}
		return tooltip;
	}

	// We do not check config.isTrue() in this method to avoid needless calculations for some tooltips.
	@Unique
	private boolean add(ModConfig.TriState config, TooltipType type, List<Text> tooltip, Text line) {
		if (config == ModConfig.TriState.ALWAYS || (config == ModConfig.TriState.TRUE && (type.isAdvanced() || Screen.hasShiftDown()))) {
			tooltip.add(line);
		}
		return config == ModConfig.TriState.TRUE;
	}
}
