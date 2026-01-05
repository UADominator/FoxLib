package foxiwhitee.FoxLib.utils.helpers;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.Objects;

public class StackOreDict {
    private String ore;
    private int count;

    public StackOreDict(String ore, int count) {
        this.ore = ore;
        this.count = count;
    }

    public boolean check(ItemStack stack, boolean withCount) {
        boolean equals = false;
        for (ItemStack s : OreDictionary.getOres(ore)) {
            if (OreDictionary.itemMatches(s, stack, false))
                equals = true;
        }
        if (withCount) {
            return equals && count <= stack.stackSize;
        }
        return equals;
    }

    public String getOre() {
        return ore;
    }

    public void setOre(String ore) {
        this.ore = ore;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        StackOreDict that = (StackOreDict) o;
        return count == that.count && Objects.equals(ore, that.ore);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ore, count);
    }

    @Override
    public String toString() {
        return count + "::" + ore;
    }
}
