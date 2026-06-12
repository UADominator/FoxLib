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

        int cTopLeft = getAnimatedColor(progress, 0.00F, colors, maxSegments);
        int cTopRight = getAnimatedColor(progress, 0.25F, colors, maxSegments);
        int cBottomRight = getAnimatedColor(progress, 0.50F, colors, maxSegments);
        int cBottomLeft = getAnimatedColor(progress, 0.75F, colors, maxSegments);

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
        setColor(tess, cTopLeft);
        tess.addVertex(rx, ry + 1, zLevel);
        setColor(tess, cTopRight);
        tess.addVertex(rx + rWidth, ry + 1, zLevel);
        setColor(tess, cTopRight);
        tess.addVertex(rx + rWidth, ry, zLevel);
        tess.draw();

        tess.startDrawingQuads();
        setColor(tess, cTopRight);
        tess.addVertex(rx + rWidth - 1, ry, zLevel);
        setColor(tess, cBottomRight);
        tess.addVertex(rx + rWidth - 1, ry + rHeight, zLevel);
        setColor(tess, cBottomRight);
        tess.addVertex(rx + rWidth, ry + rHeight, zLevel);
        setColor(tess, cTopRight);
        tess.addVertex(rx + rWidth, ry, zLevel);
        tess.draw();

        tess.startDrawingQuads();
        setColor(tess, cBottomLeft);
        tess.addVertex(rx, ry + rHeight - 1, zLevel);
        setColor(tess, cBottomLeft);
        tess.addVertex(rx, ry + rHeight, zLevel);
        setColor(tess, cBottomRight);
        tess.addVertex(rx + rWidth, ry + rHeight, zLevel);
        setColor(tess, cBottomRight);
        tess.addVertex(rx + rWidth, ry + rHeight - 1, zLevel);
        tess.draw();

        tess.startDrawingQuads();
        setColor(tess, cTopLeft);
        tess.addVertex(rx, ry, zLevel);
        setColor(tess, cBottomLeft);
        tess.addVertex(rx, ry + rHeight, zLevel);
        setColor(tess, cBottomLeft);
        tess.addVertex(rx + 1, ry + rHeight, zLevel);
        setColor(tess, cTopLeft);
        tess.addVertex(rx + 1, ry, zLevel);
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
}
