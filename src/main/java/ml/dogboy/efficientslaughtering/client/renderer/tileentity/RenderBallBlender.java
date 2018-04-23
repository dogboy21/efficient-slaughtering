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

import ml.dogboy.efficientslaughtering.Registry;
import ml.dogboy.efficientslaughtering.tileentity.TileBallBlender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public class RenderBallBlender extends TileEntitySpecialRenderer<TileBallBlender> {

    private final RenderItem itemRenderer;
    private int baseAngle = 0;
    private float lastUpdate = 0;

    public RenderBallBlender() {
        this.itemRenderer = Minecraft.getMinecraft().getRenderItem();
    }

    @Override
    public void render(TileBallBlender te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        GlStateManager.color(1, 1, 1, 1);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit,240 / 1.0F, 240 / 1.0F);

        if (te.isMaster()) {
            List<ItemStack> items = new ArrayList<>();
            for (int i = 0; i < te.getSlots() - 1; i++) {
                ItemStack stack = te.getStackInSlot(i);
                if (!stack.isEmpty()) {
                    items.add(stack);
                }
            }

            if (te.getProgress() == -1) {
                this.baseAngle = 0;
            }

            float spacing = 360.0f / ((float) items.size());
            float angle = this.baseAngle;

            float maxIndent = te.getProgress() != -1 ? 1.0f - (1.0f * (((float) te.getProgress()) / 1200f)) : 0.0f;
            float dist = 1.0f - maxIndent * ((MathHelper.sin(getAngleRadians(this.baseAngle)) + 1) / 2.0f);

            for (int i = 0; i < items.size(); i++) {
                ItemStack stack = items.get(i);

                GlStateManager.pushMatrix();

                GlStateManager.translate(x + 0.5, y + 1, z + 0.5);
                GlStateManager.translate(dist * MathHelper.cos(getAngleRadians(angle)),
                        te.getProgress() == -1 ? 0 : MathHelper.sin(getAngleRadians(angle * 2)) / 10.0,
                        dist * MathHelper.sin(getAngleRadians(angle)));
                GlStateManager.scale(0.5F, 0.5F, 0.5F);
                RenderHelper.enableStandardItemLighting();
                this.itemRenderer.renderItem(stack, ItemCameraTransforms.TransformType.FIXED);
                RenderHelper.disableStandardItemLighting();

                GlStateManager.enableLighting();
                GlStateManager.popMatrix();

                angle += spacing;
            }

            ItemStack product = te.getStackInSlot(8);
            if (!product.isEmpty()) {
                GlStateManager.pushMatrix();

                GlStateManager.translate(x + 0.5, y + 1, z + 0.5);
                GlStateManager.scale(0.5F, 0.5F, 0.5F);
                RenderHelper.enableStandardItemLighting();
                this.itemRenderer.renderItem(product, ItemCameraTransforms.TransformType.FIXED);
                RenderHelper.disableStandardItemLighting();

                GlStateManager.enableLighting();
                GlStateManager.popMatrix();
            }

            if (Math.abs(this.lastUpdate - partialTicks) > 0.1) {
                this.baseAngle += 1;
                if (this.baseAngle >= 360) {
                    this.baseAngle -= 360;
                }
                this.lastUpdate = partialTicks;
            }


            EntityPlayerSP player = Minecraft.getMinecraft().player;
            if (player.isSneaking() && Minecraft.isGuiEnabled()) { //TODO Configurable
                Vec3d start = player.getPositionEyes(partialTicks);
                Vec3d lookVec = player.getLook(partialTicks);
                Vec3d end = start.addVector(lookVec.x * 5, lookVec.y * 5, lookVec.z * 5);

                RayTraceResult rayTraceResult = this.getWorld().rayTraceBlocks(start, end, false);
                if (rayTraceResult != null && rayTraceResult.typeOfHit == RayTraceResult.Type.BLOCK
                        && this.getWorld().getBlockState(rayTraceResult.getBlockPos()).getBlock() == Registry.BALL_BLENDER) {
                    TileEntity tileEntity = this.getWorld().getTileEntity(rayTraceResult.getBlockPos());
                    if (tileEntity instanceof TileBallBlender) {
                        if (tileEntity.getPos().equals(te.getPos()) || te.isSlave((TileBallBlender) tileEntity)) {
                            this.drawNameplate(te, te.getEnergyStored() + " / " + te.getMaxEnergyStored() + " FE", x, y, z, 32);
                        }
                    }
                }
            }
        }
    }

    private static float getAngleRadians(float degree) {
        if (degree >= 360) {
            degree -= 360;
        }

        return (float) Math.toRadians(degree);
    }

}
