package cc.unilock.tooltiptooltips.mixin;

import cc.unilock.tooltiptooltips.HarvestLevelManager;
import cc.unilock.tooltiptooltips.ModConfig;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.block.Block;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;
import java.util.Optional;

import static cc.unilock.tooltiptooltips.TooltipToolTips.CONFIG;

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
					TagKey<Block> inverseTag = material.getInverseTag();
					if (inverseTag != null) {
						Identifier id = inverseTag.id();
						String key = id.toTranslationKey("harvest_level");
						if (I18n.hasTranslation(key)) {
							shift |= add(CONFIG.tools.harvestLevel.value(), type, tooltip, Text.translatable("tooltiptooltips.harvest_level", I18n.translate(key)).formatted(Formatting.GRAY));
						} else {
							String tier = HarvestLevelManager.getTier(id);
							if (tier != null) {
								shift |= add(CONFIG.tools.harvestLevel.value(), type, tooltip, Text.translatable("tooltiptooltips.harvest_level", tier).formatted(Formatting.GRAY));
							} else {
								shift |= add(CONFIG.tools.harvestLevel.value(), type, tooltip, Text.translatable("tooltiptooltips.inverse_tag", id.toString()).formatted(Formatting.GRAY));
							}
						}
					}
				}

				if (CONFIG.tools.harvestSpeed.value().enabled()) {
					// Thanks Mojang
					int efficiency = Optional.ofNullable(context.getRegistryLookup()).flatMap(registries -> registries.getOptionalWrapper(RegistryKeys.ENCHANTMENT).flatMap(registry -> registry.getOptional(Enchantments.EFFICIENCY).map(enchantment -> EnchantmentHelper.getEnchantments(stack).getLevel(enchantment)))).orElse(0);
					int efficiencyModifier = efficiency > 0 ? (efficiency * efficiency) + 1 : 0;
					MutableText speedText = Text.translatable("tooltiptooltips.harvest_speed", material.getMiningSpeedMultiplier() + efficiencyModifier).formatted(Formatting.GRAY);
					shift |= add(CONFIG.tools.harvestSpeed.value(), type, tooltip, speedText);
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
