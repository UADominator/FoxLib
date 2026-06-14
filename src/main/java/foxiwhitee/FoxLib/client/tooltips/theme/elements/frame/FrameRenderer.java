package foxiwhitee.FoxLib.client.tooltips.theme.elements.frame;

import foxiwhitee.FoxLib.FoxLib;
import foxiwhitee.FoxLib.client.tooltips.theme.TooltipTheme;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public final class FrameRenderer {
    private static final long initTime = System.currentTimeMillis();
    private static final int TEX_SIZE = FrameDrawer.SIZE;
    private static final int TEX_BORDER = FrameDrawer.BORDER;
    private static final int RENDER_BORDER = 8;
    private static final int GLOW_OUTER_COLOR = 0x00000000;

    private static final Map<TooltipTheme, ResourceLocation> CACHE = new HashMap<>();

    private FrameRenderer() {
    }

    public static void drawNineSlice(TooltipTheme theme, float x, float y, float w, float h, float alpha) {
        ResourceLocation tex = getOrCreate(theme);
        if (tex == null) {
            return;
        }
        Minecraft.getMinecraft().getTextureManager().bindTexture(tex);

        GL11.glColor4f(1F, 1F, 1F, alpha);
        GL11.glEnable(GL11.GL_BLEND);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glDisable(GL11.GL_LIGHTING);

        float bw = Math.max(1F, w);
        float bh = Math.max(1F, h);
        float border = Math.min(RENDER_BORDER, Math.min(bw, bh) / 3F);
        if (border < 3F) {
            border = Math.min(RENDER_BORDER, Math.min(bw, bh) / 2F);
        }

        float ub = TEX_BORDER / (float) TEX_SIZE;
        float vb = TEX_BORDER / (float) TEX_SIZE;
        float uMid = (TEX_SIZE - TEX_BORDER) / (float) TEX_SIZE;
        float vMid = (TEX_SIZE - TEX_BORDER) / (float) TEX_SIZE;

        float bgAlpha = theme.getStyle().bgAlpha;

        Tessellator t = Tessellator.instance;
        GL11.glColor4f(1F, 1F, 1F, bgAlpha);
        t.startDrawingQuads();

        quad(t, x, y, bw, bh, ub, vb, uMid, vMid);

        t.draw();

        if (theme.isDrawBorder()) {
            GL11.glColor4f(1F, 1F, 1F, 1.0F);
            t.startDrawingQuads();

            quad(t, x, y, border, border, 0, 0, ub, vb);
            quad(t, x + bw - border, y, border, border, uMid, 0, 1, vb);
            quad(t, x, y + bh - border, border, border, 0, vMid, ub, 1);
            quad(t, x + bw - border, y + bh - border, border, border, uMid, vMid, 1, 1);

            quad(t, x + border, y, bw - border * 2, border, ub, 0, uMid, vb);
            quad(t, x + border, y + bh - border, bw - border * 2, border, ub, vMid, uMid, 1);
            quad(t, x, y + border, border, bh - border * 2, 0, vb, ub, vMid);
            quad(t, x + bw - border, y + border, border, bh - border * 2, uMid, vb, 1, vMid);

            t.draw();
        }
        GL11.glColor4f(1F, 1F, 1F, 1F);
    }

    private static void quad(Tessellator t, float x, float y, float w, float h, float u1, float v1, float u2, float v2) {
        t.addVertexWithUV(x, y + h, 0, u1, v2);
        t.addVertexWithUV(x + w, y + h, 0, u2, v2);
        t.addVertexWithUV(x + w, y, 0, u2, v1);
        t.addVertexWithUV(x, y, 0, u1, v1);
    }

    private static ResourceLocation getOrCreate(TooltipTheme theme) {
        ResourceLocation existing = CACHE.get(theme);
        if (existing != null) {
            return existing;
        }
        try {
            BufferedImage img = FrameDrawer.draw(theme);
            DynamicTexture dyn = new DynamicTexture(img);
            ResourceLocation rl = Minecraft.getMinecraft().getTextureManager()
                .getDynamicTextureLocation( FoxLib.MODID + "_frame_" + theme.getId().toLowerCase(), dyn);
            Minecraft.getMinecraft().getTextureManager().bindTexture(rl);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
            CACHE.put(theme, rl);
            return rl;
        } catch (Throwable t) {
            return null;
        }
    }

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

        int rx = x + 3;
        int ry = y + 3;
        int rWidth = width - 6;
        int rHeight = height - 6;
        int zLevel = 399;

        float lenTop = (float) rWidth;
        float lenRight = (float) rHeight;
        float lenBot = (float) rWidth;
        float lenLeft = (float) rHeight;
        float perimeter = lenTop + lenRight + lenBot + lenLeft;

        float shareTop = lenTop / perimeter;
        float shareRight = lenRight / perimeter;
        float shareBot = lenBot / perimeter;
        float shareLeft = lenLeft / perimeter;

        float startTop = 0.0F;
        float startRight = startTop + shareTop;
        float startBot = startRight + shareRight;
        float startLeft = startBot + shareBot;

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glShadeModel(GL11.GL_SMOOTH);

        Tessellator tess = Tessellator.instance;
        int step = 6;

        for (int px = rx; px < rx + rWidth; px += step) {
            int nextX = Math.min(px + step, rx + rWidth);
            float ratio1 = (float) (px - rx) / lenTop;
            float ratio2 = (float) (nextX - rx) / lenTop;
            float pct1 = startTop + (shareTop * ratio1);
            float pct2 = startTop + (shareTop * ratio2);
            int c1 = getAnimatedColor(progress, pct1, colors, maxSegments);
            int c2 = getAnimatedColor(progress, pct2, colors, maxSegments);

            tess.startDrawingQuads();
            setColor(tess, c1);
            tess.addVertex(px, ry, zLevel);
            setColor(tess, c1);
            tess.addVertex(px, ry + 1, zLevel);
            setColor(tess, c2);
            tess.addVertex(nextX, ry + 1, zLevel);
            setColor(tess, c2);
            tess.addVertex(nextX, ry, zLevel);
            tess.draw();
        }

        for (int py = ry; py < ry + rHeight; py += step) {
            int nextY = Math.min(py + step, ry + rHeight);
            float ratio1 = (float) (py - ry) / lenRight;
            float ratio2 = (float) (nextY - ry) / lenRight;
            float pct1 = startRight + (shareRight * ratio1);
            float pct2 = startRight + (shareRight * ratio2);
            int c1 = getAnimatedColor(progress, pct1, colors, maxSegments);
            int c2 = getAnimatedColor(progress, pct2, colors, maxSegments);

            tess.startDrawingQuads();
            setColor(tess, c1);
            tess.addVertex(rx + rWidth - 1, py, zLevel);
            setColor(tess, c2);
            tess.addVertex(rx + rWidth - 1, nextY, zLevel);
            setColor(tess, c2);
            tess.addVertex(rx + rWidth, nextY, zLevel);
            setColor(tess, c1);
            tess.addVertex(rx + rWidth, py, zLevel);
            tess.draw();
        }

        for (int px = rx; px < rx + rWidth; px += step) {
            int nextX = Math.min(px + step, rx + rWidth);
            float ratio1 = (float) (rx + rWidth - px) / lenBot;
            float ratio2 = (float) (rx + rWidth - nextX) / lenBot;
            float pct1 = startBot + (shareBot * ratio1);
            float pct2 = startBot + (shareBot * ratio2);
            int c1 = getAnimatedColor(progress, pct1, colors, maxSegments);
            int c2 = getAnimatedColor(progress, pct2, colors, maxSegments);

            tess.startDrawingQuads();
            setColor(tess, c1);
            tess.addVertex(px, ry + rHeight - 1, zLevel);
            setColor(tess, c1);
            tess.addVertex(px, ry + rHeight, zLevel);
            setColor(tess, c2);
            tess.addVertex(nextX, ry + rHeight, zLevel);
            setColor(tess, c2);
            tess.addVertex(nextX, ry + rHeight - 1, zLevel);
            tess.draw();
        }

        for (int py = ry; py < ry + rHeight; py += step) {
            int nextY = Math.min(py + step, ry + rHeight);
            float ratio1 = (float) (ry + rHeight - py) / lenLeft;
            float ratio2 = (float) (ry + rHeight - nextY) / lenLeft;
            float pct1 = startLeft + (shareLeft * ratio1);
            float pct2 = startLeft + (shareLeft * ratio2);
            int c1 = getAnimatedColor(progress, pct1, colors, maxSegments);
            int c2 = getAnimatedColor(progress, pct2, colors, maxSegments);

            tess.startDrawingQuads();
            setColor(tess, c1);
            tess.addVertex(rx, py, zLevel);
            setColor(tess, c2);
            tess.addVertex(rx, nextY, zLevel);
            setColor(tess, c2);
            tess.addVertex(rx + 1, nextY, zLevel);
            setColor(tess, c1);
            tess.addVertex(rx + 1, py, zLevel);
            tess.draw();
        }

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

    public static void drawGlowOutline(int x, int y, int width, int height, int glowSize, float animationSpeed, int[] colors) {
        float time = (float) (System.currentTimeMillis() - initTime) / 1000.0F * animationSpeed;
        float progress = time % 1.0F;

        int maxSegments = colors.length - 1;
        int zLevel = -1;
        int cOuter = GLOW_OUTER_COLOR;

        int ix2 = x + width;
        int iy2 = y + height;

        int ox1 = x - glowSize;
        int oy1 = y - glowSize;
        int ox2 = ix2 + glowSize;
        int oy2 = iy2 + glowSize;

        float lenTop = (float)(ix2 - x);
        float lenRight = (float)(iy2 - y);
        float lenBot = (float)(ix2 - x);
        float lenLeft = (float)(iy2 - y);

        float perimeter = lenTop + lenRight + lenBot + lenLeft;

        float shareTop = lenTop / perimeter;
        float shareRight = lenRight / perimeter;
        float shareBot = lenBot / perimeter;
        float shareLeft = lenLeft / perimeter;

        float startTop = 0.0F;
        float startRight = startTop + shareTop;
        float startBot = startRight + shareRight;
        float startLeft = startBot + shareBot;

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glShadeModel(GL11.GL_SMOOTH);

        Tessellator tess = Tessellator.instance;

        int step = 6;

        for (int px = x; px < ix2; px += step) {
            int nextX = Math.min(px + step, ix2);

            float ratio1 = (float)(px - x) / lenTop;
            float ratio2 = (float)(nextX - x) / lenTop;

            float pct1 = startTop + (shareTop * ratio1);
            float pct2 = startTop + (shareTop * ratio2);

            int c1 = getAnimatedColor(progress, pct1, colors, maxSegments);
            int c2 = getAnimatedColor(progress, pct2, colors, maxSegments);

            tess.startDrawingQuads();
            setColor(tess, c1);
            tess.addVertex(px, y, zLevel);
            setColor(tess, c2);
            tess.addVertex(nextX, y, zLevel);
            setColor(tess, cOuter);
            tess.addVertex(nextX, oy1, zLevel);
            setColor(tess, cOuter);
            tess.addVertex(px, oy1, zLevel);
            tess.draw();
        }

        for (int py = y; py < iy2; py += step) {
            int nextY = Math.min(py + step, iy2);

            float ratio1 = (float)(py - y) / lenRight;
            float ratio2 = (float)(nextY - y) / lenRight;

            float pct1 = startRight + (shareRight * ratio1);
            float pct2 = startRight + (shareRight * ratio2);

            int c1 = getAnimatedColor(progress, pct1, colors, maxSegments);
            int c2 = getAnimatedColor(progress, pct2, colors, maxSegments);

            tess.startDrawingQuads();
            setColor(tess, c1);
            tess.addVertex(ix2, py, zLevel);
            setColor(tess, c2);
            tess.addVertex(ix2, nextY, zLevel);
            setColor(tess, cOuter);
            tess.addVertex(ox2, nextY, zLevel);
            setColor(tess, cOuter);
            tess.addVertex(ox2, py, zLevel);
            tess.draw();
        }

        for (int px = x; px < ix2; px += step) {
            int nextX = Math.min(px + step, ix2);

            float ratio1 = (float)(ix2 - px) / lenBot;
            float ratio2 = (float)(ix2 - nextX) / lenBot;

            float pct1 = startBot + (shareBot * ratio1);
            float pct2 = startBot + (shareBot * ratio2);

            int c1 = getAnimatedColor(progress, pct1, colors, maxSegments);
            int c2 = getAnimatedColor(progress, pct2, colors, maxSegments);

            tess.startDrawingQuads();
            setColor(tess, c1);
            tess.addVertex(px, iy2, zLevel);
            setColor(tess, cOuter);
            tess.addVertex(px, oy2, zLevel);
            setColor(tess, cOuter);
            tess.addVertex(nextX, oy2, zLevel);
            setColor(tess, c2);
            tess.addVertex(nextX, iy2, zLevel);
            tess.draw();
        }

        for (int py = y; py < iy2; py += step) {
            int nextY = Math.min(py + step, iy2);

            float ratio1 = (float)(iy2 - py) / lenLeft;
            float ratio2 = (float)(iy2 - nextY) / lenLeft;

            float pct1 = startLeft + (shareLeft * ratio1);
            float pct2 = startLeft + (shareLeft * ratio2);

            int c1 = getAnimatedColor(progress, pct1, colors, maxSegments);
            int c2 = getAnimatedColor(progress, pct2, colors, maxSegments);

            tess.startDrawingQuads();
            setColor(tess, c1);
            tess.addVertex(x, py, zLevel);
            setColor(tess, cOuter);
            tess.addVertex(ox1, py, zLevel);
            setColor(tess, cOuter);
            tess.addVertex(ox1, nextY, zLevel);
            setColor(tess, c2);
            tess.addVertex(x, nextY, zLevel);
            tess.draw();
        }

        int cTopLeft = getAnimatedColor(progress, startTop, colors, maxSegments);
        int cTopRight = getAnimatedColor(progress, startRight, colors, maxSegments);
        int cBottomRight = getAnimatedColor(progress, startBot, colors, maxSegments);
        int cBottomLeft = getAnimatedColor(progress, startLeft, colors, maxSegments);

        tess.startDrawingQuads();
        setColor(tess, cTopLeft);
        tess.addVertex(x, y, zLevel);
        setColor(tess, cOuter);
        tess.addVertex(x, oy1, zLevel);
        setColor(tess, cOuter);
        tess.addVertex(ox1, oy1, zLevel);
        setColor(tess, cOuter);
        tess.addVertex(ox1, y, zLevel);
        tess.draw();

        tess.startDrawingQuads();
        setColor(tess, cTopRight);
        tess.addVertex(ix2, y, zLevel);
        setColor(tess, cOuter);
        tess.addVertex(ox2, y, zLevel);
        setColor(tess, cOuter);
        tess.addVertex(ox2, oy1, zLevel);
        setColor(tess, cOuter);
        tess.addVertex(ix2, oy1, zLevel);
        tess.draw();

        tess.startDrawingQuads();
        setColor(tess, cBottomRight);
        tess.addVertex(ix2, iy2, zLevel);
        setColor(tess, cOuter);
        tess.addVertex(ix2, oy2, zLevel);
        setColor(tess, cOuter);
        tess.addVertex(ox2, oy2, zLevel);
        setColor(tess, cOuter);
        tess.addVertex(ox2, iy2, zLevel);
        tess.draw();

        tess.startDrawingQuads();
        setColor(tess, cBottomLeft);
        tess.addVertex(x, iy2, zLevel);
        setColor(tess, cOuter);
        tess.addVertex(ox1, iy2, zLevel);
        setColor(tess, cOuter);
        tess.addVertex(ox1, oy2, zLevel);
        setColor(tess, cOuter);
        tess.addVertex(x, oy2, zLevel);
        tess.draw();

        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }
}
