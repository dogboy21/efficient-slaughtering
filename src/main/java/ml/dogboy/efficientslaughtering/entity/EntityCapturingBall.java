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

package ml.dogboy.efficientslaughtering.entity;

import ml.dogboy.efficientslaughtering.Registry;
import ml.dogboy.efficientslaughtering.api.SlaughteringRegistry;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EntityCapturingBall extends EntityThrowable {

    private boolean precise = false;

    public EntityCapturingBall(World worldIn) {
        super(worldIn);
        this.setEntityInvulnerable(true);
        this.setSize(0.3f, 0.3f);
    }

    public EntityCapturingBall(World worldIn, EntityLivingBase throwerIn, boolean precise) {
        super(worldIn, throwerIn);
        this.precise = precise;
        this.setEntityInvulnerable(true);
        this.setSize(0.3f, 0.3f);
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        if (result.entityHit instanceof EntityLivingBase && !(result.entityHit instanceof EntityPlayer)) {
            EntityLivingBase hit = (EntityLivingBase) result.entityHit;
            ItemStack stack = new ItemStack(Registry.CAPTURING_BALL, 1, this.precise ? 1 : 0);
            ResourceLocation key = EntityList.getKey(hit);

            if (!SlaughteringRegistry.isBlacklisted(hit) && key != null) {
                NBTTagCompound itemData = new NBTTagCompound();
                itemData.setString("CapturedEntity", key.toString());

                stack.setTagCompound(itemData);
                hit.setDead();
            }

            this.setDead();
            if (!this.world.isRemote) {
                this.entityDropItem(stack, 0);
            }

            return;
        }

        if (result.typeOfHit == RayTraceResult.Type.BLOCK) {
            this.setDead();
            if (!this.world.isRemote) {
                this.entityDropItem(new ItemStack(Registry.CAPTURING_BALL, 1, this.precise ? 1 : 0), 0);
            }
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        compound.setBoolean("Precise", this.precise);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        this.precise = compound.getBoolean("Precise");
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
    }

}
