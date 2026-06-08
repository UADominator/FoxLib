package foxiwhitee.FoxLib.client.tooltips;

import net.minecraft.client.renderer.Tessellator;
import org.lwjgl.opengl.GL11;

public class TooltipDraw {
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

    public static float fract(float v) {
        return v - (float) Math.floor(v);
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
