package foxiwhitee.FoxLib.api;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;

public interface IGuiBlockData {
    Class<? extends Block> getBlock();
    Class<? extends TileEntity> getTile();
    Class<? extends Container> getContainer();
    String getGui();
    Container constructContainer(EntityPlayer player, TileEntity te);
    Object constructGui(EntityPlayer player, TileEntity te);
}
