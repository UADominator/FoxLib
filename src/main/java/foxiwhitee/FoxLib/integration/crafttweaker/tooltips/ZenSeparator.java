package foxiwhitee.FoxLib.integration.crafttweaker.tooltips;

import foxiwhitee.FoxLib.client.tooltips.builder.SeparatorBuilder;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

/**
 * ZenScript data holding bridge for tooltip line splitter allocations.
 */
@ZenClass("mods.foxlib.ZenSeparator")
@SuppressWarnings("unused")
public final class ZenSeparator {
    private final SeparatorBuilder internal;

    /**
     * Prepares script model layout handling separator setups.
     * @param internal Native processing model layer
     */
    public ZenSeparator(SeparatorBuilder internal) {
        this.internal = internal;
    }

    /**
     * Extracts raw builder modules processing separator maps.
     * @return Native underlying processing SeparatorBuilder object
     */
    public SeparatorBuilder getInternal() {
        return this.internal;
    }

    /**
     * Binds a global color configuration identity list tracking divider layout arrays.
     * @param index  The layout layer z-level index slot position order specification
     * @param colors Array sequence of partition layer colors (supports HEX strings or integers)
     * @return This wrapper instance for method chaining
     */
    @ZenMethod
    public ZenSeparator applySeparator(int index, String[] colors) {
        this.internal.applySeparator(index, ZenTooltipManager.parseStringArray(colors));
        return this;
    }
}
