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

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelCapturingBall extends ModelBase {

    public ModelRenderer basePart;
    public ModelRenderer side1;
    public ModelRenderer side2;
    public ModelRenderer side3;
    public ModelRenderer side4;

    public ModelCapturingBall() {
        this.textureWidth = 64;
        this.textureHeight = 32;
        this.side3 = new ModelRenderer(this, 24, 0);
        this.side3.setRotationPoint(4.0F, 14.0F, -2.0F);
        this.side3.addBox(0.0F, 0.0F, 0.0F, 1, 6, 6, 0.0F);
        this.basePart = new ModelRenderer(this, 0, 0);
        this.basePart.setRotationPoint(-2.0F, 13.0F, -2.0F);
        this.basePart.addBox(0.0F, 0.0F, 0.0F, 6, 8, 6, 0.0F);
        this.side1 = new ModelRenderer(this, 0, 14);
        this.side1.setRotationPoint(-2.0F, 14.0F, -3.0F);
        this.side1.addBox(0.0F, 0.0F, 0.0F, 6, 6, 1, 0.0F);
        this.side4 = new ModelRenderer(this, 24, 0);
        this.side4.setRotationPoint(-3.0F, 14.0F, -2.0F);
        this.side4.addBox(0.0F, 0.0F, 0.0F, 1, 6, 6, 0.0F);
        this.side2 = new ModelRenderer(this, 0, 14);
        this.side2.setRotationPoint(-2.0F, 14.0F, 4.0F);
        this.side2.addBox(0.0F, 0.0F, 0.0F, 6, 6, 1, 0.0F);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) { 
        this.side3.render(f5);
        this.basePart.render(f5);
        this.side1.render(f5);
        this.side4.render(f5);
        this.side2.render(f5);
    }

    /**
     * This is a helper function from Tabula to set the rotation of model parts
     */
    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
