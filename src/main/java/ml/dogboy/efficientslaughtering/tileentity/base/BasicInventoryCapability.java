/*
 * efficient-slaughtering
 * Copyright (C) 2018 Dogboy21
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ml.dogboy.efficientslaughtering.tileentity.base;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import java.util.function.Function;

public class BasicInventoryCapability implements IItemHandler, NBTSaveable {

    private final TileEntity holder;
    private final NonNullList<ItemStack> items;

    private Function<ItemStack, Boolean> allowInsert;
    private Function<ItemStack, Boolean> allowExtract;

    public BasicInventoryCapability(TileEntity holder, int size) {
        this.holder = holder;
        this.items = NonNullList.withSize(size, ItemStack.EMPTY);
    }

    @Override
    public int getSlots() {
        return this.items.size();
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int slot) {
        if (slot < 0 || slot >= this.items.size()) {
            return ItemStack.EMPTY;
        }

        return this.items.get(slot);
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        if (this.allowInsert != null && !this.allowInsert.apply(stack)) {
            return stack;
        }

        return this.insertItemInternal(slot, stack, simulate);
    }

    private ItemStack insertItemInternal(int slot, @Nonnull ItemStack stack, boolean simulate) {
        if (stack.isEmpty()) {
            return stack;
        }

        ItemStack slotStack = this.getStackInSlot(slot);
        if (slotStack.isEmpty()) {
            this.items.set(slot, stack);
            return ItemStack.EMPTY;
        }

        if (ItemHandlerHelper.canItemStacksStack(slotStack, stack)) {
            int addableAmount = Math.min(slotStack.getMaxStackSize() - slotStack.getCount(), stack.getCount());
            if (addableAmount > 0) {
                if (!simulate) {
                    slotStack.grow(addableAmount);
                    this.onContentsChanged();
                }
                stack.shrink(addableAmount);
            }
        }

        return stack;
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (amount == 0) {
            return ItemStack.EMPTY;
        }

        ItemStack stack = this.getStackInSlot(slot);
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }

        if (this.allowExtract != null && !this.allowExtract.apply(stack)) {
            return ItemStack.EMPTY;
        }

        int toExtract = Math.min(amount, stack.getCount());

        ItemStack copy = stack.copy();
        copy.setCount(toExtract);

        if (!simulate) {
            stack.shrink(toExtract);
            this.onContentsChanged();
        }

        return copy;
    }

    @Override
    public int getSlotLimit(int slot) {
        return 64;
    }

    public ItemStack insertItem(ItemStack stack) {
        for (int i = 0; i < this.getSlots() && !stack.isEmpty(); i++) {
            stack = this.insertItemInternal(i, stack, false);
        }

        return stack;
    }

    public void setInsertionFilter(Function<ItemStack, Boolean> filter) {
        this.allowInsert = filter;
    }

    public void setExtractionFilter(Function<ItemStack, Boolean> filter) {
        this.allowExtract = filter;
    }

    @Override
    public void writeToNbt(NBTTagCompound tagCompound) {
        NBTTagList itemList = new NBTTagList();

        for (int i = 0; i < this.items.size(); i++) {
            ItemStack item = this.items.get(i);
            if (item.isEmpty()) {
                continue;
            }

            NBTTagCompound itemCompound = new NBTTagCompound();
            item.writeToNBT(itemCompound);
            itemCompound.setInteger("_InvSlot", i);
            itemList.appendTag(itemCompound);
        }

        tagCompound.setTag("Items", itemList);
    }

    @Override
    public void readFromNbt(NBTTagCompound tagCompound) {
        if (!tagCompound.hasKey("Items")) {
            return;
        }

        NBTTagList itemList = tagCompound.getTagList("Items", 10);
        for (int i = 0; i < itemList.tagCount(); i++) {
            NBTTagCompound itemCompound = itemList.getCompoundTagAt(i);
            this.items.set(itemCompound.getInteger("_InvSlot"), new ItemStack(itemCompound));
        }
    }

    private void onContentsChanged() {
        this.holder.markDirty();
    }

}
