package net.phazoganon.enchantedbookrework.event;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AnvilUpdateEvent;
import net.phazoganon.enchantedbookrework.EnchantedBookRework;

import java.util.Iterator;

@EventBusSubscriber(modid = EnchantedBookRework.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class AnvilEvent {
    @SubscribeEvent
    public static void onAnvilUpdate(AnvilUpdateEvent event) {
        ItemStack leftStack = event.getLeft();
        ItemStack leftStackCopy = leftStack.copy();
        ItemStack rightStack = event.getRight();
        if (leftStack.getItem() == Items.ENCHANTED_BOOK && rightStack.getItem() == Items.ENCHANTED_BOOK) {
            event.setCanceled(true);
        }
        ItemEnchantments.Mutable itemenchantments$mutable = new ItemEnchantments.Mutable(EnchantmentHelper.getEnchantmentsForCrafting(leftStackCopy));
        ItemEnchantments itemenchantments = EnchantmentHelper.getEnchantmentsForCrafting(rightStack);
        Iterator var25 = itemenchantments.entrySet().iterator();
        while (var25.hasNext()) {
            Object2IntMap.Entry<Holder<Enchantment>> entry = (Object2IntMap.Entry)var25.next();
            Holder<Enchantment> holder = (Holder) entry.getKey();
            int i2 = itemenchantments$mutable.getLevel(holder);
            int j2 = entry.getIntValue();
            j2 = i2 == j2 ? j2+1 : i2+1;
            Enchantment enchantment = (Enchantment) holder.value();
            if (j2 > enchantment.getMaxLevel()) {
                event.setCanceled(true);
            }
        }
    }
}
