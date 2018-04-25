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

import ml.dogboy.efficientslaughtering.Reference;
import ml.dogboy.efficientslaughtering.tileentity.TileSpawner;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class RenderSpawner extends TileEntitySpecialRenderer<TileSpawner> {

    private static final ResourceLocation TEXTURE_BEAM = new ResourceLocation(Reference.MODID, "textures/blocks/spawner_beam.png");

    @Override
    public void render(TileSpawner te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        GlStateManager.color(1, 1, 1, 1);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit,240 / 1.0F, 240 / 1.0F);

        if (te.isLower()) {
            double height = te.getUpper().getPos().getY() - te.getPos().getY() + 1;

            GlStateManager.pushMatrix();

            if (te.getEntityToSpawn() != null) {
                double scaleFactor = Math.min(1d / te.getEntityToSpawn().width, (height - 1.5) / te.getEntityToSpawn().height);
                RenderSpawner.renderEntity(te.getEntityToSpawn(), x, y, z, height, scaleFactor, 0);
            }

            GlStateManager.popMatrix();

            GlStateManager.pushMatrix();
            GlStateManager.alphaFunc(516, 0.1F);
            this.bindTexture(TEXTURE_BEAM);

            GlStateManager.disableFog();
            GlStateManager.enableBlend();

            RenderSpawner.renderBeamSegment(x, y, z, partialTicks, height, 0.1875d);

            GlStateManager.disableBlend();
            GlStateManager.enableFog();
            GlStateManager.popMatrix();
        }
    }

    // Derived from TileEntityBeaconRenderer
    private static void renderBeamSegment(double x, double y, double z, double partialTicks, double height, double width) {
        GlStateManager.glTexParameteri(3553, 10242, 10497);
        GlStateManager.glTexParameteri(3553, 10243, 10497);
        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        GlStateManager.depthMask(true);
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        double d1 = height < 0 ? partialTicks : -partialTicks;
        double d2 = MathHelper.frac(d1 * 0.2D - (double)MathHelper.floor(d1 * 0.1D));
        double d3 = partialTicks * 0.025D * -1.5D;
        double d4 = 0.5D + Math.cos(d3 + 2.356194490192345D) * width;
        double d5 = 0.5D + Math.sin(d3 + 2.356194490192345D) * width;
        double d6 = 0.5D + Math.cos(d3 + (Math.PI / 4D)) * width;
        double d7 = 0.5D + Math.sin(d3 + (Math.PI / 4D)) * width;
        double d8    = 0.5D + Math.cos(d3 + 3.9269908169872414D) * width;
        double d9 = 0.5D + Math.sin(d3 + 3.9269908169872414D) * width;
        double d10 = 0.5D + Math.cos(d3 + 5.497787143782138D) * width;
        double d11 = 0.5D + Math.sin(d3 + 5.497787143782138D) * width;
        double d14 = -1.0D + d2;
        double d15 = height * 1 * (0.5D / width) + d14;
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        bufferbuilder.pos(x + d4, y + height, z + d5).tex(1.0D, d15).color(1, 1, 1, 1.0F).endVertex();
        bufferbuilder.pos(x + d4, y, z + d5).tex(1.0D, d14).color(1, 1, 1, 1.0F).endVertex();
        bufferbuilder.pos(x + d6, y, z + d7).tex(0.0D, d14).color(1, 1, 1, 1.0F).endVertex();
        bufferbuilder.pos(x + d6, y + height, z + d7).tex(0.0D, d15).color(1, 1, 1, 1.0F).endVertex();
        bufferbuilder.pos(x + d10, y + height, z + d11).tex(1.0D, d15).color(1, 1, 1, 1.0F).endVertex();
        bufferbuilder.pos(x + d10, y, z + d11).tex(1.0D, d14).color(1, 1, 1, 1.0F).endVertex();
        bufferbuilder.pos(x + d8, y, z + d9).tex(0.0D, d14).color(1, 1, 1, 1.0F).endVertex();
        bufferbuilder.pos(x + d8, y + height, z + d9).tex(0.0D, d15).color(1, 1, 1, 1.0F).endVertex();
        bufferbuilder.pos(x + d6, y + height, z + d7).tex(1.0D, d15).color(1, 1, 1, 1.0F).endVertex();
        bufferbuilder.pos(x + d6, y, z + d7).tex(1.0D, d14).color(1, 1, 1, 1.0F).endVertex();
        bufferbuilder.pos(x + d10, y, z + d11).tex(0.0D, d14).color(1, 1, 1, 1.0F).endVertex();
        bufferbuilder.pos(x + d10, y + height, z + d11).tex(0.0D, d15).color(1, 1, 1, 1.0F).endVertex();
        bufferbuilder.pos(x + d8, y + height, z + d9).tex(1.0D, d15).color(1, 1, 1, 1.0F).endVertex();
        bufferbuilder.pos(x + d8, y, z + d9).tex(1.0D, d14).color(1, 1, 1, 1.0F).endVertex();
        bufferbuilder.pos(x + d4, y, z + d5).tex(0.0D, d14).color(1, 1, 1, 1.0F).endVertex();
        bufferbuilder.pos(x + d4, y + height, z + d5).tex(0.0D, d15).color(1, 1, 1, 1.0F).endVertex();
        tessellator.draw();
        GlStateManager.enableLighting();
        GlStateManager.enableTexture2D();
        GlStateManager.depthMask(true);
    }

    private static <T extends Entity> void renderEntity(T entity, double x, double y, double z, double height, double scale, float partialTicks) {
        Render<T> entityRender = Minecraft.getMinecraft().getRenderManager().getEntityRenderObject(entity);
        double entHeight = entity.height * scale;
        double posY = (height - entHeight) / 2;
        posY += (MathHelper.sin((entity.getEntityWorld().getTotalWorldTime() % 360) / 10.0f) / 4.0f);

        GlStateManager.translate(x + 0.5d, y + posY, z + 0.5d);
        GlStateManager.scale(scale, scale, scale);
        entityRender.doRender(entity, 0, 0, 0, 0, partialTicks);
    }

    public boolean isGlobalRenderer(TileSpawner te) {
        return true;
    }

}
