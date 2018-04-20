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

import ml.dogboy.efficientslaughtering.EfficientSlaughtering;
import ml.dogboy.efficientslaughtering.Reference;
import ml.dogboy.efficientslaughtering.Registry;
import ml.dogboy.efficientslaughtering.client.renderer.RenderCapturingBall;
import ml.dogboy.efficientslaughtering.entity.EntityCapturingBall;
import ml.dogboy.efficientslaughtering.proxy.CommonProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ClientProxy extends CommonProxy {

    private static Minecraft minecraft;

    @Override
    public void onPreInit() {
        ClientProxy.minecraft = Minecraft.getMinecraft();

        MinecraftForge.EVENT_BUS.register(this);

        RenderingRegistry.registerEntityRenderingHandler(EntityCapturingBall.class, RenderCapturingBall::new);
    }

    @Override
    public void onInit() {
        Minecraft.getMinecraft().getItemColors().registerItemColorHandler((IItemColor) Registry.CAPTURING_BALL, Registry.CAPTURING_BALL);
    }

    @SubscribeEvent
    public void registerModels(ModelRegistryEvent event) {
        EfficientSlaughtering.logger.info("Registering models");

        ModelLoader.setCustomModelResourceLocation(Registry.CAPTURING_BALL, 0,
                new ModelResourceLocation(new ResourceLocation(Reference.MODID, "capturing_ball"), "inventory"));
        ModelLoader.setCustomModelResourceLocation(Registry.CAPTURING_BALL, 1,
                new ModelResourceLocation(new ResourceLocation(Reference.MODID, "capturing_ball"), "inventory"));
    }

}
