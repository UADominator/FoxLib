package foxiwhitee.FoxLib.client.tooltips.theme.elements.object;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.Objects;

public class TooltipObjectKey {
    private final Item item;
    private final int damage;
    private final NBTTagCompound nbt;
    private final String text;

    public TooltipObjectKey(ItemStack stack) {
        this.item = stack.getItem();
        this.damage = stack.getItemDamage();
        this.nbt = stack.hasTagCompound() ? (NBTTagCompound) stack.getTagCompound().copy() : null;
        this.text = "";
    }

    public TooltipObjectKey(String text) {
        this.item = null;
        this.damage = -1;
        this.nbt = null;
        this.text = text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TooltipObjectKey createKey = (TooltipObjectKey) o;
        return damage == createKey.damage &&
            Objects.equals(item, createKey.item) &&
            Objects.equals(nbt, createKey.nbt) &&
            Objects.equals(text, createKey.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(item, damage, nbt, text);
    }
}
