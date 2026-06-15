package foxiwhitee.FoxLib.client.tooltips.theme.elements.frame;

import java.awt.*;

@SuppressWarnings("all")
public class FrameStyle {
    public final Color bgTop;
    public final Color bgBottom;
    public final Color borderOuter;
    public final Color borderInner;
    public final Color accent;
    public final Color accentBright;
    public final float bgAlpha;

    public FrameStyle(int bgTop, int bgBottom, int borderOuter, int borderInner, int accent, int accentBright, float bgAlpha) {
        this.bgTop = color(bgTop);
        this.bgBottom = color(bgBottom);
        this.borderOuter = color(borderOuter);
        this.borderInner = color(borderInner);
        this.accent = color(accent);
        this.accentBright = color(accentBright);
        this.bgAlpha = bgAlpha;
    }

    public FrameStyle(Color bgTop, Color bgBottom, Color borderOuter, Color borderInner, Color accent, Color accentBright, float bgAlpha) {
        this.bgTop = bgTop;
        this.bgBottom = bgBottom;
        this.borderOuter = borderOuter;
        this.borderInner = borderInner;
        this.accent = accent;
        this.accentBright = accentBright;
        this.bgAlpha = bgAlpha;
    }

    private Color color(int color) {
        return new Color(color | 0xFF000000, true);
    }
}
