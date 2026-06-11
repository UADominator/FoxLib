package foxiwhitee.FoxLib.client.tooltips;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import org.lwjgl.opengl.GL11;

public class TooltipDraw {
    private static final long initTime = System.currentTimeMillis();

    private static int interpolateColor(int color1, int color2, float ratio) {
        int a1 = (color1 >> 24) & 255;
        int r1 = (color1 >> 16) & 255;
        int g1 = (color1 >> 8) & 255;
        int b1 = color1 & 255;

        int a2 = (color2 >> 24) & 255;
        int r2 = (color2 >> 16) & 255;
        int g2 = (color2 >> 8) & 255;
        int b2 = color2 & 255;

        int a = (int) (a1 + (a2 - a1) * ratio);
        int r = (int) (r1 + (r2 - r1) * ratio);
        int g = (int) (g1 + (g2 - g1) * ratio);
        int b = (int) (b1 + (b2 - b1) * ratio);

        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    public static void drawInnerLine(int x, int y, int width, int height, float animationSpeed, int[] colors) {
        float time = (float) (System.currentTimeMillis() - initTime) / 1000.0F * animationSpeed;
        float progress = time % 1.0F;

        int maxSegments = colors.length - 1;

        int cTopLeft =     getAnimatedColor(progress, 0.00F, colors, maxSegments);
        int cTopRight =    getAnimatedColor(progress, 0.25F, colors, maxSegments);
        int cBottomRight = getAnimatedColor(progress, 0.50F, colors, maxSegments);
        int cBottomLeft =  getAnimatedColor(progress, 0.75F, colors, maxSegments);

        int rx = x + 3;
        int ry = y + 3;
        int rWidth = width - 6;
        int rHeight = height - 6;

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glShadeModel(GL11.GL_SMOOTH);

        Tessellator tess = Tessellator.instance;

        tess.startDrawingQuads();
        setColor(tess, cTopLeft);
        int zLevel = 399;
        tess.addVertex(rx, ry, zLevel);
        setColor(tess, cTopLeft);   tess.addVertex(rx, ry + 1, zLevel);
        setColor(tess, cTopRight);  tess.addVertex(rx + rWidth, ry + 1, zLevel);
        setColor(tess, cTopRight);  tess.addVertex(rx + rWidth, ry, zLevel);
        tess.draw();

        tess.startDrawingQuads();
        setColor(tess, cTopRight);    tess.addVertex(rx + rWidth - 1, ry, zLevel);
        setColor(tess, cTopRight);    tess.addVertex(rx + rWidth - 1, ry + rHeight, zLevel);
        setColor(tess, cBottomRight); tess.addVertex(rx + rWidth, ry + rHeight, zLevel);
        setColor(tess, cBottomRight); tess.addVertex(rx + rWidth, ry, zLevel);
        tess.draw();

        tess.startDrawingQuads();
        setColor(tess, cBottomLeft);  tess.addVertex(rx, ry + rHeight - 1, zLevel);
        setColor(tess, cBottomLeft);  tess.addVertex(rx, ry + rHeight, zLevel);
        setColor(tess, cBottomRight); tess.addVertex(rx + rWidth, ry + rHeight, zLevel);
        setColor(tess, cBottomRight); tess.addVertex(rx + rWidth, ry + rHeight - 1, zLevel);
        tess.draw();

        tess.startDrawingQuads();
        setColor(tess, cTopLeft);    tess.addVertex(rx, ry, zLevel);
        setColor(tess, cTopLeft);    tess.addVertex(rx, ry + rHeight, zLevel);
        setColor(tess, cBottomLeft); tess.addVertex(rx + 1, ry + rHeight, zLevel);
        setColor(tess, cBottomLeft); tess.addVertex(rx + 1, ry, zLevel);
        tess.draw();

        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    private static int getAnimatedColor(float globalProgress, float phaseOffset, int[] colors, int maxSegments) {
        float localProgress = (globalProgress + phaseOffset) % 1.0F;
        float segmentPosition = localProgress * maxSegments;
        int index = (int) segmentPosition;
        float ratio = segmentPosition - index;

        return interpolateColor(colors[index], colors[index + 1], ratio);
    }

    private static void setColor(Tessellator tess, int color) {
        float a = (float) (color >> 24 & 255) / 255.0F;
        float r = (float) (color >> 16 & 255) / 255.0F;
        float g = (float) (color >> 8 & 255) / 255.0F;
        float b = (float) (color & 255) / 255.0F;
        tess.setColorRGBA_F(r, g, b, a);
    }

    public static float clamp01(float v) {
        return v < 0.0F ? 0.0F : (Math.min(v, 1.0F));
    }

    public static float smootherStep(float v) {
        float t = clamp01(v);
        return t * t * t * (t * (t * 6.0F - 15.0F) + 10.0F);
    }

    public static float easeOutCubic(float v) {
        float t = clamp01(v);
        float u = 1.0F - t;
        return 1.0F - u * u * u;
    }

    public static int scaleAlpha(int color, float factor) {
        int a = (color >>> 24) & 255;
        int na = clampInt((int) (a * factor), 0, 255);
        return (color & 0x00FFFFFF) | (na << 24);
    }

    public static int clampInt(int v, int min, int max) {
        if (v < min) {
            return min;
        }
        return Math.min(v, max);
    }

    public static void rect(double left, double top, double right, double bottom, int color) {
        if (left == right || top == bottom) {
            return;
        }
        if (left > right) {
            double tmp = left; left = right; right = tmp;
        }
        if (top > bottom) {
            double tmp = top; top = bottom; bottom = tmp;
        }

        float a = ((color >> 24) & 255) / 255.0F;
        float r = ((color >> 16) & 255) / 255.0F;
        float g = ((color >> 8) & 255) / 255.0F;
        float b = (color & 255) / 255.0F;

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        Tessellator t = Tessellator.instance;
        t.startDrawingQuads();
        t.setColorRGBA_F(r, g, b, a);
        t.addVertex(left, bottom, 0);
        t.addVertex(right, bottom, 0);
        t.addVertex(right, top, 0);
        t.addVertex(left, top, 0);
        t.draw();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    public static void thickLine(double x1, double y1, double x2, double y2, double thickness, int color) {
        double dx = x2 - x1;
        double dy = y2 - y1;
        double len = Math.sqrt(dx * dx + dy * dy);
        if (len < 0.001D) {
            return;
        }
        double nx = -dy / len * thickness * 0.5D;
        double ny = dx / len * thickness * 0.5D;

        float a = ((color >> 24) & 255) / 255.0F;
        float r = ((color >> 16) & 255) / 255.0F;
        float g = ((color >> 8) & 255) / 255.0F;
        float b = (color & 255) / 255.0F;

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        Tessellator t = Tessellator.instance;
        t.startDrawingQuads();
        t.setColorRGBA_F(r, g, b, a);
        t.addVertex(x1 + nx, y1 + ny, 0);
        t.addVertex(x2 + nx, y2 + ny, 0);
        t.addVertex(x2 - nx, y2 - ny, 0);
        t.addVertex(x1 - nx, y1 - ny, 0);
        t.draw();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    public static void diamond(double cx, double cy, double size, int color) {
        float a = ((color >> 24) & 255) / 255.0F;
        float r = ((color >> 16) & 255) / 255.0F;
        float g = ((color >> 8) & 255) / 255.0F;
        float b = (color & 255) / 255.0F;

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        Tessellator t = Tessellator.instance;
        t.startDrawingQuads();
        t.setColorRGBA_F(r, g, b, a);
        t.addVertex(cx, cy - size, 0);
        t.addVertex(cx + size, cy, 0);
        t.addVertex(cx, cy + size, 0);
        t.addVertex(cx - size, cy, 0);
        t.draw();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    public static void circle(double cx, double cy, double radius, int color) {
        float a = ((color >> 24) & 255) / 255.0F;
        float r = ((color >> 16) & 255) / 255.0F;
        float g = ((color >> 8) & 255) / 255.0F;
        float b = (color & 255) / 255.0F;

        int slices = 16;
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        Tessellator t = Tessellator.instance;
        t.startDrawing(GL11.GL_TRIANGLE_FAN);
        t.setColorRGBA_F(r, g, b, a);
        t.addVertex(cx, cy, 0);
        for (int i = 0; i <= slices; i++) {
            double ang = i * Math.PI * 2.0D / slices;
            t.addVertex(cx + Math.cos(ang) * radius, cy + Math.sin(ang) * radius, 0);
        }
        t.draw();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    public static int lerpColor(int a, int b, float t) {
        float u = clamp01(t);
        int aa = (a >>> 24) & 255;
        int ar = (a >> 16) & 255;
        int ag = (a >> 8) & 255;
        int ab = a & 255;
        int ba = (b >>> 24) & 255;
        int br = (b >> 16) & 255;
        int bg = (b >> 8) & 255;
        int bb = b & 255;

        int oa = (int) (aa + (ba - aa) * u);
        int or = (int) (ar + (br - ar) * u);
        int og = (int) (ag + (bg - ag) * u);
        int ob = (int) (ab + (bb - ab) * u);
        return (oa << 24) | (or << 16) | (og << 8) | ob;
    }
}
