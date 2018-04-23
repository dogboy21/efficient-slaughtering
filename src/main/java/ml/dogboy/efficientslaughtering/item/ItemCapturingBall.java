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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import ml.dogboy.efficientslaughtering.EfficientSlaughtering;
import ml.dogboy.efficientslaughtering.Registry;
import ml.dogboy.efficientslaughtering.api.SlaughteringRegistry;
import ml.dogboy.efficientslaughtering.client.ClientUtils;
import ml.dogboy.efficientslaughtering.entity.EntityCapturingBall;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class ItemCapturingBall extends ItemBase implements IItemColor {

    public static final int TINT_BASE = 0;
    public static final int TINT_CENTER = 1;
    public static final int TINT_MIDDLE = 2;

    public static final int PRIMARY_COLOR = 9922967;
    public static final int SECONDARY_COLOR = 6844571;

    public ItemCapturingBall() {
        super("capturing_ball");
        this.setHasSubtypes(true);
        this.setMaxStackSize(1);
    }

    public static ItemStack getForEntity(ResourceLocation entityId) {
        NBTTagCompound entityTag = new NBTTagCompound();
        entityTag.setString("id", entityId.toString());

        NBTTagCompound itemTag = new NBTTagCompound();
        itemTag.setTag("CapturedEntity", entityTag);

        ItemStack itemStack = new ItemStack(Registry.CAPTURING_BALL, 1, 0);
        itemStack.setTagCompound(itemTag);

        return itemStack;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        int meta = stack.getMetadata();
        return super.getUnlocalizedName() + "." + meta;
    }

    public static boolean hasCapturedEntity(ItemStack stack) {
        return stack.hasTagCompound() && stack.getTagCompound().hasKey("CapturedEntity");
    }

    @Nullable
    public static NBTTagCompound getEntityTag(ItemStack stack) {
        if (!ItemCapturingBall.hasCapturedEntity(stack)) {
            return null;
        }

        return stack.getTagCompound().getCompoundTag("CapturedEntity");
    }

    @Nullable
    public static NBTTagCompound stripEntityTag(ItemStack stack) {
        NBTTagCompound original = ItemCapturingBall.getEntityTag(stack);
        if (original == null) {
            return null;
        }

        if (stack.getMetadata() == 0) {
            NBTTagCompound compound = new NBTTagCompound();
            compound.setString("id", original.getString("id"));
            return compound;
        } else {
            NBTTagCompound compound = original.copy();
            ResourceLocation id = new ResourceLocation(original.getString("id"));
            EntityEntry entry = ForgeRegistries.ENTITIES.getValue(id);
            EntityLivingBase entity = (EntityLivingBase) entry.newInstance(EfficientSlaughtering.getProxy().getWorld());

            for (String tagName : SlaughteringRegistry.getStrippedTags(entity.getClass())) {
                compound.removeTag(tagName);
            }

            return compound;
        }
    }

    @Nullable
    public static ResourceLocation getCapturedEntity(ItemStack stack) {
        if (!ItemCapturingBall.hasCapturedEntity(stack)) {
            return null;
        }

        return new ResourceLocation(stack.getTagCompound().getCompoundTag("CapturedEntity").getString("id"));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        if (ItemCapturingBall.hasCapturedEntity(stack)) {
            ResourceLocation id = ItemCapturingBall.getCapturedEntity(stack);
            if (id != null) {
                String name = EntityList.getTranslationName(id);
                String customName = stack.getTagCompound().getCompoundTag("CapturedEntity").getString("CustomName");
                if (!customName.isEmpty()) {
                    name = customName + " (" + name + ")";
                }

                tooltip.add(I18n.format("tooltip.efficientslaughtering.captured_entity", name));
            }

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            JsonObject jsonObject = gson.fromJson(stripEntityTag(stack).toString(), JsonElement.class).getAsJsonObject();
            String[] lines = gson.toJson(jsonObject).split("\n");
            tooltip.addAll(Arrays.asList(lines));
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

        if (this.isInMobTab(tab)) {
            for (ResourceLocation entityId : EntityList.ENTITY_EGGS.keySet()) {
                items.add(ItemCapturingBall.getForEntity(entityId));
            }
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);

        if (ItemCapturingBall.hasCapturedEntity(stack)) {
            return new ActionResult<>(EnumActionResult.FAIL, stack);
        }

        stack.shrink(1);

        if (!worldIn.isRemote) {
            EntityCapturingBall entity = new EntityCapturingBall(worldIn, playerIn, stack.getMetadata() == 1);
            entity.shoot(playerIn, playerIn.rotationPitch, playerIn.rotationYaw, 0.0F, 1.5f, 0.3F);
            worldIn.spawnEntity(entity);
        }

        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int colorMultiplier(ItemStack stack, int tintIndex) {
        if (ItemCapturingBall.hasCapturedEntity(stack)) {
            ResourceLocation entity = ItemCapturingBall.getCapturedEntity(stack);
            if (entity != null) {
                EntityList.EntityEggInfo eggInfo = EntityList.ENTITY_EGGS.get(entity);

                if (tintIndex == TINT_BASE) {
                    return eggInfo.primaryColor;
                } else if (tintIndex == TINT_CENTER) {
                    return eggInfo.secondaryColor;
                } else if (tintIndex == TINT_MIDDLE) {
                    return ClientUtils.darken(eggInfo.primaryColor, 0.8f);
                }
            }
        }

        if (tintIndex == TINT_BASE) {
            return PRIMARY_COLOR;
        } else if (tintIndex == TINT_CENTER) {
            return SECONDARY_COLOR;
        } else if (tintIndex == TINT_MIDDLE) {
            return ClientUtils.darken(PRIMARY_COLOR, 0.8f);
        }

        return 0;
    }

    private boolean isInMobTab(CreativeTabs targetTab) {
        return EfficientSlaughtering.mobTab == targetTab;
    }

}
