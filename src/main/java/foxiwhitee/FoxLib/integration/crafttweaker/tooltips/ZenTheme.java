package foxiwhitee.FoxLib.integration.crafttweaker.tooltips;

import foxiwhitee.FoxLib.client.tooltips.builder.ThemeBuilder;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

/**
 * ZenScript wrapper class for organizing and chaining tooltip properties together.
 */
@ZenClass("mods.foxlib.ZenTheme")
@SuppressWarnings("unused")
public final class ZenTheme {
    private final ThemeBuilder internal;

    /**
     * Initializes a standard ZenScript interface tracking a core ThemeBuilder block.
     * @param internal Core theme generation instance layer
     */
    public ZenTheme(ThemeBuilder internal) {
        this.internal = internal;
    }

    /**
     * Unwraps and returns the primitive underlying engine builder block.
     * @return Native internal ThemeBuilder module tracking settings
     */
    public ThemeBuilder getInternal() {
        return this.internal;
    }

    /**
     * Triggers item layout drawing passes injecting a item icon frame block inside the tooltip.
     * @return This builder wrapper instance driving continuous interface chaining
     */
    @ZenMethod
    public ZenTheme hasIcon() {
        this.internal.hasIcon();
        return this;
    }

    /**
     * Triggers soft ambient dropped backing shadows stretching behind layout windows.
     * @return This builder wrapper instance driving continuous interface chaining
     */
    @ZenMethod
    public ZenTheme enableShadow() {
        this.internal.enableShadow();
        return this;
    }

    /**
     * Instructs layout passes to draw a basic linear perimeter loop surrounding elements.
     * @return This builder wrapper instance driving continuous interface chaining
     */
    @ZenMethod
    public ZenTheme drawBorder() {
        this.internal.drawBorder();
        return this;
    }

    /**
     * Integrates base color parameters mapping palettes over background panels.
     * @param style Shell tracking targeted canvas rendering adjustments
     * @return This builder wrapper instance driving continuous interface chaining
     */
    @ZenMethod
    public ZenTheme appendStyle(ZenStyle style) {
        this.internal.appendStyle(style.getInternal());
        return this;
    }

    /**
     * Plugs an active drifting atmospheric matrix module into this localized theme canvas.
     * @param particles Shell tracking atmospheric emitter properties and limits
     * @return This builder wrapper instance driving continuous interface chaining
     */
    @ZenMethod
    public ZenTheme appendParticles(ZenParticles particles) {
        this.internal.appendParticles(particles.getInternal());
        return this;
    }

    /**
     * Appends horizontal line formatting bars slicing between stacked text layers.
     * @param separator Shell tracking indexed formatting bars
     * @return This builder wrapper instance driving continuous interface chaining
     */
    @ZenMethod
    public ZenTheme appendSeparator(ZenSeparator separator) {
        this.internal.appendSeparator(separator.getInternal());
        return this;
    }

    /**
     * Locks a 9-patch scaling texturized display border layout matrix around window boundaries.
     * @param frame Shell tracking frame sizing parameters and texture targets
     * @return This builder wrapper instance driving continuous interface chaining
     */
    @ZenMethod
    public ZenTheme appendFrame(ZenFrame frame) {
        this.internal.appendFrame(frame.getInternal());
        return this;
    }

    /**
     * Sets up secondary animated color cycling perimeter framing tracks tracing window edges.
     * @param innerLine Shell tracking interior layout track velocities and parameters
     * @return This builder wrapper instance driving continuous interface chaining
     */
    @ZenMethod
    public ZenTheme appendInnerLine(ZenInnerLine innerLine) {
        this.internal.appendInnerLine(innerLine.getInternal());
        return this;
    }

    /**
     * Affixes an outer volumetric lighting wash background module to the theme layout sheet.
     * @param glowOutline Shell tracking dimensional backlight glow limits and colors
     * @return This builder wrapper instance driving continuous interface chaining
     */
    @ZenMethod
    public ZenTheme appendGlowOutline(ZenGlowOutline glowOutline) {
        this.internal.appendGlowOutline(glowOutline.getInternal());
        return this;
    }
}
