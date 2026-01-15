package foxiwhitee.FoxLib.items;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public abstract class ModItemBlock extends ItemBlock {
    private final Block blockType;

    public ModItemBlock(Block b) {
        super(b);
        this.blockType = b;
    }

    public String getUnlocalizedName() {
        return this.blockType.getUnlocalizedName();
    }

    public String getUnlocalizedName(ItemStack i) {
        return this.blockType.getUnlocalizedName();
    }

    protected boolean isBlock(Block... blocks) {
        for (Block block : blocks) {
            if (blockType.equals(block)) {
                return true;
            }
        }
        return false;
    }
}

