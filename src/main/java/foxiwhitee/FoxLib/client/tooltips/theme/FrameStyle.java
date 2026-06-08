package foxiwhitee.FoxLib.client.tooltips.theme;

import java.awt.*;

public class FrameStyle {
    public final Color bgTop;
    public final Color bgBottom;
    public final Color borderOuter;
    public final Color borderInner;
    public final Color accent;
    public final Color accentDark;
    public final Color accentBright;
    public final Color gold;
    public final Color goldHi;

    public FrameStyle(int bgTop, int bgBottom, int borderOuter, int borderInner, int accent, int accentDark, int accentBright, int gold, int goldHi) {
        this.bgTop = new Color(bgTop | 0xFF000000, true);
        this.bgBottom = new Color(bgBottom | 0xFF000000, true);
        this.borderOuter = new Color(borderOuter | 0xFF000000, true);
        this.borderInner = new Color(borderInner | 0xFF000000, true);
        this.accent = new Color(accent | 0xFF000000, true);
        this.accentDark = new Color(accentDark | 0xFF000000, true);
        this.accentBright = new Color(accentBright | 0xFF000000, true);
        this.gold = new Color(gold | 0xFF000000, true);
        this.goldHi = new Color(goldHi | 0xFF000000, true);
    }
}
