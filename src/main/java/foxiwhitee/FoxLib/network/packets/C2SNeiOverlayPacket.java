package foxiwhitee.FoxLib.network.packets;

import cpw.mods.fml.common.network.ByteBufUtils;
import foxiwhitee.FoxLib.api.IHasNeiOverlay;
import foxiwhitee.FoxLib.network.BasePacket;
import foxiwhitee.FoxLib.network.IInfoPacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;


public class C2SNeiOverlayPacket extends BasePacket {
    private final int xCoord, yCoord, zCoord;
    private final NBTTagCompound tag;

    public C2SNeiOverlayPacket(ByteBuf data) {
        super(data);
        xCoord = data.readInt();
        yCoord = data.readInt();
        zCoord = data.readInt();

        tag = ByteBufUtils.readTag(data);
    }

    public C2SNeiOverlayPacket(int xCoord, int yCoord, int zCoord, NBTTagCompound tag) {
        super();
        this.xCoord = xCoord;
        this.yCoord = yCoord;
        this.zCoord = zCoord;
        this.tag = tag;
        ByteBuf data = Unpooled.buffer();
        data.writeInt(getId());
        data.writeInt(xCoord);
        data.writeInt(yCoord);
        data.writeInt(zCoord);

        ByteBufUtils.writeTag(data, tag);

        setPacketData(data);
    }

    @Override
    public void handleServerSide(IInfoPacket network, BasePacket packet, EntityPlayer player) {
        if (player != null && player.worldObj != null) {
            TileEntity te = player.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord);
            if (te instanceof IHasNeiOverlay tile && tag != null) {
                tile.overlayRecipe(tag, player);
            }
        }
    }
}
