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
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EntityCapturingBall extends EntityThrowable {

    private boolean precise = false;

    public EntityCapturingBall(World worldIn) {
        super(worldIn);
        this.setEntityInvulnerable(true);
        this.setSize(0.5f, 0.5f);
    }

    public EntityCapturingBall(World worldIn, EntityLivingBase throwerIn, boolean precise) {
        super(worldIn, throwerIn);
        this.precise = precise;
        this.setEntityInvulnerable(true);
        this.setSize(0.5f, 0.5f);
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        if (result.entityHit instanceof EntityLiving) {
            EntityLiving hit = (EntityLiving) result.entityHit;
            String name = EntityList.getEntityString(hit);

            ItemStack stack = new ItemStack(Registry.CAPTURING_BALL, 1, this.precise ? 1 : 0);

            if (!SlaughteringRegistry.isBlacklisted(hit.getClass()) && name != null) {
                NBTTagCompound nbt = new NBTTagCompound();
                hit.writeEntityToNBT(nbt);

                NBTTagCompound itemData = new NBTTagCompound();
                itemData.setString("CapturedEntityName", name);
                itemData.setTag("CapturedEntity", nbt);


                stack.setTagCompound(itemData);
            }

            this.setDead();
            if (!this.world.isRemote) {
                this.entityDropItem(stack, 0.5f);
            }

            return;
        }

        if (result.typeOfHit == RayTraceResult.Type.BLOCK) {
            this.setDead();
            if (!this.world.isRemote) {
                this.entityDropItem(new ItemStack(Registry.CAPTURING_BALL, 1, this.precise ? 1 : 0), 0.5f);
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

}
