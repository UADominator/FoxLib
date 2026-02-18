package foxiwhitee.FoxLib.tile;

import foxiwhitee.FoxLib.api.orientable.FastOrientableManager;
import foxiwhitee.FoxLib.api.orientable.IOrientable;
import foxiwhitee.FoxLib.tile.event.TileEvent;
import foxiwhitee.FoxLib.tile.event.TileEventHandler;
import foxiwhitee.FoxLib.tile.event.TileEventType;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import java.lang.reflect.Method;
import java.util.*;

public class FoxBaseTile extends TileEntity implements IOrientable {
    private static final Map<Class<? extends FoxBaseTile>, Map<TileEventType, List<TileEventHandler>>> HANDLERS = new HashMap<>();
    private final int orientableId = FastOrientableManager.nextId();

    public FoxBaseTile() {
    }

    public final void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);

        if (this.canBeRotated()) {
            ForgeDirection f = ForgeDirection.getOrientation(data.getByte("f_fwd"));
            ForgeDirection u = ForgeDirection.getOrientation(data.getByte("f_up"));
            this.setOrientation(f, u);
        }

        for (TileEventHandler h : this.getHandlerListFor(TileEventType.SERVER_NBT_READ)) {
            h.readFromNBT(this, data);
        }
    }

    public final void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);

        if (this.canBeRotated()) {
            data.setByte("f_fwd", (byte) getForward().ordinal());
            data.setByte("f_up", (byte) getUp().ordinal());
        }

        for (TileEventHandler h : this.getHandlerListFor(TileEventType.SERVER_NBT_WRITE)) {
            h.writeToNBT(this, data);
        }
    }

    public final void updateEntity() {
        for(TileEventHandler h : this.getHandlerListFor(TileEventType.TICK)) {
            h.tick(this);
        }
    }

    private boolean readFromStream(ByteBuf data) {
        boolean output = false;
        try {
            if (this.canBeRotated()) {
                ForgeDirection oldForward = this.getForward(), oldUp = this.getUp();
                byte orientationForward = data.readByte(), orientationUp = data.readByte();
                ForgeDirection newForward = ForgeDirection.getOrientation(orientationForward & 7);
                ForgeDirection newUp = ForgeDirection.getOrientation(orientationUp & 7);
                this.setOrientation(newForward, newUp);
                output = newForward != oldForward || newUp != oldUp;
            }
            for (TileEventHandler h : this.getHandlerListFor(TileEventType.CLIENT_NBT_READ)) {
                if (h.readFromStream(this, data)) {
                    output = true;
                }
            }
        } catch (Throwable ignored) {}
        return output;
    }

    private void writeToStream(ByteBuf data) {
        try {
            if (this.canBeRotated()) {
                data.writeByte((byte) getForward().ordinal());
                data.writeByte((byte) getUp().ordinal());
            }
            for (TileEventHandler h : this.getHandlerListFor(TileEventType.CLIENT_NBT_WRITE)) {
                h.writeToStream(this, data);
            }
        } catch (Throwable ignored) {}
    }

    public void markForUpdate() {
        if (this.worldObj != null) {
            this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
            this.worldObj.markTileEntityChunkModified(this.xCoord, this.yCoord, this.zCoord, this);
        }
    }

    private List<TileEventHandler> getHandlerListFor(TileEventType type) {
        Map<TileEventType, List<TileEventHandler>> eventToHandlers = this.getEventToHandlers();
        return this.getHandlers(eventToHandlers, type);
    }

    private Map<TileEventType, List<TileEventHandler>> getEventToHandlers() {
        Class<? extends FoxBaseTile> clazz = this.getClass();
        Map<TileEventType, List<TileEventHandler>> storedHandlers = HANDLERS.get(clazz);
        if (storedHandlers == null) {
            Map<TileEventType, List<TileEventHandler>> newStoredHandlers = new EnumMap<>(TileEventType.class);
            HANDLERS.put(clazz, newStoredHandlers);

            for (Method method : clazz.getMethods()) {
                TileEvent event = method.getAnnotation(TileEvent.class);
                if (event != null) {
                    this.addHandler(newStoredHandlers, event.value(), method);
                }
            }

            return newStoredHandlers;
        } else {
            return storedHandlers;
        }
    }

    private List<TileEventHandler> getHandlers(Map<TileEventType, List<TileEventHandler>> eventToHandlers, TileEventType event) {
        List<TileEventHandler> oldHandlers = eventToHandlers.get(event);
        if (oldHandlers == null) {
            List<TileEventHandler> newHandlers = new LinkedList<>();
            eventToHandlers.put(event, newHandlers);
            return newHandlers;
        } else {
            return oldHandlers;
        }
    }

    private void addHandler(Map<TileEventType, List<TileEventHandler>> handlerSet, TileEventType value, Method m) {
        List<TileEventHandler> list = handlerSet.computeIfAbsent(value, (k) -> new ArrayList<>());
        list.add(new TileEventHandler(m));
    }

    public boolean canBeRotated() {
        return true;
    }

    @Override
    public ForgeDirection getForward() { return FastOrientableManager.getForward(orientableId); }

    @Override
    public ForgeDirection getUp() { return FastOrientableManager.getUp(orientableId); }

    @Override
    public void setOrientation(ForgeDirection forward, ForgeDirection up) {
        FastOrientableManager.set(orientableId, forward, up);
        this.markForUpdate();
        if (worldObj != null) worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, getBlockType());
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound data = new NBTTagCompound();
        ByteBuf stream = Unpooled.buffer();

        try {
            this.writeToStream(stream);
            if (stream.readableBytes() == 0) {
                return null;
            }
        } catch (Throwable ignored) {
        }

        stream.capacity(stream.readableBytes());
        data.setByteArray("X", stream.array());
        return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, getBlockMetadata(), data);
    }

    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        if (pkt.func_148853_f() == getBlockMetadata()) {
            ByteBuf stream = Unpooled.copiedBuffer(pkt.func_148857_g().getByteArray("X"));
            if (this.readFromStream(stream)) {
                this.markForUpdate();
            }
        }
    }
}
