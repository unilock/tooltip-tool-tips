package com.shnupbups.tooltiptooltips.mixin;

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
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

import static com.shnupbups.tooltiptooltips.TooltipToolTips.CONFIG;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    @Inject(method = "getTooltip(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/client/item/TooltipContext;)Ljava/util/List;", at = @At("RETURN"), cancellable = true)
    private void getTooltipInject(PlayerEntity player, TooltipContext context, CallbackInfoReturnable<List<Text>> cir) {
        final ItemStack stack = (ItemStack) (Object) this;
        List<Text> texts = new ArrayList<>();
        List<Text> tooltip = cir.getReturnValue();

        if (stack.getItem() instanceof ToolItem tool) {
            ToolMaterial material = tool.getMaterial();

            if (tool instanceof MiningToolItem) {
                if (CONFIG.tools.harvest_level) {
                    texts.add(Text.translatable("tooltip.harvest_level", material.getMiningLevel()).formatted(Formatting.GRAY));
                }

                if (CONFIG.tools.harvest_speed) {
                    int efficiency = EnchantmentHelper.get(stack).getOrDefault(Enchantments.EFFICIENCY, 0);
                    int efficiencyModifier = efficiency > 0 ? (efficiency * efficiency) + 1 : 0;
                    MutableText speedText = Text.translatable("tooltip.harvest_speed", material.getMiningSpeedMultiplier() + efficiencyModifier).formatted(Formatting.GRAY);
                    if (efficiency > 0) {
                        speedText.append(Text.literal(" ").append(Text.translatable("tooltip.efficiency_modifier", efficiencyModifier).formatted(Formatting.WHITE)));
                    }
                    texts.add(speedText);
                }
            }

            if (CONFIG.armor_tools.enchantability) {
                texts.add(Text.translatable("tooltip.enchantability", material.getEnchantability()).formatted(Formatting.GRAY));
            }
        } else if (stack.getItem() instanceof ArmorItem armor) {
            if (CONFIG.armor_tools.enchantability) {
                ArmorMaterial material = armor.getMaterial();
                texts.add(Text.translatable("tooltip.enchantability", material.getEnchantability()).formatted(Formatting.GRAY));
            }
        }

        if (CONFIG.armor_tools.durability && stack.isDamageable() && (!stack.isDamaged() || !context.isAdvanced())) {
            texts.add(Text.translatable("tooltip.durability", stack.getMaxDamage() - stack.getDamage(), stack.getMaxDamage()).formatted(Formatting.GRAY));
        }

        if (stack.isFood()) {
            FoodComponent foodComponent = stack.getItem().getFoodComponent();

            if (CONFIG.food.hunger) {
                texts.add(Text.translatable("tooltip.hunger", foodComponent.getHunger()).formatted(Formatting.GRAY));
            }

            if (CONFIG.food.saturation) {
                texts.add(Text.translatable("tooltip.saturation", foodComponent.getSaturationModifier()).formatted(Formatting.GRAY));
            }
        }

        if (CONFIG.always_show || Screen.hasShiftDown() || context.isAdvanced()) {
            tooltip.addAll(texts);
        } else if (!texts.isEmpty()) {
            tooltip.add(Text.translatable("tooltip.press_shift").formatted(Formatting.GRAY));
        }

        cir.setReturnValue(tooltip);
    }
}
