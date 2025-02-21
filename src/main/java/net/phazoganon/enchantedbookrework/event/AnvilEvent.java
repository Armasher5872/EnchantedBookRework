package net.phazoganon.enchantedbookrework.event;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AnvilUpdateEvent;
import net.phazoganon.enchantedbookrework.EnchantedBookRework;

@EventBusSubscriber(modid = EnchantedBookRework.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class AnvilEvent {
    @SubscribeEvent
    public static void onAnvilUpdate(AnvilUpdateEvent anvilUpdateEvent) {
        ItemStack leftStack = anvilUpdateEvent.getLeft();
        ItemStack rightStack = anvilUpdateEvent.getRight();
        Item leftItem = leftStack.getItem();
        Item rightItem = rightStack.getItem();
        if (leftItem == Items.ENCHANTED_BOOK && rightItem == Items.ENCHANTED_BOOK) {
            anvilUpdateEvent.setCanceled(true);
        }
    }
}
