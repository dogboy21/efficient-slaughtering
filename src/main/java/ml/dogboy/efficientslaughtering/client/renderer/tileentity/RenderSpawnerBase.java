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

package ml.dogboy.efficientslaughtering.client.renderer.tileentity;

import ml.dogboy.efficientslaughtering.tileentity.TileSpawnerBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class RenderSpawnerBase extends TileEntitySpecialRenderer<TileSpawnerBase> {

    private final RenderItem itemRenderer;

    public RenderSpawnerBase() {
        this.itemRenderer = Minecraft.getMinecraft().getRenderItem();
    }

    @Override
    public void render(TileSpawnerBase te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        GlStateManager.color(1, 1, 1, 1);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit,240 / 1.0F, 240 / 1.0F);

        IItemHandler itemHandler = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.DOWN);
        if (itemHandler == null) {
            return;
        }

        ItemStack item = itemHandler.getStackInSlot(0);
        if (!item.isEmpty()) {
            GlStateManager.pushMatrix();

            GlStateManager.translate(x + 0.5, y + 6.0 / 16.0, z + 0.5);
            GlStateManager.scale(0.5F, 0.5F, 0.5F);
            GlStateManager.rotate(180, 0, 1, 1);
            RenderHelper.enableStandardItemLighting();
            this.itemRenderer.renderItem(item, ItemCameraTransforms.TransformType.FIXED);
            RenderHelper.disableStandardItemLighting();

            GlStateManager.enableLighting();
            GlStateManager.popMatrix();
        }
    }

}
