package foxiwhitee.FoxLib.client.tooltips;

import foxiwhitee.FoxLib.client.tooltips.theme.TooltipTheme;

import java.util.ArrayList;
import java.util.List;

public final class TooltipState {

    private static final long STALE_GAP_MS = 250L;

    public long hoverStartMs;
    public long lastTouchedMs;
    public int lastSignature = Integer.MIN_VALUE;
    public TooltipTheme lastTheme;
    public final List<TooltipParticles.Particle> particles = new ArrayList<>();

    private static final TooltipState GLOBAL = new TooltipState();

    public static TooltipState get(long now, int signature, long appearDurationMs, TooltipTheme theme) {
        long sinceLastTouch = GLOBAL.lastTouchedMs > 0L ? (now - GLOBAL.lastTouchedMs) : Long.MAX_VALUE;
        boolean firstHover = GLOBAL.lastTouchedMs == 0L;
        boolean staleGap = !firstHover && sinceLastTouch > STALE_GAP_MS;
        boolean signatureChanged = signature != GLOBAL.lastSignature;
        boolean themeChanged = theme != GLOBAL.lastTheme;

        if (staleGap) {
            GLOBAL.particles.clear();
        }

        boolean reset = firstHover || staleGap;
        if (signatureChanged && !reset) {
            long elapsedOnPrev = now - GLOBAL.hoverStartMs;
            if (elapsedOnPrev >= appearDurationMs && themeChanged) {
                reset = true;
            }
        }

        if (reset) {
            GLOBAL.hoverStartMs = now;
        }
        GLOBAL.lastSignature = signature;
        GLOBAL.lastTheme = theme;
        GLOBAL.lastTouchedMs = now;
        return GLOBAL;
    }
}
