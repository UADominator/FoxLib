package foxiwhitee.FoxLib.utils.helpers;

import cpw.mods.fml.common.network.IGuiHandler;
import foxiwhitee.FoxLib.api.FoxLibApi;
import foxiwhitee.FoxLib.api.IGuiBlockData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class GuiHandler implements IGuiHandler {
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te == null) {
            return null;
        }
        IGuiBlockData data = FoxLibApi.instance.registries().registerGui().getById(ID);
        if (data == null) {
            return null;
        }
        return data.constructContainer(player, te);
    }

    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te == null) {
            return null;
        }
        IGuiBlockData data = FoxLibApi.instance.registries().registerGui().getById(ID);
        if (data == null) {
            return null;
        }
        return data.constructGui(player, te);
    }
}
