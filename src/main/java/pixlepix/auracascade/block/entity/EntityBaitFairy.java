package pixlepix.auracascade.block.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import pixlepix.auracascade.AuraCascade;
import pixlepix.auracascade.network.PacketBurst;

import java.util.Random;

/**
 * Created by pixlepix on 12/16/14.
 */
public class EntityBaitFairy extends EntityFairy {
    public EntityBaitFairy(World p_i1582_1_) {
        super(p_i1582_1_);
    }

    @Override
    public void onEntityUpdate() {
        super.onEntityUpdate();
        if (!world.isRemote) {
            if (new Random().nextInt(3600) == 0) {
                Random random = new Random();
                Entity entity;
                switch (random.nextInt(4)) {
                    case 0:
                        entity = new EntityCow(world);
                        break;

                    case 1:
                        entity = new EntityChicken(world);
                        break;

                    case 2:
                        entity = new EntityPig(world);
                        break;

                    default:
                        entity = new EntitySheep(world);
                        break;
                }

                entity.setPosition(posX, posY, posZ);
                world.spawnEntity(entity);
                AuraCascade.proxy.networkWrapper.sendToAllAround(new PacketBurst(5, entity.posX, entity.posY, entity.posZ), new NetworkRegistry.TargetPoint(entity.world.provider.getDimension(), entity.posX, entity.posY, entity.posZ, 32));

            }
        }
    }
}


