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

package ml.dogboy.efficientslaughtering.client.models;

import net.minecraft.client.model.IMultipassModel;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelCapturingBall extends ModelBase implements IMultipassModel {

    private final ModelRenderer basePart;

    private final ModelRenderer plateLeftTop;
    private final ModelRenderer plateLeftMiddle;
    private final ModelRenderer plateLeftBottom;
    private final ModelRenderer plateLeftCenter;

    private final ModelRenderer plateRightTop;
    private final ModelRenderer plateRightMiddle;
    private final ModelRenderer plateRightBottom;
    private final ModelRenderer plateRightCenter;

    private final ModelRenderer plateFrontTop;
    private final ModelRenderer plateFrontMiddle;
    private final ModelRenderer plateFrontBottom;
    private final ModelRenderer plateFrontCenter;

    private final ModelRenderer plateBackTop;
    private final ModelRenderer plateBackMiddle;
    private final ModelRenderer plateBackBottom;
    private final ModelRenderer plateBackCenter;

    public ModelCapturingBall() {
        this.textureWidth = 64;
        this.textureHeight = 32;
        this.plateRightBottom = new ModelRenderer(this, 24, 0);
        this.plateRightBottom.setRotationPoint(4.0F, 18.0F, -2.0F);
        this.plateRightBottom.addBox(0.0F, 0.0F, 0.0F, 1, 2, 6, 0.0F);
        this.plateFrontTop = new ModelRenderer(this, 0, 14);
        this.plateFrontTop.setRotationPoint(-2.0F, 14.0F, -3.0F);
        this.plateFrontTop.addBox(0.0F, 0.0F, 0.0F, 6, 2, 1, 0.0F);
        this.plateBackTop = new ModelRenderer(this, 0, 14);
        this.plateBackTop.setRotationPoint(-2.0F, 14.0F, 4.0F);
        this.plateBackTop.addBox(0.0F, 0.0F, 0.0F, 6, 2, 1, 0.0F);
        this.plateRightCenter = new ModelRenderer(this, 56, 4);
        this.plateRightCenter.setRotationPoint(4.1F, 15.5F, -0.5F);
        this.plateRightCenter.addBox(0.0F, 0.0F, 0.0F, 1, 3, 3, 0.0F);
        this.plateFrontMiddle = new ModelRenderer(this, 0, 17);
        this.plateFrontMiddle.setRotationPoint(-2.0F, 16.0F, -3.0F);
        this.plateFrontMiddle.addBox(0.0F, 0.0F, 0.0F, 6, 2, 1, 0.0F);
        this.plateLeftTop = new ModelRenderer(this, 24, 0);
        this.plateLeftTop.setRotationPoint(-3.0F, 14.0F, -2.0F);
        this.plateLeftTop.addBox(0.0F, 0.0F, 0.0F, 1, 2, 6, 0.0F);
        this.plateBackBottom = new ModelRenderer(this, 0, 14);
        this.plateBackBottom.setRotationPoint(-2.0F, 18.0F, 4.0F);
        this.plateBackBottom.addBox(0.0F, 0.0F, 0.0F, 6, 2, 1, 0.0F);
        this.plateLeftCenter = new ModelRenderer(this, 56, 4);
        this.plateLeftCenter.setRotationPoint(-3.1F, 15.5F, -0.5F);
        this.plateLeftCenter.addBox(0.0F, 0.0F, 0.0F, 1, 3, 3, 0.0F);
        this.plateLeftMiddle = new ModelRenderer(this, 24, 8);
        this.plateLeftMiddle.setRotationPoint(-3.0F, 16.0F, -2.0F);
        this.plateLeftMiddle.addBox(0.0F, 0.0F, 0.0F, 1, 2, 6, 0.0F);
        this.plateLeftBottom = new ModelRenderer(this, 24, 0);
        this.plateLeftBottom.setRotationPoint(-3.0F, 18.0F, -2.0F);
        this.plateLeftBottom.addBox(0.0F, 0.0F, 0.0F, 1, 2, 6, 0.0F);
        this.basePart = new ModelRenderer(this, 0, 0);
        this.basePart.setRotationPoint(-2.0F, 13.0F, -2.0F);
        this.basePart.addBox(0.0F, 0.0F, 0.0F, 6, 8, 6, 0.0F);
        this.plateFrontBottom = new ModelRenderer(this, 0, 14);
        this.plateFrontBottom.setRotationPoint(-2.0F, 18.0F, -3.0F);
        this.plateFrontBottom.addBox(0.0F, 0.0F, 0.0F, 6, 2, 1, 0.0F);
        this.plateBackCenter = new ModelRenderer(this, 56, 0);
        this.plateBackCenter.setRotationPoint(-0.5F, 15.5F, 4.1F);
        this.plateBackCenter.addBox(0.0F, 0.0F, 0.0F, 3, 3, 1, 0.0F);
        this.plateBackMiddle = new ModelRenderer(this, 0, 17);
        this.plateBackMiddle.setRotationPoint(-2.0F, 16.0F, 4.0F);
        this.plateBackMiddle.addBox(0.0F, 0.0F, 0.0F, 6, 2, 1, 0.0F);
        this.plateRightTop = new ModelRenderer(this, 24, 0);
        this.plateRightTop.setRotationPoint(4.0F, 14.0F, -2.0F);
        this.plateRightTop.addBox(0.0F, 0.0F, 0.0F, 1, 2, 6, 0.0F);
        this.plateRightMiddle = new ModelRenderer(this, 24, 8);
        this.plateRightMiddle.setRotationPoint(4.0F, 16.0F, -2.0F);
        this.plateRightMiddle.addBox(0.0F, 0.0F, 0.0F, 1, 2, 6, 0.0F);
        this.plateFrontCenter = new ModelRenderer(this, 56, 0);
        this.plateFrontCenter.setRotationPoint(-0.5F, 15.5F, -3.1F);
        this.plateFrontCenter.addBox(0.0F, 0.0F, 0.0F, 3, 3, 1, 0.0F);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        this.renderOtherParts(entity, f, f1, f2, f3, f4, f5);
    }

    @Override
    public void renderMultipass(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        this.renderCenterParts(entity, f, f1, f2, f3, f4, f5);
    }

    public void renderStripe(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        this.plateFrontMiddle.render(f5);
        this.plateLeftMiddle.render(f5);
        this.plateRightMiddle.render(f5);
        this.plateBackMiddle.render(f5);
    }

    public void renderCenterParts(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        this.plateFrontCenter.render(f5);
        this.plateBackCenter.render(f5);
        this.plateLeftCenter.render(f5);
        this.plateRightCenter.render(f5);
    }

    public void renderOtherParts(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        this.basePart.render(f5);

        this.plateFrontTop.render(f5);
        this.plateFrontBottom.render(f5);

        this.plateLeftTop.render(f5);
        this.plateLeftBottom.render(f5);

        this.plateBackTop.render(f5);
        this.plateBackBottom.render(f5);

        this.plateRightTop.render(f5);
        this.plateRightBottom.render(f5);
    }

}
