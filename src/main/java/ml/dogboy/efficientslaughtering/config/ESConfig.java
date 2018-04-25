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

package ml.dogboy.efficientslaughtering.config;

import ml.dogboy.efficientslaughtering.Reference;
import net.minecraftforge.common.config.Config;

@Config(modid = Reference.MODID, type = Config.Type.INSTANCE)
public class ESConfig {

    public static class ClientSettings {

        @Config.Comment("Should the energy hud be visible without sneaking")
        public boolean alwaysShowEnergyHud = false;

    }

    @Config.Comment("Client-side settings")
    public static ClientSettings clientSettings = new ClientSettings();

    @Config.Comment("The internal energy capacity of a Ball Blender")
    @Config.RangeInt(min = 1000)
    public static int ballBlenderEnergyCapacity = 1000000;

    @Config.Comment("How much energy the Ball Blender drains per work tick")
    @Config.RangeInt(min = 10)
    public static int ballBlenderEnergyDraw = 1000;

    @Config.Comment("How long does the Ball Blender take to produce a Spawning Core")
    @Config.RangeInt(min = 10)
    public static int ballBlenderWorkTicks = 1200;

    @Config.RangeInt(min = 3)
    @Config.Comment("The max distance between the upper and lower parts of a spawner")
    public static int maxSpawnerPlateDistance = 10;

    @Config.Comment("How much energy the Spawner drains per killed mob")
    @Config.RangeInt(min = 1000)
    public static int spawnerBaseEnergyDraw = 200000;

    @Config.Comment("How long does the Spawner take to kill a mob")
    @Config.RangeInt(min = 10)
    public static int spawnerBaseWorkTicks = 30;

    @Config.RangeDouble(min = 0.01, max = 0.99)
    @Config.Comment("How much energy cost should be removed by an efficiency upgrade")
    public static double efficiencyUpgradeBonus = 0.1;

    @Config.RangeDouble(min = 0.01, max = 0.99)
    @Config.Comment("How much work ticks should be removed by an speed upgrade")
    public static double speedUpgradeBonus = 0.1;

    @Config.RangeInt(min = 1)
    @Config.Comment("The chance of a head drop is (Number of beheading upgrades) in this number")
    public static int beheadingChance = 10;

}
