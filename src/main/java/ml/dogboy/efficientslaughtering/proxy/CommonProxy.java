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

package ml.dogboy.efficientslaughtering.proxy;

import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class CommonProxy {

    public void onPreInit() {

    }

    public void onInit() {

    }

    public void onPostInit() {

    }

    public World getWorld() {
        return FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(0);
    }

}
