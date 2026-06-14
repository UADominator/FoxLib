package foxiwhitee.FoxLib.integration.crafttweaker.tooltips;

import foxiwhitee.FoxLib.client.tooltips.builder.FrameBuilder;
import net.minecraft.util.ResourceLocation;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

/**
 * ZenScript data holding bridge for custom 9-patch asset framing overlays.
 */
@ZenClass("mods.foxlib.ZenFrame")
public final class ZenFrame {
    private final FrameBuilder internal;

    /**
     * Establishes a script interface wrapping frame processing operations.
     * @param internal Core processing frame component layout block
     */
    public ZenFrame(FrameBuilder internal) {
        this.internal = internal;
    }

    /**
     * Extracts primitive system blocks mapping border frame data.
     * @return Core native FrameBuilder registry controller
     */
    public FrameBuilder getInternal() {
        return this.internal;
    }

    /**
     * Configures the texture asset and dimensions for the tooltip background frame.
     * @param texturePath     The resource path string (e.g., "foxlib:textures/gui/frames/test.png")
     * @param width           The total layout width target parameter
     * @param realCenterWidth The calculated central stretching slice width boundary
     * @return This wrapper instance for method chaining
     */
    @ZenMethod
    public ZenFrame data(String texturePath, int width, int realCenterWidth) {
        if (texturePath == null || texturePath.isEmpty()) {
            this.internal.data(new ResourceLocation("minecraft", "textures/misc/unknown_pack.png"), width, realCenterWidth);
            return this;
        }

        this.internal.data(new ResourceLocation(texturePath), width, realCenterWidth);
        return this;
    }
}
