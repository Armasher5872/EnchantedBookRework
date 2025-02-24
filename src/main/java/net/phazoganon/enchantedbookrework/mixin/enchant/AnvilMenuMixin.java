package net.phazoganon.enchantedbookrework.mixin.enchant;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.StringUtil;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Iterator;

import static net.minecraft.world.inventory.AnvilMenu.calculateIncreasedRepairCost;

//Unideal approach to achieving what I want
@Mixin(AnvilMenu.class)
public abstract class AnvilMenuMixin extends ItemCombinerMenu {
    @Shadow private String itemName;
    @Shadow public int repairItemCountCost;
    @Shadow private final DataSlot cost;
    @Shadow private boolean onlyRenaming;
    public AnvilMenuMixin(int containerId, Inventory inventory, ContainerLevelAccess access, ItemCombinerMenuSlotDefinition slotDefinition) {
        super(MenuType.ANVIL, containerId, inventory, access, slotDefinition);
        cost = DataSlot.standalone();
    }
    @Inject(method = "createResult", at = @At(value = "HEAD"), cancellable = true)
    private void createResult(@NotNull CallbackInfo ci) {
        ci.cancel();
        ItemStack itemstack = this.inputSlots.getItem(0);
        ItemStack itemstack2 = this.inputSlots.getItem(1);
        this.onlyRenaming = false;
        this.cost.set(1);
        int i = 0;
        long j = 0L;
        int k = 0;
        if (!itemstack.isEmpty() && EnchantmentHelper.canStoreEnchantments(itemstack)) {
            if (itemstack.getItem() == Items.ENCHANTED_BOOK && itemstack2.getItem() == Items.ENCHANTED_BOOK) {
                return;
            }
            ItemStack itemstack1 = itemstack.copy();
            ItemEnchantments.Mutable itemenchantments$mutable = new ItemEnchantments.Mutable(EnchantmentHelper.getEnchantmentsForCrafting(itemstack1));
            j += (long) itemstack.getOrDefault(DataComponents.REPAIR_COST, 0) + (long) itemstack2.getOrDefault(DataComponents.REPAIR_COST, 0);
            this.repairItemCountCost = 0;
            boolean flag = false;
            int k2;
            int i3;
            if (!itemstack2.isEmpty()) {
                flag = itemstack2.has(DataComponents.STORED_ENCHANTMENTS);
                int j1;
                if (itemstack1.isDamageableItem() && itemstack.isValidRepairItem(itemstack2)) {
                    k2 = Math.min(itemstack1.getDamageValue(), itemstack1.getMaxDamage()/4);
                    if (k2 <= 0) {
                        this.resultSlots.setItem(0, ItemStack.EMPTY);
                        this.cost.set(0);
                        return;
                    }
                    for(i3 = 0; k2 > 0 && i3 < itemstack2.getCount(); ++i3) {
                        j1 = itemstack1.getDamageValue()-k2;
                        itemstack1.setDamageValue(j1);
                        ++i;
                        k2 = Math.min(itemstack1.getDamageValue(), itemstack1.getMaxDamage()/4);
                    }
                    this.repairItemCountCost = i3;
                }
                else {
                    if (!flag && (!itemstack1.is(itemstack2.getItem()) || !itemstack1.isDamageableItem())) {
                        this.resultSlots.setItem(0, ItemStack.EMPTY);
                        this.cost.set(0);
                        return;
                    }
                    if (itemstack1.isDamageableItem() && !flag) {
                        k2 = itemstack.getMaxDamage()-itemstack.getDamageValue();
                        i3 = itemstack2.getMaxDamage()-itemstack2.getDamageValue();
                        j1 = i3+itemstack1.getMaxDamage()*12/100;
                        int k1 = k2+j1;
                        int l1 = itemstack1.getMaxDamage()-k1;
                        if (l1 < 0) {
                            l1 = 0;
                        }
                        if (l1 < itemstack1.getDamageValue()) {
                            itemstack1.setDamageValue(l1);
                            i += 2;
                        }
                    }
                    ItemEnchantments itemenchantments = EnchantmentHelper.getEnchantmentsForCrafting(itemstack2);
                    boolean flag2 = false;
                    boolean flag3 = false;
                    Iterator var25 = itemenchantments.entrySet().iterator();
                    while (var25.hasNext()) {
                        Object2IntMap.Entry<Holder<Enchantment>> entry = (Object2IntMap.Entry)var25.next();
                        Holder<Enchantment> holder = (Holder) entry.getKey();
                        int i2 = itemenchantments$mutable.getLevel(holder);
                        int j2 = entry.getIntValue();
                        j2 = i2 == j2 ? j2+1 : i2+1;
                        Enchantment enchantment = (Enchantment) holder.value();
                        boolean flag1 = itemstack.supportsEnchantment(holder);
                        if (this.player.getAbilities().instabuild) {
                            flag1 = true;
                        }
                        Iterator var20 = itemenchantments$mutable.keySet().iterator();
                        while(var20.hasNext()) {
                            Holder<Enchantment> holder1 = (Holder)var20.next();
                            if (!holder1.equals(holder) && !Enchantment.areCompatible(holder, holder1)) {
                                flag1 = false;
                                ++i;
                            }
                        }
                        if (!flag1) {
                            flag3 = true;
                        }
                        else {
                            flag2 = true;
                            if (j2 > enchantment.getMaxLevel()) {
                                j2 = enchantment.getMaxLevel();
                            }
                            itemenchantments$mutable.set(holder, j2);
                            int l3 = enchantment.getAnvilCost();
                            if (flag) {
                                l3 = Math.max(1, l3 / 2);
                            }
                            i += l3 * j2;
                            if (itemstack.getCount() > 1) {
                                i = 40;
                            }
                        }
                    }
                    if (flag3 && !flag2) {
                        this.resultSlots.setItem(0, ItemStack.EMPTY);
                        this.cost.set(0);
                        return;
                    }
                }
            }
            if (this.itemName != null && !StringUtil.isBlank(this.itemName)) {
                if (!this.itemName.equals(itemstack.getHoverName().getString())) {
                    k = 1;
                    i += k;
                    itemstack1.set(DataComponents.CUSTOM_NAME, Component.literal(this.itemName));
                }
            }
            else if (itemstack.has(DataComponents.CUSTOM_NAME)) {
                k = 1;
                i += k;
                itemstack1.remove(DataComponents.CUSTOM_NAME);
            }
            if (flag && !itemstack1.isBookEnchantable(itemstack2)) {
                itemstack1 = ItemStack.EMPTY;
            }
            k2 = i <= 0 ? 0 : (int) Mth.clamp(j + (long) i, 0L, 2147483647L);
            this.cost.set(k2);
            if (i <= 0) {
                itemstack1 = ItemStack.EMPTY;
            }
            if (k == i && k > 0) {
                if (this.cost.get() >= 40) {
                    this.cost.set(39);
                }
                this.onlyRenaming = true;
            }
            if (this.cost.get() >= 40 && !this.player.getAbilities().instabuild) {
                itemstack1 = ItemStack.EMPTY;
            }
            if (!itemstack1.isEmpty()) {
                i3 = itemstack1.getOrDefault(DataComponents.REPAIR_COST, 0);
                if (i3 < itemstack2.getOrDefault(DataComponents.REPAIR_COST, 0)) {
                    i3 = itemstack2.getOrDefault(DataComponents.REPAIR_COST, 0);
                }
                if (k != i || k == 0) {
                    i3 = calculateIncreasedRepairCost(i3);
                }
                itemstack1.set(DataComponents.REPAIR_COST, i3);
                EnchantmentHelper.setEnchantments(itemstack1, itemenchantments$mutable.toImmutable());
            }
            this.resultSlots.setItem(0, itemstack1);
            this.broadcastChanges();
        }
        else {
            this.resultSlots.setItem(0, ItemStack.EMPTY);
            this.cost.set(0);
        }
    }
}