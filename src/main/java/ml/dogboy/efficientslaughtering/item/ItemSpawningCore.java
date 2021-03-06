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

import ml.dogboy.efficientslaughtering.EfficientSlaughtering;
import ml.dogboy.efficientslaughtering.Registry;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityList;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ItemSpawningCore extends ItemBase {

    public ItemSpawningCore() {
        super("spawning_core");
        this.setMaxStackSize(1);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        if (stack.hasTagCompound()) {
            ResourceLocation entityId = new ResourceLocation(stack.getTagCompound().getString("CapturedEntity"));
            String name = EntityList.getTranslationName(entityId);
            tooltip.add(name);
        }
    }

    public static ItemStack getForEntity(ResourceLocation entityId) {
        NBTTagCompound itemTag = new NBTTagCompound();
        itemTag.setString("CapturedEntity", entityId.toString());

        ItemStack itemStack = new ItemStack(Registry.SPAWNING_CORE, 1, 0);
        itemStack.setTagCompound(itemTag);

        return itemStack;
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (EfficientSlaughtering.mobTab == tab) {
            for (ResourceLocation entityId : EntityList.ENTITY_EGGS.keySet()) {
                items.add(ItemSpawningCore.getForEntity(entityId));
            }
        }
    }
}
