package foxiwhitee.FoxLib.registries;

import foxiwhitee.FoxLib.api.registries.IGuiBlockRegister;
import foxiwhitee.FoxLib.api.registries.IPacketRegister;
import foxiwhitee.FoxLib.api.registries.IRegistries;

public class Registries implements IRegistries {
    private final IPacketRegister packetRegister = new PacketRegister();
    private final IGuiBlockRegister guiBlockRegister = new GuiBlockRegister();

    @Override
    public IPacketRegister registerPacket() {
        return packetRegister;
    }

    @Override
    public IGuiBlockRegister registerGui() {
        return guiBlockRegister;
    }

}
