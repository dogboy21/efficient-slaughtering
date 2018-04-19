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

package ml.dogboy.efficientslaughtering.client;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;

public class ClientUtils {

    public static void applyColor(int color) {
        float cr = (color >>> 16) & 0xFF;
        float cg = (color >>> 8) & 0xFF;
        float cb = color & 0xFF;

        GlStateManager.color(cr / 255f, cg / 255f, cb / 255f);
    }

    public static int darken(int color, float factor) {
        int cr = (color >>> 16) & 0xFF;
        int cg = (color >>> 8) & 0xFF;
        int cb = color & 0xFF;

        cr = MathHelper.clamp((int) (cr * factor), 0, 255);
        cg = MathHelper.clamp((int) (cg * factor), 0, 255);
        cb = MathHelper.clamp((int) (cb * factor), 0, 255);

        return (cr << 16) | (cg << 8) | cb;
    }

}
