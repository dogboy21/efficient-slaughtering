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

package ml.dogboy.efficientslaughtering.item;

import ml.dogboy.efficientslaughtering.tileentity.TileSpawner;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

public class ItemAnalyzer extends ItemBase {

    public ItemAnalyzer() {
        super("analyzer");
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (worldIn.isRemote) {
            return EnumActionResult.PASS;
        }

        TileEntity tileEntity = worldIn.getTileEntity(pos);
        if (tileEntity instanceof TileSpawner) {
            if (!((TileSpawner) tileEntity).isFormed()) {
                player.sendMessage(new TextComponentString("This multiblock is not complete"));
                return EnumActionResult.FAIL;
            }

            TileSpawner spawner = (TileSpawner) tileEntity;

            ITextComponent infoText = new TextComponentString("");
            infoText.appendSibling(new TextComponentString("Spawner Multiblock\n\n").setStyle(new Style().setUnderlined(true)));

            infoText.appendSibling(new TextComponentString("Work Ticks: " + spawner.getSpawnerStats().getWorkTicks() + "\n\n"));
            infoText.appendSibling(new TextComponentString("Base Energy Draw: " + spawner.getSpawnerStats().getBaseEnergyDraw() + " FE/Mob\n"));

            if (spawner.getSpawnerStats().getAdditionalBossEnergyDraw() > 0) {
                infoText.appendSibling(new TextComponentString("Additional Boss Draw: " + spawner.getSpawnerStats().getAdditionalBossEnergyDraw() + " FE/Mob\n"));
            }
            if (spawner.getSpawnerStats().getSpeedEnergyDraw() > 0) {
                infoText.appendSibling(new TextComponentString("Speed Upgrade Cost: " + spawner.getSpawnerStats().getSpeedEnergyDraw() + " FE/Mob\n"));
            }
            if (spawner.getSpawnerStats().getLootingEnergyDraw() > 0) {
                infoText.appendSibling(new TextComponentString("Looting Upgrade Cost: " + spawner.getSpawnerStats().getLootingEnergyDraw() + " FE/Mob\n"));
            }
            if (spawner.getSpawnerStats().getBeheadingEnergyDraw() > 0) {
                infoText.appendSibling(new TextComponentString("Beheading Upgrade Cost: " + spawner.getSpawnerStats().getBeheadingEnergyDraw() + " FE/Mob\n"));
            }
            if (spawner.getSpawnerStats().getFakePlayerEnergyDraw() > 0) {
                infoText.appendSibling(new TextComponentString("Fake Player Upgrade Cost: " + spawner.getSpawnerStats().getFakePlayerEnergyDraw() + " FE/Mob\n"));
            }
            infoText.appendSibling(new TextComponentString("\nTotal Energy Draw: " + spawner.getSpawnerStats().getTotalEnergyDraw() + " FE/Mob"));

            player.sendMessage(infoText);

            return EnumActionResult.SUCCESS;
        }

        return EnumActionResult.PASS;
    }
}
