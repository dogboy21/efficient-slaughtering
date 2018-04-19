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

package ml.dogboy.efficientslaughtering.client.proxy;

import ml.dogboy.efficientslaughtering.client.renderer.RenderCapturingBall;
import ml.dogboy.efficientslaughtering.entity.EntityCapturingBall;
import ml.dogboy.efficientslaughtering.proxy.CommonProxy;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

public class ClientProxy extends CommonProxy {

    @Override
    public void onPreInit() {
        RenderingRegistry.registerEntityRenderingHandler(EntityCapturingBall.class, RenderCapturingBall::new);
    }

}
