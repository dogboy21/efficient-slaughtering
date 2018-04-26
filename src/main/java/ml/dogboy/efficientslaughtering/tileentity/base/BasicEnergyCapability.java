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

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.energy.EnergyStorage;

public class BasicEnergyCapability extends EnergyStorage implements NBTSaveable {

    private final TileEntity holder;

    public BasicEnergyCapability(TileEntity holder, int capacity, int maxReceive, int maxExtract) {
        super(capacity, maxReceive, maxExtract);
        this.holder = holder;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        int received = super.receiveEnergy(maxReceive, simulate);
        if (received > 0 && !simulate) {
            this.onContentsChanged();
        }
        return received;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        int extracted = super.extractEnergy(maxExtract, simulate);
        if (extracted > 0 && !simulate) {
            this.onContentsChanged();
        }
        return extracted;
    }

    public void drain(int amount) {
        this.energy = Math.max(0, this.energy - amount);
    }

    @Override
    public void writeToNbt(NBTTagCompound tagCompound) {
        tagCompound.setInteger("EnergyStored", this.energy);
    }

    @Override
    public void readFromNbt(NBTTagCompound tagCompound) {
        this.energy = tagCompound.getInteger("EnergyStored");
    }

    public void onContentsChanged() {
        this.holder.markDirty();
        IBlockState blockState = this.holder.getWorld().getBlockState(this.holder.getPos());
        this.holder.getWorld().notifyBlockUpdate(this.holder.getPos(), blockState, blockState, 2);
    }

}
