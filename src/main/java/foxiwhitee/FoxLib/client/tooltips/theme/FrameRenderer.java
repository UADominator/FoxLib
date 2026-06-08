package foxiwhitee.FoxLib.client.tooltips.theme;

import foxiwhitee.FoxLib.FoxLib;
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

        Tessellator t = Tessellator.instance;
        t.startDrawingQuads();
        quad(t, x, y, border, border, 0, 0, ub, vb);
        quad(t, x + bw - border, y, border, border, uMid, 0, 1, vb);
        quad(t, x, y + bh - border, border, border, 0, vMid, ub, 1);
        quad(t, x + bw - border, y + bh - border, border, border, uMid, vMid, 1, 1);
        quad(t, x + border, y, bw - border * 2, border, ub, 0, uMid, vb);
        quad(t, x + border, y + bh - border, bw - border * 2, border, ub, vMid, uMid, 1);
        quad(t, x, y + border, border, bh - border * 2, 0, vb, ub, vMid);
        quad(t, x + bw - border, y + border, border, bh - border * 2, uMid, vb, 1, vMid);
        quad(t, x + border, y + border, bw - border * 2, bh - border * 2, ub, vb, uMid, vMid);
        t.draw();

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
}
