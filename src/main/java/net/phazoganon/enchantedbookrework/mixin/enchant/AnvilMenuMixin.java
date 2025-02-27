package net.phazoganon.enchantedbookrework.mixin.enchant;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Iterator;

@Mixin(AnvilMenu.class)
public abstract class AnvilMenuMixin extends ItemCombinerMenu {
    @Shadow private final DataSlot cost;
    public AnvilMenuMixin(int containerId, Inventory inventory, ContainerLevelAccess access, ItemCombinerMenuSlotDefinition slotDefinition) {
        super(MenuType.ANVIL, containerId, inventory, access, slotDefinition);
        cost = DataSlot.standalone();
    }
    @ModifyExpressionValue(method = "createResult", at = @At(value = "INVOKE", target = "Ljava/lang/Math;max(II)I"))
    private int createResultMathMax(int enchantmentLevel) {
        ItemStack itemstack = this.inputSlots.getItem(0);
        ItemStack itemstack1 = itemstack.copy();
        ItemStack itemstack2 = this.inputSlots.getItem(1);
        ItemEnchantments.Mutable itemenchantments$mutable = new ItemEnchantments.Mutable(EnchantmentHelper.getEnchantmentsForCrafting(itemstack1));
        ItemEnchantments itemenchantments = EnchantmentHelper.getEnchantmentsForCrafting(itemstack2);
        Iterator var25 = itemenchantments.entrySet().iterator();
        while (var25.hasNext()) {
            Object2IntMap.Entry<Holder<Enchantment>> entry = (Object2IntMap.Entry)var25.next();
            Holder<Enchantment> holder = (Holder) entry.getKey();
            int i2 = itemenchantments$mutable.getLevel(holder);
            int j2 = entry.getIntValue();
            j2 = i2 == j2 ? j2+1 : i2+1;
            enchantmentLevel = j2;
        }
        return enchantmentLevel;
    }
}