package foxiwhitee.FoxLib.items;

import appeng.me.helpers.IGridProxyable;
import cpw.mods.fml.common.Optional;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.util.List;

@SuppressWarnings("unused")
@Optional.InterfaceList({
    @Optional.Interface(modid = "appliedenergistics2", iface = "import appeng.me.helpers.IGridProxyable")
})
public class ModItemBlock extends ItemBlock {
    private final Block blockType;

    public ModItemBlock(Block b) {
        super(b);
        this.blockType = b;
    }

    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata) {
        if (!super.placeBlockAt(stack, player, world, x, y, z, side, hitX, hitY, hitZ, metadata)) {
            return false;
        }
        setAEOwner(x, y, z, world, player);
        return true;
    }

    @Optional.Method(modid = "appliedenergistics2")
    private void setAEOwner(int x, int y, int z, World world, EntityPlayer player) {
        if (this.blockType instanceof ITileEntityProvider) {
            TileEntity tile = world.getTileEntity(x, y, z);
            if (tile instanceof IGridProxyable proxyable) {
                proxyable.getProxy().setOwner(player);
            }
        }
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean b) {
        super.addInformation(stack, player, list, b);
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

