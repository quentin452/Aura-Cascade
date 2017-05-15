package pixlepix.auracascade.item;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import pixlepix.auracascade.block.entity.EntityFairy;
import pixlepix.auracascade.data.recipe.PylonRecipe;
import pixlepix.auracascade.data.recipe.PylonRecipeComponent;
import pixlepix.auracascade.registry.BlockRegistry;
import pixlepix.auracascade.registry.ITTinkererItem;
import pixlepix.auracascade.registry.ThaumicTinkererRecipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by pixlepix on 12/8/14.
 */
public class ItemFairyRing extends Item implements ITTinkererItem, IBauble {

    public static final String name = "fairyRing";

    public ItemFairyRing() {
        super();
        setMaxStackSize(1);
    }

    public static void makeFaries(ItemStack ringStack, EntityLivingBase entity) {
        if (entity instanceof EntityPlayer && !((EntityPlayer) entity).world.isRemote) {

            if (ringStack.getTagCompound() == null) {
                ringStack.setTagCompound(new NBTTagCompound());
            }
            int[] tagList = ringStack.getTagCompound().getIntArray("fairyList");
            for (int i : tagList) {
                @SuppressWarnings("unchecked")
				Class<? extends EntityFairy> fairyClass = ItemFairyCharm.fairyClasses[i];
                EntityFairy fairy;
                try {
                    fairy = fairyClass.getConstructor(World.class).newInstance(((EntityPlayer) entity).world);

                    fairy.setPosition(((EntityPlayer) entity).posX, ((EntityPlayer) entity).posY, ((EntityPlayer) entity).posZ);
                    fairy.player = (EntityPlayer) entity;

                    ((EntityPlayer) entity).world.spawnEntity(fairy);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }


    public static void killNearby(EntityLivingBase entityLivingBase) {
        List<EntityFairy> fairies = entityLivingBase.world.getEntitiesWithinAABB(EntityFairy.class, new AxisAlignedBB(entityLivingBase.posX - 50, entityLivingBase.posY - 50, entityLivingBase.posZ - 50, entityLivingBase.posX + 50, entityLivingBase.posY + 50, entityLivingBase.posZ + 50));
        for (EntityFairy fairy : fairies) {
            if (fairy.player == entityLivingBase) {
                fairy.setDead();

            }
        }
    }

    @Override
    public BaubleType getBaubleType(ItemStack itemStack) {
        return BaubleType.RING;
    }

    @Override
    public void onWornTick(ItemStack itemStack, EntityLivingBase entityLivingBase) {

    }

    @Override
    public void onEquipped(ItemStack ringStack, EntityLivingBase entityLivingBase) {
        makeFaries(ringStack, entityLivingBase);
    }

    @Override
    public void onUnequipped(ItemStack itemStack, EntityLivingBase entityLivingBase) {
        killNearby(entityLivingBase);

    }

    @Override
    public boolean canEquip(ItemStack itemStack, EntityLivingBase entityLivingBase) {
        return true;
    }

    @Override
    public boolean canUnequip(ItemStack itemStack, EntityLivingBase entityLivingBase) {
        return true;
    }

    @Override
    public ArrayList<Object> getSpecialParameters() {
        return null;
    }

    @Override
    public String getItemName() {
        return name;
    }

    @Override
    public boolean shouldRegister() {
        return true;
    }

    @Override
    public boolean shouldDisplayInTab() {
        return true;
    }

    @Override
    public ThaumicTinkererRecipe getRecipeItem() {
        return new PylonRecipe(new ItemStack(this),
                new PylonRecipeComponent(50000, new ItemStack(Blocks.DIAMOND_BLOCK)),

                new PylonRecipeComponent(50000, new ItemStack(Blocks.IRON_BLOCK)),

                new PylonRecipeComponent(50000, new ItemStack(Blocks.GOLD_BLOCK)),

                new PylonRecipeComponent(50000, new ItemStack(Blocks.REDSTONE_BLOCK)));
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean p_77624_4_) {
        super.addInformation(stack, player, list, p_77624_4_);
        if (stack.getTagCompound() == null) {
            stack.setTagCompound(new NBTTagCompound());
        }
        int[] tagList = stack.getTagCompound().getIntArray("fairyList");
        list.add(tagList.length + "/15");
        for (int i : tagList) {
            @SuppressWarnings("unchecked")
			Class<? extends EntityFairy> fairyClass = ItemFairyCharm.fairyClasses[i];
            list.add(ItemFairyCharm.getNameFromFairy(fairyClass));
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand){
        if (player.getHeldItem(hand).getTagCompound() == null) {
            player.getHeldItem(hand).setTagCompound(new NBTTagCompound());
        }
        if (!world.isRemote && player.isSneaking()) {
            int[] fairyCharms = player.getHeldItem(hand).getTagCompound().getIntArray("fairyList");
            Random random = new Random();
            for (int i : fairyCharms) {
                EntityItem item = new EntityItem(world, player.posX + random.nextDouble() - .5D, player.posY + random.nextDouble() - .5D, player.posZ + random.nextDouble() - .5D, new ItemStack(BlockRegistry.getFirstItemFromClass(ItemFairyCharm.class), 1, i));
                world.spawnEntity(item);
            }
            player.getHeldItem(hand).getTagCompound().setIntArray("fairyList", new int[0]);

        }
        return new ActionResult<ItemStack>(EnumActionResult.PASS, player.getHeldItem(hand));
    }

    @Override
    public int getCreativeTabPriority() {
        return -10;
    }
}
