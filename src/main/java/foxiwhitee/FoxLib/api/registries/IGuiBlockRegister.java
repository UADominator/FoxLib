package foxiwhitee.FoxLib.api.registries;

import foxiwhitee.FoxLib.api.IGuiBlockData;
import net.minecraft.block.Block;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;

public interface IGuiBlockRegister {
    IGuiBlockRegister register(Class<? extends Block> block, Class<? extends TileEntity> tile, Class<? extends Container> container, String gui);
    IGuiBlockRegister register(Class<? extends Block> block, Class<? extends TileEntity> tile, Class<? extends Container> container);
    IGuiBlockData getById(int id);
    int getIdByBlock(Class<? extends Block> block);
}
