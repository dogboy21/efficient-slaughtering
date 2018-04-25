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

import ml.dogboy.efficientslaughtering.block.BlockBallBlender;
import ml.dogboy.efficientslaughtering.block.BlockSpawner;
import ml.dogboy.efficientslaughtering.block.BlockSpawnerBase;
import ml.dogboy.efficientslaughtering.entity.EntityCapturingBall;
import ml.dogboy.efficientslaughtering.item.ItemCapturingBall;
import ml.dogboy.efficientslaughtering.item.ItemSpawnerUpgrade;
import ml.dogboy.efficientslaughtering.item.ItemSpawningCore;
import ml.dogboy.efficientslaughtering.tileentity.TileBallBlender;
import ml.dogboy.efficientslaughtering.tileentity.TileSpawner;
import ml.dogboy.efficientslaughtering.tileentity.TileSpawnerBase;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod.EventBusSubscriber(modid = Reference.MODID)
@GameRegistry.ObjectHolder(Reference.MODID)
public class Registry {

    public static final Item CAPTURING_BALL = Items.AIR;
    public static final Item SPAWNING_CORE = Items.AIR;
    public static final Item SPAWNER_UPGRADE = Items.AIR;

    public static final Block BALL_BLENDER = Blocks.AIR;
    public static final Block SPAWNER = Blocks.AIR;
    public static final Block SPAWNER_BASE = Blocks.AIR;

    public static Item BALL_BLENDER_ITEM = Items.AIR;
    public static Item SPAWNER_ITEM = Items.AIR;
    public static Item SPAWNER_BASE_ITEM = Items.AIR;

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(
                new ItemCapturingBall(),
                new ItemSpawningCore(),
                new ItemSpawnerUpgrade(),


                BALL_BLENDER_ITEM = new ItemBlock(BALL_BLENDER).setRegistryName(BALL_BLENDER.getRegistryName()),
                SPAWNER_ITEM = new ItemBlock(SPAWNER).setRegistryName(SPAWNER.getRegistryName()),
                SPAWNER_BASE_ITEM = new ItemBlock(SPAWNER_BASE).setRegistryName(SPAWNER_BASE.getRegistryName())
                );
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().registerAll(
                new BlockBallBlender(),
                new BlockSpawner(),
                new BlockSpawnerBase()
        );

        GameRegistry.registerTileEntity(TileBallBlender.class, Reference.MODID + ":ball_blender");
        GameRegistry.registerTileEntity(TileSpawner.class, Reference.MODID + ":spawner");
        GameRegistry.registerTileEntity(TileSpawnerBase.class, Reference.MODID + ":spawner_base");
    }

    @SubscribeEvent
    public static void registerEntities(RegistryEvent.Register<EntityEntry> event) {
        event.getRegistry().registerAll(
                EntityEntryBuilder.create()
                        .entity(EntityCapturingBall.class)
                        .id(new ResourceLocation(Reference.MODID, "thrown_capturing_ball"), 0)
                        .name("thrown_capturing_ball")
                        .tracker(80, 1, true)
                        .build()
        );
    }

}
