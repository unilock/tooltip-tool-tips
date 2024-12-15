package com.shnupbups.tooltiptooltips.mixin;

import com.shnupbups.tooltiptooltips.ModConfig;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MiningToolItem;
import net.minecraft.item.ToolItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

import static com.shnupbups.tooltiptooltips.TooltipToolTips.CONFIG;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    @Inject(method = "getTooltip(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/client/item/TooltipContext;)Ljava/util/List;", at = @At("RETURN"))
    private void getTooltip(PlayerEntity player, TooltipContext context, CallbackInfoReturnable<List<Text>> cir) {
        final ItemStack stack = (ItemStack) (Object) this;
        List<Text> tooltip = cir.getReturnValue();
        boolean shift = false;

        if (CONFIG.armorTools.durability.isTrue() && stack.isDamageable()) {
            shift = add(shift, CONFIG.armorTools.durability, context, tooltip, Text.translatable("tooltiptooltips.durability", stack.getMaxDamage() - stack.getDamage(), stack.getMaxDamage()).formatted(Formatting.GRAY));
        }

        if (stack.getItem() instanceof ToolItem tool) {
            ToolMaterial material = tool.getMaterial();

            if (CONFIG.armorTools.enchantability.isTrue()) {
                shift = add(shift, CONFIG.armorTools.enchantability, context, tooltip, Text.translatable("tooltiptooltips.enchantability", material.getEnchantability()).formatted(Formatting.GRAY));
            }

            if (tool instanceof MiningToolItem) {
                if (CONFIG.tools.harvestLevel.isTrue()) {
                    shift = add(shift, CONFIG.tools.harvestLevel, context, tooltip, Text.translatable("tooltiptooltips.harvest_level", material.getMiningLevel()).formatted(Formatting.GRAY));
                }

                if (CONFIG.tools.harvestSpeed.isTrue()) {
                    int efficiency = EnchantmentHelper.get(stack).getOrDefault(Enchantments.EFFICIENCY, 0);
                    int efficiencyModifier = efficiency > 0 ? (efficiency * efficiency) + 1 : 0;
                    MutableText speedText = Text.translatable("tooltiptooltips.harvest_speed", material.getMiningSpeedMultiplier() + efficiencyModifier).formatted(Formatting.GRAY);
                    if (efficiency > 0) {
                        speedText.append(Text.literal(" ").append(Text.translatable("tooltiptooltips.efficiency_modifier", efficiencyModifier).formatted(Formatting.WHITE)));
                    }
                    shift = add(shift, CONFIG.tools.harvestSpeed, context, tooltip, speedText);
                }
            }
        } else if (stack.getItem() instanceof ArmorItem armor) {
            if (CONFIG.armorTools.enchantability.isTrue()) {
                ArmorMaterial material = armor.getMaterial();
                shift = add(shift, CONFIG.armorTools.enchantability, context, tooltip, Text.translatable("tooltiptooltips.enchantability", material.getEnchantability()).formatted(Formatting.GRAY));
            }
        }

        if (stack.isFood()) {
            FoodComponent foodComponent = stack.getItem().getFoodComponent();

            if (foodComponent != null) {
                if (CONFIG.food.hunger.isTrue()) {
                    shift = add(shift, CONFIG.food.hunger, context, tooltip, Text.translatable("tooltiptooltips.hunger", foodComponent.getHunger()).formatted(Formatting.GRAY));
                }

                if (CONFIG.food.saturation.isTrue()) {
                    shift = add(shift, CONFIG.food.saturation, context, tooltip, Text.translatable("tooltiptooltips.saturation", foodComponent.getSaturationModifier()).formatted(Formatting.GRAY));
                }
            }
        }

        if (shift && !(Screen.hasShiftDown() || context.isAdvanced())) {
            tooltip.add(Text.translatable("tooltiptooltips.press_shift").formatted(Formatting.GRAY));
        }
    }

    // We do not check config.isTrue() in this method to avoid needless calculations for some tooltips.
    @Unique
    private boolean add(boolean shift, ModConfig.TriState config, TooltipContext context, List<Text> tooltip, Text line) {
        if (config == ModConfig.TriState.ALWAYS || (config == ModConfig.TriState.TRUE && (context.isAdvanced() || Screen.hasShiftDown()))) {
            tooltip.add(line);
        }
        return shift || config == ModConfig.TriState.TRUE;
    }
}
