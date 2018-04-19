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

package ml.dogboy.efficientslaughtering.client.renderer;

import ml.dogboy.efficientslaughtering.Reference;
import ml.dogboy.efficientslaughtering.client.ClientUtils;
import ml.dogboy.efficientslaughtering.client.models.ModelCapturingBall;
import ml.dogboy.efficientslaughtering.entity.EntityCapturingBall;
import ml.dogboy.efficientslaughtering.item.ItemCapturingBall;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public class RenderCapturingBall extends Render<EntityCapturingBall> {

    private static final ModelCapturingBall MODEL = new ModelCapturingBall();
    private static final ResourceLocation TEXTURE = new ResourceLocation(Reference.MODID,
            "textures/entity/capturing_ball.png");

    public RenderCapturingBall(RenderManager renderManager) {
        super(renderManager);
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(EntityCapturingBall entity) {
        return TEXTURE;
    }

    @Override
    public void doRender(EntityCapturingBall entity, double x, double y, double z, float entityYaw, float partialTicks) {
        this.bindEntityTexture(entity);

        GlStateManager.pushMatrix();

        this.applyPos(x, y, z);
        ClientUtils.applyColor(ItemCapturingBall.PRIMARY_COLOR);

        RenderCapturingBall.MODEL.render(entity, partialTicks, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);

        GlStateManager.popMatrix();
    }

    @Override
    public boolean isMultipass() {
        return true;
    }

    @Override
    public void renderMultipass(EntityCapturingBall entity, double x, double y, double z, float entityYaw, float partialTicks) {
        this.bindEntityTexture(entity);

        GlStateManager.pushMatrix();

        this.applyPos(x, y, z);
        ClientUtils.applyColor(ItemCapturingBall.SECONDARY_COLOR);

        RenderCapturingBall.MODEL.renderMultipass(entity, partialTicks, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);

        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();

        this.applyPos(x, y, z);
        ClientUtils.applyColor(ClientUtils.darken(ItemCapturingBall.PRIMARY_COLOR, 0.8f));

        RenderCapturingBall.MODEL.renderStripe(entity, partialTicks, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);

        GlStateManager.popMatrix();
    }

    private void applyPos(double x, double y, double z) {
        GlStateManager.translate((float)x - 0.03125f, (float)y - (0.0625f * 8f), (float)z - 0.03125f);
        GlStateManager.scale(0.6f, 0.6f, 0.6f);
    }

}
