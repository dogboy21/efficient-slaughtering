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

package ml.dogboy.efficientslaughtering;

import ml.dogboy.efficientslaughtering.item.ItemCapturingBall;
import ml.dogboy.efficientslaughtering.proxy.CommonProxy;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = Reference.MODID, name = Reference.MODNAME, version = Reference.VERSION)
public class EfficientSlaughtering {

    public static final Logger logger = LogManager.getLogger(EfficientSlaughtering.class);

    @Mod.Instance(Reference.MODID)
    private static EfficientSlaughtering instance;

    @SidedProxy(serverSide = Reference.PROXY, clientSide = Reference.CLIENT_PROXY)
    private static CommonProxy proxy;

    public static final CreativeTabs tab = new CreativeTabs(Reference.MODID) {
        @Override
        public ItemStack getTabIconItem() {
            return new ItemStack(Registry.CAPTURING_BALL);
        }
    };

    public static final CreativeTabs mobTab = new CreativeTabs(Reference.MODID + "_mobs") {
        @Override
        public ItemStack getTabIconItem() {
            return ItemCapturingBall.getForEntity(new ResourceLocation("minecraft", "zombie"));
        }
    };


    @Mod.EventHandler
    public void onPreInit(FMLPreInitializationEvent event) {
        EfficientSlaughtering.proxy.onPreInit();
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Mod.EventHandler
    public void onInit(FMLInitializationEvent event) {
        EfficientSlaughtering.proxy.onInit();
    }

    @Mod.EventHandler
    public void onPostInit(FMLPostInitializationEvent event) {
        EfficientSlaughtering.proxy.onPostInit();
    }

    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(Reference.MODID)) {
            System.out.println("CONFIG SYNC");
            ConfigManager.sync(Reference.MODID, Config.Type.INSTANCE);
        }
    }

    public static EfficientSlaughtering getInstance() {
        return EfficientSlaughtering.instance;
    }

    public static CommonProxy getProxy() {
        return EfficientSlaughtering.proxy;
    }

}
