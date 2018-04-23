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

import ml.dogboy.efficientslaughtering.tileentity.TileBallBlender;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import javax.annotation.Nullable;

public class BlockBallBlender extends BlockBase implements ITileEntityProvider {

    private static final PropertyBool NORTH = PropertyBool.create("north");
    private static final PropertyBool EAST = PropertyBool.create("east");
    private static final PropertyBool SOUTH = PropertyBool.create("south");
    private static final PropertyBool WEST = PropertyBool.create("west");

    protected static final AxisAlignedBB AABB_BOTTOM_HALF = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.625D, 1.0D);

    public BlockBallBlender() {
        super("ball_blender", Material.ROCK, MapColor.IRON);

        this.setDefaultState(this.blockState.getBaseState()
                .withProperty(NORTH, false)
                .withProperty(EAST, false)
                .withProperty(SOUTH, false)
                .withProperty(WEST, false));
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileBallBlender();
    }

    @Override
    public boolean doesSideBlockRendering(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing face) {
        return face == EnumFacing.DOWN;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return AABB_BOTTOM_HALF;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    private boolean shouldConnectTo(IBlockAccess world, BlockPos pos, EnumFacing facing) {
        BlockPos checkPos = pos.add(facing.getDirectionVec());

        TileEntity tileentity = world instanceof ChunkCache
                ? ((ChunkCache)world).getTileEntity(checkPos, Chunk.EnumCreateEntityType.CHECK)
                : world.getTileEntity(checkPos);
        if (!(tileentity instanceof TileBallBlender)) {
            return false;
        }

        return world.getBlockState(checkPos).getBlock() == this && ((TileBallBlender) tileentity).isMultiblockPart();
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        return state.withProperty(NORTH, shouldConnectTo(worldIn, pos, EnumFacing.NORTH))
                .withProperty(EAST,  shouldConnectTo(worldIn, pos, EnumFacing.EAST))
                .withProperty(SOUTH, shouldConnectTo(worldIn, pos, EnumFacing.SOUTH))
                .withProperty(WEST,  shouldConnectTo(worldIn, pos, EnumFacing.WEST));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return 0;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, NORTH, SOUTH, EAST, WEST);
    }

}
