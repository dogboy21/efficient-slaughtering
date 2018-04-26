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


    public static class BallBlenderSettings {

        @Config.Comment("The internal energy capacity of a Ball Blender")
        @Config.RangeInt(min = 1000)
        public int ballBlenderEnergyCapacity = 1000000;

        @Config.Comment("How much energy the Ball Blender drains per work tick")
        @Config.RangeInt(min = 10)
        public int ballBlenderEnergyDraw = 1000;

        @Config.Comment("How long does the Ball Blender take to produce a Spawning Core")
        @Config.RangeInt(min = 10)
        public int ballBlenderWorkTicks = 1200;

    }

    @Config.Comment("Capturing Ball Blender settings")
    public static BallBlenderSettings ballBlenderSettings = new BallBlenderSettings();


    public static class SpawnerSettings {

        @Config.RangeInt(min = 3)
        @Config.Comment("The max distance between the upper and lower parts of a spawner")
        public int maxSpawnerPlateDistance = 10;

        @Config.Comment("How much energy the Spawner drains per killed mob")
        @Config.RangeInt(min = 1000)
        public int spawnerBaseEnergyDraw = 200000;

        @Config.Comment("How long does the Spawner take to kill a mob")
        @Config.RangeInt(min = 10)
        public int spawnerBaseWorkTicks = 30;

        public static class SpawnerUpgrades {

            @Config.RangeDouble(min = 0.01, max = 0.99)
            @Config.Comment("How much energy cost should be removed by an efficiency upgrade")
            public double efficiencyUpgradeBonus = 0.1;

            @Config.RangeDouble(min = 0.01, max = 0.99)
            @Config.Comment("How much work ticks should be removed by an speed upgrade")
            public double speedUpgradeBonus = 0.1;

            @Config.RangeInt(min = 1)
            @Config.Comment("The chance of a head drop is (Number of beheading upgrades) in this number")
            public int beheadingChance = 10;


            @Config.RangeInt(min = 0, max = 10)
            @Config.Comment("The energy cost of one beheading upgrade")
            public double beheadingEnergyCost = 0.08;

            @Config.RangeInt(min = 0, max = 10)
            @Config.Comment("The energy cost of one speed upgrade")
            public double speedEnergyCost = 0.05;

            @Config.RangeInt(min = 0, max = 10)
            @Config.Comment("The energy cost of one looting upgrade")
            public double lootingEnergyCost = 0.05;

            @Config.RangeInt(min = 0, max = 10)
            @Config.Comment("The energy cost of the fakeplayer upgrade")
            public double fakeplayerEnergyCost = 0.1;

        }

        @Config.Comment("Spawner upgrade settings")
        public SpawnerUpgrades spawnerUpgrades = new SpawnerUpgrades();

        @Config.Comment("Are boss mobs allowed to be spawned")
        public boolean allowBoss = true;

        @Config.RangeInt(min = 0, max = 10)
        @Config.Comment("The additional energy cost when a boss is spawned")
        public double bossEnergyFactor = 0.5;

    }

    @Config.Comment("Spawner settings")
    public static SpawnerSettings spawnerSettings = new SpawnerSettings();

}
