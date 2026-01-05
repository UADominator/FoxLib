package foxiwhitee.FoxLib.tile.event;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import foxiwhitee.FoxLib.tile.FoxBaseTile;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class TileEventHandler {
    private final Method method;

    public TileEventHandler(Method method) {
        this.method = method;
    }

    public void tick(FoxBaseTile tile) {
        try {
            this.method.invoke(tile);
        } catch (InvocationTargetException | IllegalArgumentException | IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    public void writeToNBT(FoxBaseTile tile, NBTTagCompound data) {
        try {
            this.method.invoke(tile, data);
        } catch (InvocationTargetException | IllegalArgumentException | IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    public void readFromNBT(FoxBaseTile tile, NBTTagCompound data) {
        try {
            this.method.invoke(tile, data);
        } catch (InvocationTargetException | IllegalArgumentException | IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    public void writeToStream(FoxBaseTile tile, ByteBuf data) {
        try {
            this.method.invoke(tile, data);
        } catch (InvocationTargetException | IllegalArgumentException | IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    @SideOnly(Side.CLIENT)
    public boolean readFromStream(FoxBaseTile tile, ByteBuf data) {
        try {
            return (Boolean)this.method.invoke(tile, data);
        } catch (InvocationTargetException | IllegalArgumentException | IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }
}
