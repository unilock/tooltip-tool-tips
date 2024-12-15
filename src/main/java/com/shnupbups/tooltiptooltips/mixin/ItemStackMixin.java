package com.shnupbups.tooltiptooltips.mixin;

import com.shnupbups.tooltiptooltips.ModConfig;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MiningToolItem;
import net.minecraft.item.ToolItem;
import net.minecraft.item.ToolMaterial;
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

import static com.shnupbups.tooltiptooltips.TooltipToolTips.CONFIG;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    @Inject(method = "getTooltip(Lnet/minecraft/item/Item$TooltipContext;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/item/tooltip/TooltipType;)Ljava/util/List;", at = @At("RETURN"))
    private void getTooltip(Item.TooltipContext context, @Nullable PlayerEntity player, TooltipType type, CallbackInfoReturnable<List<Text>> cir) {
        final ItemStack stack = (ItemStack) (Object) this;
        List<Text> tooltip = cir.getReturnValue();
        boolean shift = false;

        if (CONFIG.armorTools.durability.isTrue() && stack.isDamageable()) {
            shift = add(shift, CONFIG.armorTools.durability, type, tooltip, Text.translatable("tooltiptooltips.durability", stack.getMaxDamage() - stack.getDamage(), stack.getMaxDamage()).formatted(Formatting.GRAY));
        }

        if (stack.getItem() instanceof ToolItem tool) {
            ToolMaterial material = tool.getMaterial();

            if (CONFIG.armorTools.enchantability.isTrue()) {
                shift = add(shift, CONFIG.armorTools.enchantability, type, tooltip, Text.translatable("tooltiptooltips.enchantability", material.getEnchantability()).formatted(Formatting.GRAY));
            }

            if (tool instanceof MiningToolItem) {
                if (CONFIG.tools.harvestLevel.isTrue()) {
                    String path = material.getInverseTag().id().getPath();
                    // Workaround for Mythic Metals
                    boolean endsWithTool = path.endsWith("_tool");
                    if (path.startsWith("incorrect_for_") && (endsWithTool || path.endsWith("_tools"))) {
                        shift = add(shift, CONFIG.tools.harvestLevel, type, tooltip, Text.translatable("tooltiptooltips.harvest_level", path.substring(14, path.length() - (endsWithTool ? 5 : 6))).formatted(Formatting.GRAY));
                    } else {
                        shift = add(shift, CONFIG.tools.harvestLevel, type, tooltip, Text.translatable("tooltiptooltips.inverse_tag", material.getInverseTag().id().getPath()).formatted(Formatting.GRAY));
                    }
                }

                if (CONFIG.tools.harvestSpeed.isTrue()) {
                    // Thanks Mojang
                    int efficiency = Optional.ofNullable(context.getRegistryLookup()).flatMap(registries -> registries.getOptionalWrapper(RegistryKeys.ENCHANTMENT).flatMap(registry -> registry.getOptional(Enchantments.EFFICIENCY).map(enchantment -> stack.getEnchantments().getLevel(enchantment)))).orElse(0);
                    int efficiencyModifier = efficiency > 0 ? (efficiency * efficiency) + 1 : 0;
                    MutableText speedText = Text.translatable("tooltiptooltips.harvest_speed", material.getMiningSpeedMultiplier() + efficiencyModifier).formatted(Formatting.GRAY);
                    shift = add(shift, CONFIG.tools.harvestSpeed, type, tooltip, speedText);
                }
            }
        } else if (stack.getItem() instanceof ArmorItem armor) {
            if (CONFIG.armorTools.enchantability.isTrue()) {
                ArmorMaterial material = armor.getMaterial().value();
                shift = add(shift, CONFIG.armorTools.enchantability, type, tooltip, Text.translatable("tooltiptooltips.enchantability", material.enchantability()).formatted(Formatting.GRAY));
            }
        }

        if (stack.contains(DataComponentTypes.FOOD)) {
            FoodComponent foodComponent = stack.get(DataComponentTypes.FOOD);

            if (foodComponent != null) {
                if (CONFIG.food.hunger.isTrue()) {
                    shift = add(shift, CONFIG.food.hunger, type, tooltip, Text.translatable("tooltiptooltips.hunger", foodComponent.nutrition()).formatted(Formatting.GRAY));
                }

                if (CONFIG.food.saturation.isTrue()) {
                    shift = add(shift, CONFIG.food.saturation, type, tooltip, Text.translatable("tooltiptooltips.saturation", foodComponent.saturation()).formatted(Formatting.GRAY));
                }
            }
        }

        if (shift && !(Screen.hasShiftDown() || type.isAdvanced())) {
            tooltip.add(Text.translatable("tooltiptooltips.press_shift").formatted(Formatting.GRAY));
        }
    }

    // We do not check config.isTrue() in this method to avoid needless calculations for some tooltips.
    @Unique
    private boolean add(boolean shift, ModConfig.TriState config, TooltipType type, List<Text> tooltip, Text line) {
        if (config == ModConfig.TriState.ALWAYS || (config == ModConfig.TriState.TRUE && (type.isAdvanced() || Screen.hasShiftDown()))) {
            tooltip.add(line);
        }
        return shift || config == ModConfig.TriState.TRUE;
    }
}
