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

import ml.dogboy.efficientslaughtering.entity.EntityCapturingBall;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ItemCapturingBall extends ItemBase {

    public ItemCapturingBall() {
        super("capturing_ball");
        this.setHasSubtypes(true);
        this.setMaxStackSize(1);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        int meta = stack.getMetadata();
        return super.getUnlocalizedName() + "." + meta;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("CapturedEntity")) {
            String unlocName = stack.getTagCompound().getString("CapturedEntityName");
            if (unlocName.isEmpty()) {
                unlocName = "generic";
            }

            tooltip.add(I18n.format("tooltip.efficientslaughtering.captured_entity",
                    I18n.format("entity." + unlocName + ".name")));
        } else {
            tooltip.add(I18n.format("tooltip.efficientslaughtering.no_captured_entity"));
        }
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (this.isInCreativeTab(tab)) {
            items.add(new ItemStack(this, 1, 0));
            items.add(new ItemStack(this, 1, 1));
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);

        stack.shrink(1);

        if (!worldIn.isRemote) {
            EntityCapturingBall entity = new EntityCapturingBall(worldIn, playerIn, stack.getMetadata() == 1);
            entity.shoot(playerIn, playerIn.rotationPitch, playerIn.rotationYaw, 0.0F, 2f, 0.3F);
            worldIn.spawnEntity(entity);
        }

        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }
}
