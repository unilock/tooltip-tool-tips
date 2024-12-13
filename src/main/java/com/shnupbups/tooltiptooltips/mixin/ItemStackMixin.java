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

        if (stack.getItem() instanceof ToolItem tool) {
            ToolMaterial material = tool.getMaterial();

            if (tool instanceof MiningToolItem) {
                if (CONFIG.tools.harvestLevel.isTrue()) {
                    add(CONFIG.tools.harvestLevel, context, tooltip, Text.translatable("tooltip.harvest_level", material.getMiningLevel()).formatted(Formatting.GRAY));
                    shift = shift || CONFIG.tools.harvestLevel == ModConfig.TriState.TRUE;
                }

                if (CONFIG.tools.harvestSpeed.isTrue()) {
                    int efficiency = EnchantmentHelper.get(stack).getOrDefault(Enchantments.EFFICIENCY, 0);
                    int efficiencyModifier = efficiency > 0 ? (efficiency * efficiency) + 1 : 0;
                    MutableText speedText = Text.translatable("tooltip.harvest_speed", material.getMiningSpeedMultiplier() + efficiencyModifier).formatted(Formatting.GRAY);
                    if (efficiency > 0) {
                        speedText.append(Text.literal(" ").append(Text.translatable("tooltip.efficiency_modifier", efficiencyModifier).formatted(Formatting.WHITE)));
                    }
                    add(CONFIG.tools.harvestSpeed, context, tooltip, speedText);
                    shift = shift || CONFIG.tools.harvestSpeed == ModConfig.TriState.TRUE;
                }
            }

            if (CONFIG.armorTools.enchantability.isTrue()) {
                add(CONFIG.armorTools.enchantability, context, tooltip, Text.translatable("tooltip.enchantability", material.getEnchantability()).formatted(Formatting.GRAY));
                shift = shift || CONFIG.armorTools.enchantability == ModConfig.TriState.TRUE;
            }
        } else if (stack.getItem() instanceof ArmorItem armor) {
            if (CONFIG.armorTools.enchantability.isTrue()) {
                ArmorMaterial material = armor.getMaterial();
                add(CONFIG.armorTools.enchantability, context, tooltip, Text.translatable("tooltip.enchantability", material.getEnchantability()).formatted(Formatting.GRAY));
                shift = shift || CONFIG.armorTools.enchantability == ModConfig.TriState.TRUE;
            }
        }

        if (CONFIG.armorTools.durability.isTrue() && stack.isDamageable()) {
            add(CONFIG.armorTools.durability, context, tooltip, Text.translatable("tooltip.durability", stack.getMaxDamage() - stack.getDamage(), stack.getMaxDamage()).formatted(Formatting.GRAY));
            shift = shift || CONFIG.armorTools.durability == ModConfig.TriState.TRUE;
        }

        if (stack.isFood()) {
            FoodComponent foodComponent = stack.getItem().getFoodComponent();

            if (foodComponent != null) {
                if (CONFIG.food.hunger.isTrue()) {
                    add(CONFIG.food.hunger, context, tooltip, Text.translatable("tooltip.hunger", foodComponent.getHunger()).formatted(Formatting.GRAY));
                    shift = shift || CONFIG.food.hunger == ModConfig.TriState.TRUE;
                }

                if (CONFIG.food.saturation.isTrue()) {
                    add(CONFIG.food.saturation, context, tooltip, Text.translatable("tooltip.saturation", foodComponent.getSaturationModifier()).formatted(Formatting.GRAY));
                    shift = shift || CONFIG.food.saturation == ModConfig.TriState.TRUE;
                }
            }
        }

        if (shift && !(Screen.hasShiftDown() || context.isAdvanced())) {
            tooltip.add(Text.translatable("tooltip.press_shift").formatted(Formatting.GRAY));
        }
    }

    @Unique
    private void add(ModConfig.TriState config, TooltipContext context, List<Text> tooltip, Text line) {
        if (config == ModConfig.TriState.ALWAYS || (config == ModConfig.TriState.TRUE && (context.isAdvanced() || Screen.hasShiftDown()))) {
            tooltip.add(line);
        }
    }
}
