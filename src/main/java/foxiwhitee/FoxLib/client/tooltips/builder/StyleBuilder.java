package foxiwhitee.FoxLib.client.tooltips.builder;

import foxiwhitee.FoxLib.client.tooltips.theme.elements.frame.FrameStyle;

import java.awt.*;

/**
 * The class stores the primary colors for the theme in the tooltip
 */
@SuppressWarnings("all")
public final class StyleBuilder {
    private Color bgTop;
    private Color bgBottom;
    private Color borderOuter;
    private Color borderInner;
    private Color accent;
    private Color accentBright;
    private float bgAlpha;

    StyleBuilder() {}

    /**
     * Sets all values for tooltip style
     * @param bgTop Background color flowing smoothly into bgBottom
     * @param bgBottom Bottom Background Color
     * @param bgAlpha Background transparency. 0 to 1
     * @param borderOuter Outer border color
     * @param borderInner Inner border color
     * @param accent Main color effects
     * @param accentBright Lighter color effects
     * @return This
     */
    public StyleBuilder all(int bgTop, int bgBottom, float bgAlpha, int borderOuter, int borderInner, int accent, int accentBright) {
        this.bgTop = color(bgTop);
        this.bgBottom = color(bgBottom);
        this.borderOuter = color(borderOuter);
        this.borderInner = color(borderInner);
        this.accent = color(accent);
        this.accentBright = color(accentBright);
        this.bgAlpha = bgAlpha;
        return this;
    }

    /**
     * Sets the dataset for the background
     * @param bgTop Background color flowing smoothly into bgBottom
     * @param bgBottom Bottom Background Color
     * @param bgAlpha Background transparency. 0 to 1
     * @return This
     */
    public StyleBuilder bg(int bgTop, int bgBottom, float bgAlpha) {
        this.bgTop = color(bgTop);
        this.bgBottom = color(bgBottom);
        this.bgAlpha = bgAlpha;
        return this;
    }

    /**
     * Sets the dataset for the border
     * @param borderOuter Outer border color
     * @param borderInner Inner border color
     * @return This
     */
    public StyleBuilder border(int borderOuter, int borderInner) {
        this.borderOuter = color(borderOuter);
        this.borderInner = color(borderInner);
        return this;
    }

    /**
     * Sets the dataset for the effects
     * @param accent Main color effects
     * @param accentBright Lighter color effects
     * @return This
     */
    public StyleBuilder accent(int accent, int accentBright) {
        this.accent = color(accent);
        this.accentBright = color(accentBright);
        return this;
    }

    /**
     * Creates a color with an int value
     * @param color Hash value of color
     * @return Color Class
     */
    private Color color(int color) {
        return new Color(color | 0xFF000000, true);
    }

    Color getBgTop() {
        return bgTop == null ? color(0x00000000) : bgTop;
    }

    Color getBgBottom() {
        return bgBottom == null ? color(0x00000000) : bgBottom;
    }

    Color getBorderOuter() {
        return borderOuter == null ? color(0x00000000) : borderOuter;
    }

    Color getBorderInner() {
        return borderInner == null ? color(0x00000000) : borderInner;
    }

    Color getAccent() {
        return accent == null ? color(0x00000000) : accent;
    }

    Color getAccentBright() {
        return accentBright == null ? color(0x00000000) : accentBright;
    }

    float getBgAlpha() {
        return bgAlpha;
    }

    FrameStyle build() {
        return new FrameStyle(getBgTop(), getBgBottom(), getBorderOuter(), getBorderInner(), getAccent(), getAccentBright(), getBgAlpha());
    }
}
