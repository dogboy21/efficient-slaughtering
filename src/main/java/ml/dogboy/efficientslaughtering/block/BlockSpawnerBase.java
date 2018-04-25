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

package ml.dogboy.efficientslaughtering.block;

import ml.dogboy.efficientslaughtering.tileentity.TileSpawnerBase;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;

public class BlockSpawnerBase extends BlockBase implements ITileEntityProvider {

    private static final AxisAlignedBB AABB_BOTTOM_HALF = new AxisAlignedBB(0, 0, 0, 1, 5.0d / 16.0d, 1);
    private static final AxisAlignedBB AABB_UPPER_HALF = new AxisAlignedBB(0, 11.0d / 16.0d, 0, 1, 1, 1);

    public static final PropertyBool UPPER = PropertyBool.create("upper");

    public BlockSpawnerBase() {
        super("spawner_base", Material.ROCK, MapColor.IRON);

        this.setDefaultState(this.blockState.getBaseState()
                .withProperty(UPPER, false));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, UPPER);
    }

    @Override
    public boolean doesSideBlockRendering(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing face) {
        return face == EnumFacing.DOWN;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return state.getValue(UPPER) ? AABB_UPPER_HALF : AABB_BOTTOM_HALF;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(UPPER) ? 1 : 0;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return meta == 0
                ? this.getDefaultState().withProperty(UPPER, false)
                : this.getDefaultState().withProperty(UPPER, true);
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        return facing == EnumFacing.DOWN
                ? this.getDefaultState().withProperty(UPPER, true)
                : this.getDefaultState().withProperty(UPPER, false);
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileSpawnerBase();
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (state.getValue(UPPER)) {
            return false;
        }

        TileSpawnerBase tileSpawnerBase = (TileSpawnerBase) worldIn.getTileEntity(pos);
        IItemHandler itemHandler = tileSpawnerBase.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.DOWN);
        if (itemHandler == null) {
            return false;
        }

        if (itemHandler.getStackInSlot(0).isEmpty()) {
            ItemStack handItem = playerIn.getHeldItem(hand);
            if (!handItem.isEmpty()) {
                playerIn.setHeldItem(hand, itemHandler.insertItem(0, handItem, false));
                tileSpawnerBase.updateBase();
                return true;
            }
        } else {
            ItemStack itemStack = itemHandler.extractItem(0, 1, false);
            if (!playerIn.inventory.addItemStackToInventory(itemStack)) {
                playerIn.dropItem(itemStack, false);
            }
            tileSpawnerBase.updateBase();
            return true;
        }

        return false;
    }
}
