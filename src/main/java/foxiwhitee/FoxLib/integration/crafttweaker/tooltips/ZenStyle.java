package foxiwhitee.FoxLib.integration.crafttweaker.tooltips;

import foxiwhitee.FoxLib.client.tooltips.builder.StyleBuilder;
import minetweaker.api.data.IData;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

/**
 * ZenScript data holding bridge for background and structural color setups.
 */
@ZenClass("mods.foxlib.ZenStyle")
@SuppressWarnings("unused")
public final class ZenStyle {
    private final StyleBuilder internal;

    /**
     * Allocates a script layout layer tracking native color setups.
     * @param internal Native underlying engine builder block
     */
    public ZenStyle(StyleBuilder internal) {
        this.internal = internal;
    }

    /**
     * Unwraps and outputs the core builder block tracking state colors.
     * @return Underlying StyleBuilder processing node
     */
    public StyleBuilder getInternal() {
        return this.internal;
    }

    /**
     * Configures all color parameters for the tooltip style frame simultaneously.
     * @param bgTop        The top gradient color of the background (HEX string or int)
     * @param bgBottom     The bottom gradient color of the background (HEX string or int)
     * @param bgAlpha      The opacity alpha multiplier float value ranging from 0.0 to 1.0
     * @param borderOuter  The color of the outer perimeter boundary line (HEX string or int)
     * @param borderInner  The color of the inner perimeter padding line (HEX string or int)
     * @param accent       The base primary highlight accent color (HEX string or int)
     * @param accentBright The vibrant secondary glow highlight accent color (HEX string or int)
     * @return This wrapper instance for method chaining
     */
    @ZenMethod
    public ZenStyle all(IData bgTop, IData bgBottom, float bgAlpha, IData borderOuter, IData borderInner, IData accent, IData accentBright) {
        this.internal.all(
            ZenTooltipManager.parseColor(bgTop),
            ZenTooltipManager.parseColor(bgBottom),
            bgAlpha,
            ZenTooltipManager.parseColor(borderOuter),
            ZenTooltipManager.parseColor(borderInner),
            ZenTooltipManager.parseColor(accent),
            ZenTooltipManager.parseColor(accentBright)
        );
        return this;
    }

    /**
     * Sets the background gradient colors and transparency.
     * @param bgTop    The top gradient color of the background (HEX string or int)
     * @param bgBottom The bottom gradient color of the background (HEX string or int)
     * @param bgAlpha  The opacity alpha multiplier float value ranging from 0.0 to 1.0
     * @return This wrapper instance for method chaining
     */
    @ZenMethod
    public ZenStyle bg(IData bgTop, IData bgBottom, float bgAlpha) {
        this.internal.bg(
            ZenTooltipManager.parseColor(bgTop),
            ZenTooltipManager.parseColor(bgBottom),
            bgAlpha
        );
        return this;
    }

    /**
     * Sets the outer and inner bounding border perimeter outline colors.
     * @param borderOuter The color of the outer perimeter boundary line (HEX string or int)
     * @param borderInner The color of the inner perimeter padding line (HEX string or int)
     * @return This wrapper instance for method chaining
     */
    @ZenMethod
    public ZenStyle border(IData borderOuter, IData borderInner) {
        this.internal.border(
            ZenTooltipManager.parseColor(borderOuter),
            ZenTooltipManager.parseColor(borderInner)
        );
        return this;
    }

    /**
     * Sets the decorative accent highlights applied to elements within the tooltip layout frame.
     * @param accent       The base primary highlight accent color (HEX string or int)
     * @param accentBright The vibrant secondary glow highlight accent color (HEX string or int)
     * @return This wrapper instance for method chaining
     */
    @ZenMethod
    public ZenStyle accent(IData accent, IData accentBright) {
        this.internal.accent(
            ZenTooltipManager.parseColor(accent),
            ZenTooltipManager.parseColor(accentBright)
        );
        return this;
    }
}
