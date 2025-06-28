package com.shnupbups.tooltiptooltips.mixin;

import com.shnupbups.tooltiptooltips.ModConfig;
import com.shnupbups.tooltiptooltips.TooltipToolTips;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;

import static com.shnupbups.tooltiptooltips.TooltipToolTips.CONFIG;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
	@Inject(method = "getTooltip(Lnet/minecraft/item/Item$TooltipContext;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/item/tooltip/TooltipType;)Ljava/util/List;", at = @At("RETURN"))
	private void getTooltip(Item.TooltipContext context, @Nullable PlayerEntity player, TooltipType type, CallbackInfoReturnable<List<Text>> cir) {
		final ItemStack stack = (ItemStack) (Object) this;
		List<Text> tooltip = cir.getReturnValue();
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
					String path = material.getInverseTag().id().getPath();
					Matcher matcher = TooltipToolTips.getMatcher(path);
					if (matcher.find()) {
						shift |= add(CONFIG.tools.harvestLevel.value(), type, tooltip, Text.translatable("tooltiptooltips.harvest_level", matcher.group(2)).formatted(Formatting.GRAY));
					} else {
						shift |= add(CONFIG.tools.harvestLevel.value(), type, tooltip, Text.translatable("tooltiptooltips.inverse_tag", path).formatted(Formatting.GRAY));
					}
				}

				if (CONFIG.tools.harvestSpeed.value().enabled()) {
					// Thanks Mojang
					int efficiency = Optional.ofNullable(context.getRegistryLookup()).flatMap(registries -> registries.getOptionalWrapper(RegistryKeys.ENCHANTMENT).flatMap(registry -> registry.getOptional(Enchantments.EFFICIENCY).map(enchantment -> stack.getEnchantments().getLevel(enchantment)))).orElse(0);
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

		if (stack.contains(DataComponentTypes.FOOD)) {
			FoodComponent foodComponent = stack.get(DataComponentTypes.FOOD);

			if (foodComponent != null) {
				if (CONFIG.food.hunger.value().enabled()) {
					shift |= add(CONFIG.food.hunger.value(), type, tooltip, Text.translatable("tooltiptooltips.hunger", foodComponent.nutrition()).formatted(Formatting.GRAY));
				}

				if (CONFIG.food.saturation.value().enabled()) {
					shift |= add(CONFIG.food.saturation.value(), type, tooltip, Text.translatable("tooltiptooltips.saturation", foodComponent.saturation()).formatted(Formatting.GRAY));
				}
			}
		}

		if (shift && !(Screen.hasShiftDown() || type.isAdvanced())) {
			tooltip.add(Text.translatable("tooltiptooltips.press_shift").formatted(Formatting.GRAY));
		}
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
