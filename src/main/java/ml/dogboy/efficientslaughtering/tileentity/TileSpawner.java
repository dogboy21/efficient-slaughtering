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

package ml.dogboy.efficientslaughtering.tileentity;

import assets.efficientslaughtering.utilities.DirectionHelper;
import com.mojang.authlib.GameProfile;
import ml.dogboy.efficientslaughtering.Registry;
import ml.dogboy.efficientslaughtering.api.SlaughteringRegistry;
import ml.dogboy.efficientslaughtering.block.BlockSpawner;
import ml.dogboy.efficientslaughtering.config.ESConfig;
import ml.dogboy.efficientslaughtering.item.ItemSpawnerUpgrade;
import ml.dogboy.efficientslaughtering.tileentity.base.BasicEnergyCapability;
import ml.dogboy.efficientslaughtering.tileentity.base.BasicInventoryCapability;
import ml.dogboy.efficientslaughtering.tileentity.base.NBTSaveable;
import ml.dogboy.efficientslaughtering.tileentity.base.PersistantSyncableTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityWitherSkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class TileSpawner extends PersistantSyncableTileEntity implements ITickable {

    private static final Random RNG = new Random();

    private static FakePlayer fakePlayer = null;
    private static DamageSource sourceFakePlayer = new DamageSource("efficientslaughtering_fakeplayer") {

        @Nullable
        @Override
        public Entity getTrueSource() {
            if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) {
                return TileSpawner.getFakePlayer();
            }

            return null;
        }

    };

    private BlockPos counterpart = null;
    private boolean isBottom = false;

    private EntityLivingBase entityToSpawn = null;
    private Map<String, Integer> upgrades = new HashMap<>();
    private SpawnerStats spawnerStats = new SpawnerStats();

    private BasicInventoryCapability inventory = new BasicInventoryCapability(this, 27);
    private BasicEnergyCapability energy = new BasicEnergyCapability(this, 10000000, 20000, 0);

    @Override
    public void update() {
        if (this.world.getTotalWorldTime() % 20 == 0) {
            if (this.counterpart == null) {
                if (this.isValid()) {
                    outer:
                    for (int i = 1; i < ESConfig.spawnerSettings.maxSpawnerPlateDistance; i++) {
                        BlockPos searchPos = this.pos.add(0, i, 0);
                        IBlockState block = this.world.getBlockState(searchPos);

                        if (block.getBlock() == Registry.SPAWNER && block.getValue(BlockSpawner.UPPER)) {
                            TileEntity tileEntity = this.world.getTileEntity(searchPos);
                            if (tileEntity instanceof TileSpawner) {
                                if (((TileSpawner) tileEntity).isValid()) {
                                    this.counterpart = searchPos;
                                    this.isBottom = true;
                                    ((TileSpawner) tileEntity).counterpart = this.pos;
                                    ((TileSpawner) tileEntity).isBottom = false;
                                    this.refreshUpgradeCache();
                                    this.triggerUpdate();
                                    ((TileSpawner) tileEntity).triggerUpdate();
                                    break;
                                }

                            }
                        } else {
                            if (!this.world.isAirBlock(searchPos)) {
                                break;
                            }

                            for (Vec3i direction : DirectionHelper.ALL_AROUND) {
                                if (!this.world.isAirBlock(searchPos.add(direction))) {
                                    break outer;
                                }
                            }
                        }
                    }
                }
            } else {
                boolean shouldBreak = !this.isValid();
                boolean updateCounterpart = false;

                TileEntity counterpartTe = this.world.getTileEntity(this.counterpart);
                if (counterpartTe instanceof TileSpawner) {
                    if (!((TileSpawner) counterpartTe).isValid()) {
                        shouldBreak = true;
                        updateCounterpart = true;
                    }
                } else {
                    shouldBreak = true;
                }

                if (this.isLower()) {
                    if (!shouldBreak) {
                        outer:
                        for (int i = 1; i < ESConfig.spawnerSettings.maxSpawnerPlateDistance; i++) {
                            BlockPos searchPos = this.pos.add(0, i, 0);

                            if (this.world.getBlockState(searchPos).getBlock() == Registry.SPAWNER) {
                                break;
                            }

                            if (!this.world.isAirBlock(searchPos)) {
                                shouldBreak = true;
                                break;
                            }

                            for (Vec3i direction : DirectionHelper.ALL_AROUND) {
                                if (!this.world.isAirBlock(searchPos.add(direction))) {
                                    shouldBreak = true;
                                    break outer;
                                }
                            }
                        }
                    }
                }

                if (shouldBreak) {
                    if (updateCounterpart) {
                        ((TileSpawner) counterpartTe).counterpart = null;
                        ((TileSpawner) counterpartTe).triggerUpdate();
                    }
                    this.counterpart = null;
                    this.triggerUpdate();
                }
            }
        }

        if (!this.world.isRemote && this.entityToSpawn != null && this.isLower()) {
            int speedUpgrades = this.upgrades.getOrDefault("speed", 0);
            int efficiencyUpgrades = this.upgrades.getOrDefault("efficiency", 0);
            int lootingUpgrades = this.upgrades.getOrDefault("looting", 0);
            int beheadingUpgrades = this.upgrades.getOrDefault("beheading", 0);
            boolean fakePlayer = this.upgrades.getOrDefault("player", 0) > 0;

            int speed = (int) Math.ceil(ESConfig.spawnerSettings.spawnerBaseWorkTicks * Math.pow(1 - ESConfig.spawnerSettings.spawnerUpgrades.speedUpgradeBonus, speedUpgrades));
            int energyDraw = (int) Math.ceil(ESConfig.spawnerSettings.spawnerBaseEnergyDraw * Math.pow(1 - ESConfig.spawnerSettings.spawnerUpgrades.efficiencyUpgradeBonus, efficiencyUpgrades));
            this.spawnerStats.baseEnergyDraw = energyDraw;
            if (!this.entityToSpawn.isNonBoss()) {
                int bossDraw = (int) Math.ceil(energyDraw * ESConfig.spawnerSettings.bossEnergyFactor);
                energyDraw += bossDraw;
                this.spawnerStats.additionalBossEnergyDraw = bossDraw;
            }

            int beheadingExtraCost = (int) Math.ceil(energyDraw * ESConfig.spawnerSettings.spawnerUpgrades.beheadingEnergyCost * beheadingUpgrades);
            int speedExtraCost = (int) Math.ceil(energyDraw * ESConfig.spawnerSettings.spawnerUpgrades.speedEnergyCost * speedUpgrades);
            int lootingExtraCost = (int) Math.ceil(energyDraw * ESConfig.spawnerSettings.spawnerUpgrades.lootingEnergyCost * lootingUpgrades);
            int fakePlayerExtraCost = (int) Math.ceil(energyDraw * ESConfig.spawnerSettings.spawnerUpgrades.fakeplayerEnergyCost * (fakePlayer ? 1 : 0));
            energyDraw += beheadingExtraCost + speedExtraCost + lootingExtraCost + fakePlayerExtraCost;

            this.spawnerStats.beheadingEnergyDraw = beheadingExtraCost;
            this.spawnerStats.speedEnergyDraw = speedExtraCost;
            this.spawnerStats.lootingEnergyDraw = lootingExtraCost;
            this.spawnerStats.fakePlayerEnergyDraw = fakePlayerExtraCost;
            this.spawnerStats.totalEnergyDraw = energyDraw;
            this.spawnerStats.workTicks = speed;

            if (this.world.getTotalWorldTime() % speed == 0 && this.energy.getEnergyStored() >= energyDraw) {
                this.entityToSpawn.captureDrops = true;
                this.entityToSpawn.capturedDrops.clear();

                if (fakePlayer) {
                    this.entityToSpawn.attackEntityFrom(TileSpawner.sourceFakePlayer, 0);
                }

                this.entityToSpawn.dropLoot(fakePlayer, lootingUpgrades,
                        fakePlayer ? TileSpawner.sourceFakePlayer : DamageSource.GENERIC);
                this.entityToSpawn.captureDrops = false;

                if (beheadingUpgrades > 0 && RNG.nextInt(ESConfig.spawnerSettings.spawnerUpgrades.beheadingChance) <= beheadingUpgrades) {
                    ItemStack head = this.getHead(this.entityToSpawn);
                    if (head != null) {
                        this.inventory.insertItem(head);
                    }
                }

                for (EntityItem item : this.entityToSpawn.capturedDrops) {
                    this.inventory.insertItem(item.getItem());
                }
                this.energy.drain(energyDraw);
            }
        }
    }

    private ItemStack getHead(EntityLivingBase entity) {
        if (entity instanceof EntitySkeleton) {
            return new ItemStack(Items.SKULL, 1, 0);
        } else if (entity instanceof EntityWitherSkeleton) {
            return new ItemStack(Items.SKULL, 1, 1);
        } else if (entity instanceof EntityZombie && !(entity instanceof EntityPigZombie)) {
            return new ItemStack(Items.SKULL, 1, 2);
        } else if (entity instanceof EntityCreeper) {
            return new ItemStack(Items.SKULL, 1, 4);
        } else if (entity instanceof EntityEnderman && Loader.isModLoaded("Ender IO")) {
            Item skull = Item.REGISTRY.getObject(new ResourceLocation("enderio", "blockEndermanSkull"));
            if (skull != null) {
                return new ItemStack(skull, 1, 0);
            }
        }

        return null;
    }

    private boolean isValid() {
        for (Vec3i direction : DirectionHelper.ALL_AROUND) {
            if (this.world.getBlockState(this.pos.add(direction)).getBlock() != Registry.SPAWNER_BASE) {
                return false;
            }
        }

        for (Vec3i direction : DirectionHelper.ALL_AROUND_PLUS_ONE) {
            Block block = this.world.getBlockState(this.pos.add(direction)).getBlock();
            if (block == Registry.SPAWNER_BASE || block == Registry.SPAWNER) {
                return false;
            }
        }

        return true;
    }

    private static FakePlayer getFakePlayer() {
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
            throw new IllegalStateException("Tried to generate a fakeplayer on Side.CLIENT");
        }

        if (TileSpawner.fakePlayer == null) {
            TileSpawner.fakePlayer = FakePlayerFactory.get(DimensionManager.getWorld(0),
                    new GameProfile(UUID.fromString("8fc51b28-e549-4ebc-9b82-4bef517ccf25"), "[EfficientSlaughtering]"));
        }

        return TileSpawner.fakePlayer;
    }

    public boolean isFormed() {
        return this.counterpart != null;
    }

    public boolean isUpper() {
        return this.counterpart != null && !this.isBottom;
    }

    public boolean isLower() {
        return this.counterpart != null && this.isBottom;
    }

    public TileSpawner getUpper() {
        if (!this.isFormed()) {
            return null;
        }

        if (this.isBottom) {
            return (TileSpawner) this.world.getTileEntity(this.counterpart);
        } else {
            return this;
        }
    }

    public TileSpawner getLower() {
        if (!this.isFormed()) {
            return null;
        }

        if (this.isBottom) {
            return this;
        } else {
            return (TileSpawner) this.world.getTileEntity(this.counterpart);
        }
    }

    protected void notifiyBaseChange(TileSpawnerBase notifier) {
        this.refreshUpgradeCache();
    }

    private void refreshUpgradeCache() {
        boolean blockSpawning = false;
        this.upgrades.clear();
        this.entityToSpawn = null;

        for (Vec3i direction : DirectionHelper.ALL_AROUND) {
            BlockPos searchPos = this.pos.add(direction);
            TileEntity tileEntity = this.world.getTileEntity(searchPos);
            if (tileEntity instanceof TileSpawnerBase) {
                TileSpawnerBase spawnerBase = (TileSpawnerBase) tileEntity;
                ItemStack upgradeItem = spawnerBase.inventory.getStackInSlot(0);
                if (!upgradeItem.isEmpty()) {
                    if (upgradeItem.getItem() == Registry.SPAWNING_CORE) {
                        if (upgradeItem.hasTagCompound()) {
                            if (this.entityToSpawn != null) {
                                blockSpawning = true;
                                this.entityToSpawn = null;
                            }

                            if (!blockSpawning) {
                                Entity entity = EntityList.createEntityByIDFromName(new ResourceLocation(upgradeItem.getTagCompound().getString("CapturedEntity")), this.world);
                                if (entity instanceof EntityLivingBase) {
                                    if (!SlaughteringRegistry.isBlacklisted((EntityLivingBase) entity)
                                            && (entity.isNonBoss() || ESConfig.spawnerSettings.allowBoss)) {
                                        this.entityToSpawn = (EntityLivingBase) entity;
                                    }
                                }
                            }
                        }
                    } else if (upgradeItem.getItem() == Registry.SPAWNER_UPGRADE) {
                        int meta = upgradeItem.getMetadata();
                        if (meta < 0 || meta >= ItemSpawnerUpgrade.UPGRADES.length) {
                            continue;
                        }
                        String upgradeName = ItemSpawnerUpgrade.UPGRADES[meta];
                        this.upgrades.put(upgradeName, this.upgrades.getOrDefault(upgradeName, 0) + 1);
                    }
                }
            }
        }
    }

    public EntityLivingBase getEntityToSpawn() {
        return this.entityToSpawn;
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        BlockPos pos = this.getPos();
        return new AxisAlignedBB(pos.getX() - 1, pos.getY(), pos.getZ() - 1, pos.getX() + 1, 256, pos.getZ() + 1);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (!this.isFormed()) {
            return super.getCapability(capability, facing);
        }

        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return (T) this.getLower().inventory;
        } else if (capability == CapabilityEnergy.ENERGY) {
            return (T) this.getLower().energy;
        }

        return super.getCapability(capability, facing);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        if (!this.isFormed()) {
            return super.hasCapability(capability, facing);
        }

        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return true;
        } else if (capability == CapabilityEnergy.ENERGY) {
            return true;
        }

        return super.hasCapability(capability, facing);
    }

    @Override
    public void writeData(NBTTagCompound tagCompound) {
        this.inventory.writeToNbt(tagCompound);
        this.energy.writeToNbt(tagCompound);
        this.spawnerStats.writeToNbt(tagCompound);
    }

    @Override
    public void readData(NBTTagCompound tagCompound) {
        this.inventory.readFromNbt(tagCompound);
        this.energy.readFromNbt(tagCompound);
        this.spawnerStats.readFromNbt(tagCompound);
    }

    public static class SpawnerStats implements NBTSaveable {

        private int baseEnergyDraw = 0;
        private int additionalBossEnergyDraw = 0;
        private int beheadingEnergyDraw = 0;
        private int speedEnergyDraw = 0;
        private int lootingEnergyDraw = 0;
        private int fakePlayerEnergyDraw = 0;
        private int totalEnergyDraw = 0;
        private int workTicks = 0;

        @Override
        public void writeToNbt(NBTTagCompound tagCompound) {
            NBTTagCompound stats = new NBTTagCompound();

            stats.setInteger("BaseEnergyDraw", this.baseEnergyDraw);
            stats.setInteger("AdditionalBossEnergyDraw", this.additionalBossEnergyDraw);
            stats.setInteger("BeheadingEnergyDraw", this.beheadingEnergyDraw);
            stats.setInteger("SpeedEnergyDraw", this.speedEnergyDraw);
            stats.setInteger("LootingEnergyDraw", this.lootingEnergyDraw);
            stats.setInteger("FakePlayerEnergyDraw", this.fakePlayerEnergyDraw);
            stats.setInteger("TotalEnergyDraw", this.totalEnergyDraw);
            stats.setInteger("WorkTicks", this.workTicks);

            tagCompound.setTag("Stats", stats);
        }

        @Override
        public void readFromNbt(NBTTagCompound tagCompound) {
            if (tagCompound.hasKey("Stats")) {
                NBTTagCompound stats = tagCompound.getCompoundTag("Stats");

                this.baseEnergyDraw = stats.getInteger("BaseEnergyDraw");
                this.additionalBossEnergyDraw = stats.getInteger("AdditionalBossEnergyDraw");
                this.beheadingEnergyDraw = stats.getInteger("BeheadingEnergyDraw");
                this.speedEnergyDraw = stats.getInteger("SpeedEnergyDraw");
                this.lootingEnergyDraw = stats.getInteger("LootingEnergyDraw");
                this.fakePlayerEnergyDraw = stats.getInteger("FakePlayerEnergyDraw");
                this.totalEnergyDraw = stats.getInteger("TotalEnergyDraw");
                this.workTicks = stats.getInteger("WorkTicks");
            }
        }

        public int getBaseEnergyDraw() {
            return this.baseEnergyDraw;
        }

        public int getAdditionalBossEnergyDraw() {
            return this.additionalBossEnergyDraw;
        }

        public int getBeheadingEnergyDraw() {
            return this.beheadingEnergyDraw;
        }

        public int getSpeedEnergyDraw() {
            return this.speedEnergyDraw;
        }

        public int getLootingEnergyDraw() {
            return this.lootingEnergyDraw;
        }

        public int getFakePlayerEnergyDraw() {
            return this.fakePlayerEnergyDraw;
        }

        public int getTotalEnergyDraw() {
            return this.totalEnergyDraw;
        }

        public int getWorkTicks() {
            return this.workTicks;
        }

    }

    public SpawnerStats getSpawnerStats() {
        return this.spawnerStats;
    }

}
