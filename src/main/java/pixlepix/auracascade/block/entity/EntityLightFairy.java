package pixlepix.auracascade.block.entity;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import pixlepix.auracascade.block.FairyTorch;
import pixlepix.auracascade.registry.BlockRegistry;

/**
 * Created by pixlepix on 12/20/14.
 */
public class EntityLightFairy extends EntityFairy {
    public EntityLightFairy(World p_i1582_1_) {
        super(p_i1582_1_);
    }

    @Override
    public void onEntityUpdate() {
        BlockPos pos = new BlockPos(this);
        int lightValue = world.getLight(pos);
        if (lightValue < 10 && !world.isRemote) {
            if (world.isAirBlock(pos)) {
                world.setBlockState(pos, BlockRegistry.getFirstBlockFromClass(FairyTorch.class).getDefaultState());
            }
        }
    }
}
