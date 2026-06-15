package foxiwhitee.FoxLib.integration.crafttweaker.tooltips;

import foxiwhitee.FoxLib.client.tooltips.builder.InnerLineBuilder;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

/**
 * ZenScript data holding bridge for cycling inner lining configurations.
 */
@ZenClass("mods.foxlib.ZenInnerLine")
public final class ZenInnerLine {
    private final InnerLineBuilder internal;

    /**
     * Initializes a scripting tracking instance targeting core inner line builders.
     * @param internal Core structural engine processor node
     */
    public ZenInnerLine(InnerLineBuilder internal) {
        this.internal = internal;
    }

    /**
     * Outputs core engineering variables tracking inner line parameters.
     * @return Core native InnerLineBuilder configuration node
     */
    public InnerLineBuilder getInternal() {
        return this.internal;
    }

    /**
     * Registers tracking metrics driving color sequence array indexing ticks.
     * @param speed  The animation speed modifier float value determining translation shifts
     * @param colors Array sequence of colors (supports HEX strings or integers)
     * @return This wrapper instance for method chaining
     */
    @ZenMethod
    public ZenInnerLine data(float speed, String[] colors) {
        this.internal.data(speed, ZenTooltipManager.parseStringArray(colors));
        return this;
    }
}
