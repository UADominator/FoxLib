package foxiwhitee.FoxLib.integration.crafttweaker.tooltips;

import foxiwhitee.FoxLib.client.tooltips.builder.GlowOutlineBuilder;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

/**
 * ZenScript data holding bridge for atmospheric ambient outer back-glow arrays.
 */
@ZenClass("mods.foxlib.ZenGlowOutline")
public final class ZenGlowOutline {
    private final GlowOutlineBuilder internal;

    /**
     * Prepares scripting data boundaries handling backglow maps.
     * @param internal Native processing system segment layer
     */
    public ZenGlowOutline(GlowOutlineBuilder internal) {
        this.internal = internal;
    }

    /**
     * Pulls out underlying engine builder items mapping ambient glow.
     * @return Core processing GlowOutlineBuilder engine component
     */
    public GlowOutlineBuilder getInternal() {
        return this.internal;
    }

    /**
     * Submits thickness dimensions and execution speeds into the ambient light display engine.
     * @param glowSize       Absolute thickness pixel width boundary tracking generated backlight steps
     * @param animationSpeed Floating scale index modifying color rotation velocities
     * @param colors Array sequence of gradient colors (supports HEX strings or integers)
     * @return This processing container wrapper instance for fluent script chaining
     */
    @ZenMethod
    public ZenGlowOutline data(int glowSize, float animationSpeed, String[] colors) {
        this.internal.data(glowSize, animationSpeed, ZenTooltipManager.parseStringArray(colors));
        return this;
    }
}
