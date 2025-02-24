package net.phazoganon.enchantedbookrework.mixin.enchant;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchantmentHelper.class)
public abstract class EnchantmentHelperMixin {
    @Inject(method = "createBook", at = @At(value = "RETURN"), cancellable = true)
    private static void createBook(EnchantmentInstance enchantmentInstance, CallbackInfoReturnable<ItemStack> cir) {
        ItemStack itemStack = new ItemStack(Items.ENCHANTED_BOOK);
        if (!EnchantmentHelper.hasAnyEnchantments(itemStack)) {
            itemStack.enchant(enchantmentInstance.enchantment, 1);
        }
        cir.setReturnValue(itemStack);
    }
}