package foxiwhitee.FoxLib.client.tooltips.theme.elements.frame;

import foxiwhitee.FoxLib.client.tooltips.theme.TooltipTheme;

import java.awt.*;
import java.awt.image.BufferedImage;

public final class FrameDrawer {
    public static final int SIZE = 128;
    public static final int BORDER = 16;

    private FrameDrawer() {
    }

    public static BufferedImage draw(TooltipTheme theme) {
        FrameStyle s = theme.getStyle();
        BufferedImage img = new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        applyHints(g);
        g.setComposite(AlphaComposite.Clear);
        g.fillRect(0, 0, SIZE, SIZE);
        g.setComposite(AlphaComposite.SrcOver);

        drawBackground(g, s);
        drawOuterBorder(g, s);

        g.dispose();
        return img;
    }

    private static void applyHints(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
    }

    private static void drawBackground(Graphics2D g, FrameStyle s) {
        Color softBottom = lerp(s.bgBottom, s.bgTop, 0.45F);
        g.setPaint(new GradientPaint(0, 0, s.bgTop, 0, SIZE, softBottom));
        g.fillRect(0, 0, SIZE, SIZE);

        Color innerHi = new Color(s.bgTop.getRed(), s.bgTop.getGreen(), s.bgTop.getBlue(), 50);
        g.setPaint(new RadialGradientPaint(SIZE * 0.5F, SIZE * 0.35F, SIZE * 0.6F,
            new float[] { 0F, 1F },
            new Color[] { innerHi, new Color(0, 0, 0, 0) }));
        g.fillRect(0, 0, SIZE, SIZE);
    }

    private static void drawOuterBorder(Graphics2D g, FrameStyle s) {
        Color outer = s.borderOuter;
        Color outerDim = darken(outer, 0.78F);
        Color highlight = brighten(outer, 0.55F);

        g.setColor(outer);
        g.fillRect(0, 0, SIZE, 4);
        g.fillRect(0, 0, 4, SIZE);

        g.setColor(outerDim);
        g.fillRect(0, SIZE - 4, SIZE, 4);
        g.fillRect(SIZE - 4, 0, 4, SIZE);

        g.setColor(highlight);
        g.fillRect(0, 0, SIZE, 1);
        g.fillRect(0, 0, 1, SIZE);

        g.setColor(darken(outerDim, 0.85F));
        g.fillRect(0, SIZE - 1, SIZE, 1);
        g.fillRect(SIZE - 1, 0, 1, SIZE);
    }

    @SuppressWarnings("all")
    private static Color lerp(Color a, Color b, float t) {
        if (t < 0F) {
            t = 0F;
        }
        if (t > 1F) {
            t = 1F;
        }
        int r = (int) (a.getRed() + (b.getRed() - a.getRed()) * t);
        int gg = (int) (a.getGreen() + (b.getGreen() - a.getGreen()) * t);
        int bl = (int) (a.getBlue() + (b.getBlue() - a.getBlue())  * t);
        int al = (int) (a.getAlpha() + (b.getAlpha() - a.getAlpha()) * t);
        return new Color(clamp(r), clamp(gg), clamp(bl), clamp(al));
    }

    private static Color darken(Color c, float factor) {
        int r = (int) (c.getRed() * factor);
        int gg = (int) (c.getGreen() * factor);
        int b = (int) (c.getBlue() * factor);
        return new Color(clamp(r), clamp(gg), clamp(b), c.getAlpha());
    }

    @SuppressWarnings("all")
    private static Color brighten(Color c, float amount) {
        int r = (int) (c.getRed() + (255 - c.getRed()) * amount);
        int gg = (int) (c.getGreen() + (255 - c.getGreen()) * amount);
        int b = (int) (c.getBlue() + (255 - c.getBlue()) * amount);
        return new Color(clamp(r), clamp(gg), clamp(b), c.getAlpha());
    }

    private static int clamp(int v) {
        return v < 0 ? 0 : Math.min(v, 255);
    }
}
