package foxiwhitee.FoxLib.client.tooltips.theme;

import foxiwhitee.FoxLib.client.tooltips.theme.elements.frame.FrameStyle;

import java.awt.*;

public final class ThemePalette {
    public final int borderBright;
    public final int borderDim;
    public final int accent;
    public final int decorA;
    public final int glow;

    private ThemePalette(int bright, int dim, int accent, int decorA, int glow) {
        this.borderBright = bright;
        this.borderDim = dim;
        this.accent = accent;
        this.decorA = decorA;
        this.glow = glow;
    }

    public static ThemePalette make(TooltipTheme theme, float pulse, float alphaMul) {
        FrameStyle s = theme.getStyle();

        int borderBright = withAlpha(s.borderOuter, 220 * alphaMul);
        int borderDim = withAlpha(s.borderInner, 175 * alphaMul);
        int accent = withAlpha(s.accent, 205 * alphaMul);
        int decorA = withAlpha(s.accentBright, 220 * alphaMul);
        float glowStrength = 70F + pulse * 50F;
        int glow = withAlpha(s.borderOuter, glowStrength * alphaMul);

        return new ThemePalette(borderBright, borderDim, accent, decorA, glow);
    }

    private static int withAlpha(Color c, float alphaScaled) {
        int a = (int) alphaScaled;
        if (a < 0) a = 0;
        if (a > 255) a = 255;
        return (a << 24) | (c.getRGB() & 0x00FFFFFF);
    }
}
