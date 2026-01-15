package foxiwhitee.FoxLib.registries;

import foxiwhitee.FoxLib.api.registries.IPacketRegister;
import foxiwhitee.FoxLib.api.registries.IRegistries;

public class Registries implements IRegistries {
    private final IPacketRegister packetRegister = new PacketRegister();

    @Override
    public IPacketRegister registerPacket() {
        return packetRegister;
    }

}
