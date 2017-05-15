package pixlepix.auracascade.data;

import net.minecraft.item.ItemStack;

/**
 * Created by pixlepix on 1/24/15.
 * Wraps an itemstack and provides a sane hash code function
 */
public class ItemStackMapEntry {

    public ItemStack stack;

    public ItemStackMapEntry(ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public int hashCode() {
        if (stack == ItemStack.EMPTY || stack.getItem() == ItemStack.EMPTY.getItem()) {
            return -43532987;

        }
        return stack.getItem().getUnlocalizedName().hashCode() * -2134 + stack.getCount() * 3245879 + stack.getItemDamage() * -234569 + (stack.getTagCompound() != null ? stack.getTagCompound().hashCode() * 2345798 : 0);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ItemStackMapEntry && ((stack == ItemStack.EMPTY && ((ItemStackMapEntry) obj).stack == ItemStack.EMPTY) || ((ItemStackMapEntry) obj).stack.isItemEqual(stack));
    }
}
