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
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

import javax.annotation.Nullable;

public class BasicFluidCapability extends FluidTank implements NBTSaveable {

    private final TileEntity holder;
    private final Fluid fixedFluid;

    public BasicFluidCapability(TileEntity holder, Fluid fluid, int capacity) {
        super(fluid, 0, capacity);
        this.holder = holder;
        this.fixedFluid = fluid;
    }

    @Override
    public boolean canFillFluidType(FluidStack fluid) {
        return fluid != null && this.canFill && fluid.getFluid() == this.fixedFluid;
    }

    @Override
    public boolean canDrainFluidType(@Nullable FluidStack fluid) {
        return fluid != null && this.canDrain && fluid.getFluid() == this.fixedFluid;
    }

    @Override
    public void writeToNbt(NBTTagCompound tagCompound) {
        NBTTagCompound fluidTag = new NBTTagCompound();
        this.writeToNBT(fluidTag);
        tagCompound.setTag("Tank", fluidTag);
    }

    @Override
    public void readFromNbt(NBTTagCompound tagCompound) {
        if (tagCompound.hasKey("Tank")) {
            NBTTagCompound fluidTag = tagCompound.getCompoundTag("Tank");
            this.readFromNBT(fluidTag);
        }
    }

    @Override
    protected void onContentsChanged() {
        this.holder.markDirty();
        IBlockState blockState = this.holder.getWorld().getBlockState(this.holder.getPos());
        this.holder.getWorld().notifyBlockUpdate(this.holder.getPos(), blockState, blockState, 2);
    }

}
