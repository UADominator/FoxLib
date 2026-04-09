package foxiwhitee.FoxLib.integration.applied.tile;

import appeng.tile.TileEvent;
import appeng.tile.events.TileEventType;
import appeng.tile.grid.AENetworkTile;
import foxiwhitee.FoxLib.api.orientable.FastOrientableManager;
import foxiwhitee.FoxLib.api.orientable.IOrientable;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

public abstract class TileNetworkOrientable extends AENetworkTile implements IOrientable {
    private final int orientableId = FastOrientableManager.nextId();

    @TileEvent(TileEventType.WORLD_NBT_WRITE)
    public void writeToNbt_(NBTTagCompound data) {
        if (this.canBeRotated()) {
            data.setByte("f_fwd", (byte)this.getForward().ordinal());
            data.setByte("f_up", (byte)this.getUp().ordinal());
        }
    }

    @TileEvent(TileEventType.WORLD_NBT_READ)
    public void readFromNbt_(NBTTagCompound data) {
        if (this.canBeRotated()) {
            ForgeDirection f = ForgeDirection.getOrientation(data.getByte("f_fwd"));
            ForgeDirection u = ForgeDirection.getOrientation(data.getByte("f_up"));
            this.setOrientation(f, u);
        }
    }

    @TileEvent(TileEventType.NETWORK_WRITE)
    @SuppressWarnings("unused")
    public void writeToStream(ByteBuf data) {
        if (this.canBeRotated()) {
            data.writeByte((byte)this.getForward().ordinal());
            data.writeByte((byte)this.getUp().ordinal());
        }
    }

    @TileEvent(TileEventType.NETWORK_READ)
    @SuppressWarnings("unused")
    public boolean readFromStream(ByteBuf data) {
        if (this.canBeRotated()) {
            ForgeDirection oldForward = this.getForward();
            ForgeDirection oldUp = this.getUp();
            byte orientationForward = data.readByte();
            byte orientationUp = data.readByte();
            ForgeDirection newForward = ForgeDirection.getOrientation(orientationForward & 7);
            ForgeDirection newUp = ForgeDirection.getOrientation(orientationUp & 7);
            this.setOrientation(newForward, newUp);
            return newForward != oldForward || newUp != oldUp;
        }
        return false;
    }

    public boolean canBeRotated() {
        return true;
    }

    public ForgeDirection getForward() {
        return FastOrientableManager.getForward(this.orientableId);
    }

    public ForgeDirection getUp() {
        return FastOrientableManager.getUp(this.orientableId);
    }

    public void setOrientation(ForgeDirection forward, ForgeDirection up) {
        FastOrientableManager.set(this.orientableId, forward, up);
        this.markForUpdate();
        if (this.worldObj != null) {
            this.worldObj.notifyBlocksOfNeighborChange(this.xCoord, this.yCoord, this.zCoord, this.getBlockType());
        }

    }

    protected abstract ItemStack getItemFromTile(Object obj);
}
