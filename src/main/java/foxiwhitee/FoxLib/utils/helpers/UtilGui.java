package foxiwhitee.FoxLib.utils.helpers;

import foxiwhitee.FoxLib.client.gui.FoxBaseGui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import org.lwjgl.opengl.GL11;


@SuppressWarnings("unused")
public class UtilGui {
    public static double gauge(double pix, double ticks, double max) {
        double a = ticks * pix;
        return a / max;
    }

    public static void drawTexture(double x, double y, double u, double v, double width, double height,
                                   double uWidth, double uHeight) {
        drawTexture(x, y, u, v, width, height, uWidth, uHeight, 512, 512, 0, null, null, null);
    }

    public static void drawTexture(double x, double y, double u, double v, double width, double height,
                                   double uWidth, double uHeight, double textureWidth, double textureHeight) {
        drawTexture(x, y, u, v, width, height, uWidth, uHeight, textureWidth, textureHeight, 0, null, null, null);
    }

    public static void drawTexture(double x, double y, double u, double v, double width, double height,
                                   double uWidth, double uHeight, double textureWidth, double textureHeight, int z) {
        drawTexture(x, y, u, v, width, height, uWidth, uHeight, textureWidth, textureHeight, z, null, null, null);
    }

    public static void drawTexture(double x, double y, double u, double v, double width, double height,
                                   double uWidth, double uHeight, double textureWidth, double textureHeight,
                                   FoxBaseGui gui, String modid, String texture) {
        drawTexture(x, y, u, v, width, height, uWidth, uHeight, textureWidth, textureHeight, 0, gui, modid, texture);
    }

    public static void drawTexture(double x, double y, double u, double v, double width, double height,
                                   double uWidth, double uHeight, double textureWidth, double textureHeight, int z,
                                   FoxBaseGui gui, String modid, String texture) {
        if (gui != null && modid != null && texture != null) {
            gui.bindTexture(modid, texture);
        }
        double px = 1.0 / textureWidth;
        double py = 1.0 / textureHeight;

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);

        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();

        tessellator.addVertexWithUV(x, y + height, z, u * px, (v + uHeight) * py);
        tessellator.addVertexWithUV(x + width, y + height, z, (u + uWidth) * px, (v + uHeight) * py);
        tessellator.addVertexWithUV(x + width, y, z, (u + uWidth) * px, v * py);
        tessellator.addVertexWithUV(x, y, z, u * px, v * py);

        tessellator.draw();

        GL11.glDisable(GL11.GL_BLEND);
    }

    public static void drawAreaTooltip(int x, int y, String tooltip, int minX, int minY, int maxX, int maxY, boolean drawborder) {
        drawAreaTooltip(x, y, tooltip, minX, minY, maxX, maxY, 0, 0, drawborder);
    }

    public static void drawAreaTooltip(int x, int y, String tooltip, int minX, int minY, int maxX, int maxY) {
        drawAreaTooltip(x, y, tooltip, minX, minY, maxX, maxY, 0, 0, true);
    }

    public static void drawAreaTooltip(int x, int y, String tooltip, int minX, int minY, int maxX, int maxY, int yoffset, int xoffset) {
        drawAreaTooltip(x, y, tooltip, minX, minY, maxX, maxY, yoffset, xoffset, true);
    }

    public static void drawAreaTooltip(int x, int y, String tooltip, int minX, int minY, int maxX, int maxY, int yoffset, int xoffset, boolean drawborder) {
        if (x >= minX && x <= maxX && y >= minY && y <= maxY) {
            drawTooltip(x, y, yoffset, xoffset, tooltip, drawborder, 0);
        }

    }

    public static void drawTooltip(int x, int y, int yoffset, int xoffset, String tooltip, boolean drawborder, int width) {
        FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
        if (width == 0) {
            x -= fontRenderer.getStringWidth(tooltip) / 2;
        }

        y -= 12;
        x += xoffset;
        y += yoffset;
        y += 50;
        if (width == 0) {
            width = fontRenderer.getStringWidth(tooltip);
        }

        width += 8;
        int height = 8;
        int backgroundColor = 255;
        int borderColor = -1162167553;
        GL11.glPushAttrib(16704);
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(2929);
        GL11.glDisable(3553);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        drawRectangle(tessellator, x - 3, y - 4, x + width + 3, y - 3, backgroundColor);
        drawRectangle(tessellator, x - 3, y + height + 3, x + width + 3, y + height + 4, backgroundColor);
        drawRectangle(tessellator, x - 3, y - 3, x + width + 3, y + height + 3, backgroundColor);
        drawRectangle(tessellator, x - 4, y - 3, x - 3, y + height + 3, backgroundColor);
        drawRectangle(tessellator, x + width + 3, y - 3, x + width + 4, y + height + 3, backgroundColor);
        if (drawborder) {
            drawRectangle(tessellator, x - 3, y - 3 + 1, x - 3 + 1, y + height + 3 - 1, borderColor);
            drawRectangle(tessellator, x + width + 2, y - 3 + 1, x + width + 3, y + height + 3 - 1, borderColor);
            drawRectangle(tessellator, x - 3, y - 3, x + width + 3, y - 3 + 1, borderColor);
            drawRectangle(tessellator, x - 3, y + height + 2, x + width + 3, y + height + 3, borderColor);
        }

        tessellator.draw();
        GL11.glEnable(3553);
        fontRenderer.drawStringWithShadow(tooltip, x + 4, y, -2);
        GL11.glPopAttrib();
    }

    private static void drawRectangle(Tessellator tessellator, int x1, int y1, int x2, int y2, int color) {
        tessellator.setColorRGBA(color >>> 24 & 255, color >>> 16 & 255, color >>> 8 & 255, color & 255);
        tessellator.addVertex(x2, y1, 300.0F);
        tessellator.addVertex(x1, y1, 300.0F);
        tessellator.addVertex(x1, y2, 300.0F);
        tessellator.addVertex(x2, y2, 300.0F);
    }

}
