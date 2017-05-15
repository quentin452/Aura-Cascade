package pixlepix.auracascade.block.entity;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import pixlepix.auracascade.block.BlockExplosionContainer;

import java.util.Random;

/**
 * Created by localmacaccount on 6/4/15.
 */
public class EntityMinerExplosion extends Entity {
    public int charge;
    public long lastCharged;
    public long lastExplosion;

    public EntityMinerExplosion(World world) {
        super(world);
        setSize(1F, 1F);
    }

    @Override
    protected void entityInit() {

    }


    public void bounce() {
        Random r = new Random();
        double speed = .25 * (1 + Math.log10(charge));
        motionX = (r.nextDouble() * speed) - (speed / 2);
        motionY = (r.nextDouble() * speed) - (speed / 2);
        motionZ = (r.nextDouble() * speed) - (speed / 2);
        velocityChanged = true;
    }


    /**
     * Gets called every tick from main Entity class
     */
    @Override
    public void onEntityUpdate() {
        super.onEntityUpdate();
        if (!world.isRemote && lastCharged + 100 < world.getTotalWorldTime()) {
            setDead();

        }

        if (isCollided) {
            if (!world.isRemote) {
                explode();
                bounce();
            } else {
                //this.world.spawnParticle("hugeexplosion", posX, posY, posZ, 0.0D, 0.0D, 0.0D);
            }
        }

        move(MoverType.SELF,motionX, motionY, motionZ);
        if (world.isRemote && world.getTotalWorldTime() % 2 == 0) {
            this.world.spawnParticle(EnumParticleTypes.EXPLOSION_LARGE, posX, posY, posZ, 0.0D, 0.0D, 0.0D);
        }
    }

    public void explode() {
        int delay = (int) Math.max(0, (100 - 20 * Math.log10(charge)));
        if (lastExplosion + delay < world.getTotalWorldTime()) {
            lastExplosion = world.getTotalWorldTime();
            BlockPos pos = new BlockPos(this);

            boolean contained = false;

            for (int i = -2; i < 3; i++) {
                for (int j = -2; j < 3; j++) {
                    for (int k = -2; k < 3; k++) {
                        BlockPos pos_ = pos.add(i, j, k);
                        Block block = world.getBlockState(pos_).getBlock();
                        if (block instanceof BlockExplosionContainer) {
                            contained = true;
                            Random r = new Random();
                            int nextDamage = world.getBlockState(pos_).getValue(BlockExplosionContainer.DAMAGE) + 1;
                            if (r.nextDouble() > ((BlockExplosionContainer) block).getChanceToResist()) {

                                if (nextDamage > 15) {

                                    world.setBlockToAir(pos_);
                                } else {

                                    world.setBlockState(pos_, world.getBlockState(pos_).withProperty(BlockExplosionContainer.DAMAGE, nextDamage), 3);
                                }
                            }
                        }
                    }
                }
            }
            if (!contained) {
                world.createExplosion(this, posX, posY, posZ, 50F, true);
                setDead();
            }
        }
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbt) {
        charge = nbt.getInteger("charge");
        lastCharged = nbt.getLong("lastCharged");
        lastExplosion = nbt.getLong("lastExplosion");

    }


    @Override
    public boolean shouldRenderInPass(int pass) {
        return false;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbt) {
        nbt.setInteger("charge", charge);
        nbt.setLong("lastCharged", lastCharged);
        nbt.setLong("lastExplosion", lastExplosion);
    }
}
