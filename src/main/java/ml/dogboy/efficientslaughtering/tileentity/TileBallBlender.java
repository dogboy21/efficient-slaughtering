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

package ml.dogboy.efficientslaughtering.tileentity;

import ml.dogboy.efficientslaughtering.Registry;
import ml.dogboy.efficientslaughtering.item.ItemCapturingBall;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class TileBallBlender extends TileEntity implements ITickable, IEnergyStorage, IItemHandler {

    private static final int MAX_ENERGY = 1000000;
    private static final int WORK_TICKS = 60 * 20;
    private static final int ENERGY_DRAW = 1000;

    private static final Vec3i[] DIRECTIONS = new Vec3i[]{
            new Vec3i(1, 0, 0),
            new Vec3i(-1, 0, 0),
            new Vec3i(0, 0, 1),
            new Vec3i(0, 0, -1),
            new Vec3i(1, 0, 1),
            new Vec3i(1, 0, -1),
            new Vec3i(-1, 0, 1),
            new Vec3i(-1, 0, -1)
    };

    private static final Vec3i[] INVALID_DIRECTIONS = new Vec3i[]{
            new Vec3i(-2, 0, -2),
            new Vec3i(-2, 0, -1),
            new Vec3i(-2, 0, 0),
            new Vec3i(-2, 0, 1),
            new Vec3i(-2, 0, 2),

            new Vec3i(2, 0, -2),
            new Vec3i(2, 0, -1),
            new Vec3i(2, 0, 0),
            new Vec3i(2, 0, 1),
            new Vec3i(2, 0, 2),

            new Vec3i(-1, 0, -2),
            new Vec3i(0, 0, -2),
            new Vec3i(1, 0, -2),

            new Vec3i(-1, 0, 2),
            new Vec3i(0, 0, 2),
            new Vec3i(1, 0, 2)
    };

    private boolean isMaster;
    private BlockPos master;


    // Master Capability Handler Stuff

    private int storedEnergy = 0;
    private ItemStack[] items = new ItemStack[9];
    private int progress = -1;

    public TileBallBlender() {
        for (int i = 0; i < this.items.length; i++) {
            this.items[i] = ItemStack.EMPTY;
        }
    }

    @Override
    public void update() {
        if (!this.isMaster && this.master == null) {
            this.tryFormMultiBlock();
        }

        if (this.isMaster) {
            if (!this.checkMultiBlockIntegrity()) {
                this.isMaster = false;
                for (TileBallBlender tileBallBlender : this.getSlaves()) {
                    if (this.pos.equals(tileBallBlender.master)) {
                        tileBallBlender.master = null;

                        tileBallBlender.world.notifyNeighborsOfStateChange(tileBallBlender.pos,
                                tileBallBlender.world.getBlockState(tileBallBlender.pos).getBlock(), false);
                        tileBallBlender.triggerUpdate();
                    }
                }
                this.triggerUpdate();
            }
        } else if (this.master != null) {
            if (!(this.world.getTileEntity(this.master) instanceof TileBallBlender)) {
                this.master = null;
                this.triggerUpdate();
            }
        }

        if (this.isMaster && !this.world.isRemote) {
            if (this.progress == -1) {
                if (this.canStartProgress()) {
                    this.progress = WORK_TICKS;
                    this.triggerUpdate();
                }
            }

            if (this.progress > 0 && this.storedEnergy >= ENERGY_DRAW) {
                this.storedEnergy -= ENERGY_DRAW;
                this.progress--;
                if (this.progress == 0) {
                    NBTTagCompound entity = this.items[0].getTagCompound().getCompoundTag("CapturedEntity");

                    for (int i = 0; i < 8; i++) {
                        this.items[i] = ItemStack.EMPTY;
                    }

                    NBTTagCompound targetTag = new NBTTagCompound();
                    targetTag.setTag("CapturedEntity", entity);
                    ItemStack targetItem = new ItemStack(Registry.SPAWNING_CORE, 1);
                    targetItem.setTagCompound(targetTag);
                    this.items[8] = targetItem;

                    this.progress = -1;
                }
                this.triggerUpdate();
            }
        }
    }

    private boolean canStartProgress() {
        if (this.progress > -1) {
            return false;
        }

        if (!this.items[8].isEmpty()) {
            return false;
        }

        boolean precise = false;
        NBTTagCompound capturedEntity = null;
        for (int i = 0; i < 8; i++) {
            ItemStack itemStack = this.items[i];
            if (itemStack.isEmpty()) {
                return false;
            }

            if (itemStack.getItem() != Registry.CAPTURING_BALL) {
                return false;
            }

            if (i == 0) {
                precise = itemStack.getMetadata() == 1;
                capturedEntity = ItemCapturingBall.stripEntityTag(itemStack);
                if (capturedEntity == null) {
                    return false;
                }
            } else {
                if (precise != (itemStack.getMetadata() == 1)) {
                    return false;
                }

                if (!capturedEntity.equals(ItemCapturingBall.stripEntityTag(itemStack))) {
                    return false;
                }
            }
        }

        return true;
    }

    public int getProgress() {
        return this.progress;
    }

    private void triggerUpdate() {
        IBlockState blockState = this.world.getBlockState(this.pos);
        this.world.notifyBlockUpdate(this.pos, blockState, blockState, 2);
        this.markDirty();
    }

    // ==================== Mutliblock Handling ====================

    private List<TileBallBlender> getSlaves() {
        List<TileBallBlender> tileEntities = new ArrayList<>();
        for (Vec3i direction : DIRECTIONS) {
            BlockPos pos = this.pos.add(direction);
            TileEntity tileEntity = this.world.getTileEntity(pos);
            if (tileEntity instanceof TileBallBlender) {
                tileEntities.add((TileBallBlender) tileEntity);
            }
        }

        return tileEntities;
    }

    private void tryFormMultiBlock() {
        for (Vec3i direction : INVALID_DIRECTIONS) {
            if (this.world.getTileEntity(this.pos.add(direction)) instanceof TileBallBlender) {
                return;
            }
        }

        List<TileBallBlender> slaves = this.getSlaves();

        if (slaves.size() == 8) {
            this.isMaster = true;
            for (TileBallBlender tileBallBlender : slaves) {
                tileBallBlender.master = this.pos;

                tileBallBlender.world.notifyNeighborsOfStateChange(tileBallBlender.pos,
                        tileBallBlender.world.getBlockState(tileBallBlender.pos).getBlock(), false);
                tileBallBlender.triggerUpdate();
            }
            this.triggerUpdate();
        }
    }

    private boolean checkMultiBlockIntegrity() {
        for (Vec3i direction : INVALID_DIRECTIONS) {
            if (this.world.getTileEntity(this.pos.add(direction)) instanceof TileBallBlender) {
                return false;
            }
        }

        return this.getSlaves().size() == 8;
    }

    public TileBallBlender getMaster() {
        if (this.master != null) {
            TileEntity tile = this.world.getTileEntity(this.master);
            if (tile instanceof TileBallBlender) {
                return (TileBallBlender) tile;
            }
        }

        return null;
    }

    public boolean isSlave(TileBallBlender tileBallBlender) {
        if (!this.isMaster) {
            return false;
        }

        for (TileBallBlender slave : this.getSlaves()) {
            if (slave.master.equals(this.pos) && slave.pos.equals(tileBallBlender.pos)) {
                return true;
            }
        }

        return false;
    }

    public boolean isMaster() {
        return this.isMaster;
    }

    public boolean isMultiblockPart() {
        return this.isMaster || this.master != null;
    }

    // ==================== Capabilities ====================

    private TileBallBlender getCapabilityHandler() {
        if (this.isMaster) {
            return this;
        }

        return this.getMaster();
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (facing == null || facing != EnumFacing.DOWN) {
            return super.getCapability(capability, facing);
        }

        if (capability == CapabilityEnergy.ENERGY) {
            TileBallBlender handler = this.getCapabilityHandler();
            if (handler != null) {
                return (T) handler;
            }
        } else if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            TileBallBlender handler = this.getCapabilityHandler();
            if (handler != null) {
                return (T) handler;
            }
        }

        return super.getCapability(capability, facing);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        if (facing == null || facing != EnumFacing.DOWN) {
            return super.hasCapability(capability, facing);
        }

        if (capability == CapabilityEnergy.ENERGY) {
            return this.getCapabilityHandler() != null;
        } else if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return this.getCapabilityHandler() != null;
        }

        return super.hasCapability(capability, facing);
    }

    // ==================== Energy Handling ====================

    @Override
    public boolean canReceive() {
        if (this.isMaster) {
            return true;
        }

        TileBallBlender master = this.getMaster();
        if (master != null) {
            return master.canReceive();
        }

        return false;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        if (this.isMaster) {
            int receiveable = Math.min(maxReceive, MAX_ENERGY - this.storedEnergy);
            if (!simulate) {
                this.storedEnergy += receiveable;
                this.triggerUpdate();
            }

            return receiveable;
        }

        TileBallBlender master = this.getMaster();
        if (master != null) {
            return master.receiveEnergy(maxReceive, simulate);
        }

        return 0;
    }

    @Override
    public boolean canExtract() {
        return false;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        return 0;
    }

    @Override
    public int getEnergyStored() {
        if (this.isMaster) {
            return this.storedEnergy;
        }

        TileBallBlender master = this.getMaster();
        if (master != null) {
            return master.getEnergyStored();
        }

        return 0;
    }

    @Override
    public int getMaxEnergyStored() {
        if (this.isMaster) {
            return MAX_ENERGY;
        }

        TileBallBlender master = this.getMaster();
        if (master != null) {
            return master.getMaxEnergyStored();
        }

        return 0;
    }

    // ==================== Item Handling ====================

    @Override
    public int getSlots() {
        if (this.isMaster) {
            return this.items.length;
        }

        TileBallBlender master = this.getMaster();
        if (master != null) {
            return master.getSlots();
        }

        return 0;
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int slot) {
        if (this.isMaster) {
            return this.items[slot];
        }

        TileBallBlender master = this.getMaster();
        if (master != null) {
            return master.getStackInSlot(slot);
        }

        return ItemStack.EMPTY;
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        if (this.isMaster) {
            if (slot >= 8 || !this.items[slot].isEmpty()) {
                return stack;
            }

            if (stack.getItem() != Registry.CAPTURING_BALL) {
                return stack;
            }

            if (this.progress > -1) {
                return stack;
            }

            if (!simulate) {
                this.items[slot] = stack;
                this.triggerUpdate();
            }

            return ItemStack.EMPTY;
        }

        TileBallBlender master = this.getMaster();
        if (master != null) {
            return master.insertItem(slot, stack, simulate);
        }

        return stack;
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (this.isMaster) {
            if (this.items[slot].isEmpty()) {
                return ItemStack.EMPTY;
            }

            if (this.progress > -1) {
                return ItemStack.EMPTY;
            }

            ItemStack stack = this.items[slot];

            if (!simulate) {
                this.items[slot] = ItemStack.EMPTY;
                this.triggerUpdate();
            }

            return stack;
        }

        TileBallBlender master = this.getMaster();
        if (master != null) {
            return master.extractItem(slot, amount, simulate);
        }

        return ItemStack.EMPTY;
    }

    @Override
    public int getSlotLimit(int slot) {
        if (this.isMaster) {
            return 1;
        }

        TileBallBlender master = this.getMaster();
        if (master != null) {
            return master.getSlotLimit(slot);
        }

        return 0;
    }

    // ==================== Persistence and Synchronization ====================


    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);

        compound.setInteger("StoredEnergy", this.storedEnergy);
        compound.setInteger("Progress", this.progress);

        NBTTagCompound itemList = new NBTTagCompound();

        for (int i = 0; i < this.items.length; i++) {
            ItemStack itemStack = this.items[i];
            if (!itemStack.isEmpty()) {
                NBTTagCompound itemCompound = new NBTTagCompound();
                itemStack.writeToNBT(itemCompound);
                itemList.setTag("Slot" + i, itemCompound);
            }
        }

        compound.setTag("Items", itemList);

        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);

        this.storedEnergy = compound.getInteger("StoredEnergy");
        this.progress = compound.getInteger("Progress");

        NBTTagCompound itemList = compound.getCompoundTag("Items");

        for (int i = 0; i < this.items.length; i++) {
            if (itemList.hasKey("Slot" + i)) {
                this.items[i] = new ItemStack(itemList.getCompoundTag("Slot" + i));
            } else {
                this.items[i] = ItemStack.EMPTY;
            }
        }
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return this.writeToNBT(new NBTTagCompound());
    }

    @Override
    public void handleUpdateTag(NBTTagCompound tag) {
        this.readFromNBT(tag);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        this.handleUpdateTag(pkt.getNbtCompound());
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(this.pos, -1, this.getUpdateTag());
    }

}
