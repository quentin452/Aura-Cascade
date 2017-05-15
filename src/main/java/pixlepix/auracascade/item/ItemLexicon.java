package pixlepix.auracascade.item;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import pixlepix.auracascade.AuraCascade;
import pixlepix.auracascade.QuestManager;
import pixlepix.auracascade.lexicon.CategoryManager;
import pixlepix.auracascade.lexicon.ILexiconable;
import pixlepix.auracascade.lexicon.LexiconEntry;
import pixlepix.auracascade.lexicon.common.core.helper.ItemNBTHelper;
import pixlepix.auracascade.registry.BlockRegistry;
import pixlepix.auracascade.registry.CraftingBenchRecipe;
import pixlepix.auracascade.registry.ITTinkererItem;
import pixlepix.auracascade.registry.ThaumicTinkererRecipe;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pixlepix on 12/27/14.
 * *HEAVILY* based off of/stolen from Vazkii
 */
public class ItemLexicon extends Item implements ITTinkererItem {
    private static final String TAG_KNOWLEDGE_PREFIX = "knowledge.";
    private static final String TAG_FORCED_MESSAGE = "forcedMessage";
    private static final String TAG_QUEUE_TICKS = "queueTicks";

    private static final String name = "lexicon";

    boolean skipSound = false;

    public ItemLexicon() {
        super();
        setMaxStackSize(1);
    }

    public static void setForcedPage(ItemStack stack, String forced) {
        ItemNBTHelper.setString(stack, TAG_FORCED_MESSAGE, forced);
    }

    public static String getForcedPage(ItemStack stack) {
        return ItemNBTHelper.getString(stack, TAG_FORCED_MESSAGE, "");
    }

    private static LexiconEntry getEntryFromForce(ItemStack stack) {
        String force = getForcedPage(stack);
        for (LexiconEntry entry : CategoryManager.getAllEntries())
            if (entry.unlocalizedName.equals(force)) {
                return entry;
            }

        return null;
    }

    public static int getQueueTicks(ItemStack stack) {
        return ItemNBTHelper.getInt(stack, TAG_QUEUE_TICKS, 0);
    }

    public static void setQueueTicks(ItemStack stack, int ticks) {
        ItemNBTHelper.setInt(stack, TAG_QUEUE_TICKS, ticks);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (player.isSneaking()) {
            Block block = worldIn.getBlockState(pos).getBlock();
            if (block != null) {
                if (block instanceof ILexiconable) {
                    LexiconEntry entry = ((ILexiconable) block).getEntry(worldIn, pos, player, player.getHeldItem(hand));
                    if (entry != null) {
                        AuraCascade.proxy.setEntryToOpen(entry);
                        AuraCascade.proxy.setLexiconStack(player.getHeldItem(hand));

                        player.openGui(AuraCascade.instance, 0, worldIn, 0, 0, 0);
                        if (!worldIn.isRemote) {
                            //TODO fix sounds
                            //par3World.playSoundAtEntity(par2EntityPlayer, "aura:lexiconOpen", 0.5F, 1F);
                        }
                        return EnumActionResult.PASS;
                    }
                }
            }
        }
        return EnumActionResult.FAIL;
    }

    private void addStringToTooltip(String s, List<String> tooltip) {
        tooltip.add(s.replaceAll("&", "\u00a7"));
    }

    @Override
    public  ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand hand) {
        String force = getForcedPage(playerIn.getHeldItem(hand));
        if (force != null && !force.isEmpty()) {
            LexiconEntry entry = getEntryFromForce(playerIn.getHeldItem(hand));
            if (entry != null) {
                AuraCascade.proxy.setEntryToOpen(entry);
            } else {
                playerIn.sendStatusMessage(new TextComponentString("aura.misc.cantOpen"), false);
            }
            setForcedPage(playerIn.getHeldItem(hand), "");
        }

        QuestManager.check(playerIn);

        AuraCascade.proxy.setLexiconStack(playerIn.getHeldItem(hand));
        playerIn.openGui(AuraCascade.instance, 0, worldIn, 0, 0, 0);
        if (!worldIn.isRemote && !skipSound){
            //TODO Fix soundat
            //par2World.playSoundAt(par3EntityPlayer, "aura:lexiconOpen", 0.5F, 1F);
        }
        skipSound = false;
        return new ActionResult<ItemStack>(EnumActionResult.PASS, playerIn.getHeldItem(hand));
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int idk, boolean something) {
        int ticks = getQueueTicks(stack);
        if (ticks > 0 && entity instanceof EntityPlayer) {
            skipSound = ticks < 5;
            if (ticks == 1)
                onItemRightClick(world,(EntityPlayer) entity, EnumHand.MAIN_HAND);
            setQueueTicks(stack, ticks - 1);
        }
    }

    @Override
    public EnumRarity getRarity(ItemStack par1ItemStack) {
        return EnumRarity.UNCOMMON;
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
        return new CraftingBenchRecipe(new ItemStack(this), "CB", "  ", 'C', new ItemStack(BlockRegistry.getFirstItemFromClass(ItemAuraCrystal.class)), 'B', new ItemStack(Items.BOOK));
    }

    @Override
    public int getCreativeTabPriority() {
        return 150;
    }
}
