package foxiwhitee.FoxLib.registries;

import foxiwhitee.FoxLib.api.IGuiBlockData;
import foxiwhitee.FoxLib.api.registries.IGuiBlockRegister;
import foxiwhitee.FoxLib.utils.GuiBlockData;
import net.minecraft.block.Block;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;

import java.util.HashMap;
import java.util.Map;

public class GuiBlockRegister implements IGuiBlockRegister {
    private static int counter;
    private final Map<Integer, GuiBlockData> data = new HashMap<>();
    private final Map<Class<? extends Block>, Integer> ids = new HashMap<>();

    @Override
    public IGuiBlockRegister register(Class<? extends Block> block, Class<? extends TileEntity> tile, Class<? extends Container> container, String gui) {
        int id = counter++;
        data.put(id, new GuiBlockData(block, tile, container, gui));
        ids.put(block, id);
        return this;
    }

    @Override
    public IGuiBlockRegister register(Class<? extends Block> block, Class<? extends TileEntity> tile, Class<? extends Container> container) {
        return this.register(block, tile, container, container.getSimpleName().replace("Container", "Gui"));
    }

    @Override
    public IGuiBlockData getById(int id) {
        return data.get(id);
    }

    @Override
    public int getIdByBlock(Class<? extends Block> block) {
        return ids.get(block) == null ? -1 : ids.get(block);
    }
}
