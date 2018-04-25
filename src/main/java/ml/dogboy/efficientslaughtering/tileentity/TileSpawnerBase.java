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

import assets.efficientslaughtering.utilities.DirectionHelper;
import ml.dogboy.efficientslaughtering.block.BlockSpawnerBase;
import ml.dogboy.efficientslaughtering.tileentity.base.BasicInventoryCapability;
import ml.dogboy.efficientslaughtering.tileentity.base.PersistantSyncableTileEntity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nullable;

public class TileSpawnerBase extends PersistantSyncableTileEntity {

    protected BasicInventoryCapability inventory = new BasicInventoryCapability(this, 1);

    public void updateBase() {
        for (Vec3i direction : DirectionHelper.ALL_AROUND) {
            BlockPos searchPos = this.pos.add(direction);
            TileEntity tileEntity = this.world.getTileEntity(searchPos);
            if (tileEntity instanceof TileSpawner) {
                ((TileSpawner) tileEntity).notifiyBaseChange(this);
            }
        }
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (this.world.getBlockState(this.pos).getValue(BlockSpawnerBase.UPPER)) {
            return super.getCapability(capability, facing);
        }

        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return (T) this.inventory;
        }

        return super.getCapability(capability, facing);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        if (this.world.getBlockState(this.pos).getValue(BlockSpawnerBase.UPPER)) {
            return super.hasCapability(capability, facing);
        }

        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return true;
        }

        return super.hasCapability(capability, facing);
    }

    @Override
    public void writeData(NBTTagCompound tagCompound) {
        this.inventory.writeToNbt(tagCompound);
    }

    @Override
    public void readData(NBTTagCompound tagCompound) {
        this.inventory.readFromNbt(tagCompound);
    }

}
