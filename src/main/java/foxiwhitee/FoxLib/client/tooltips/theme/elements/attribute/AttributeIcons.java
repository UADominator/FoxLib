package foxiwhitee.FoxLib.client.tooltips.theme.elements.attribute;

import foxiwhitee.FoxLib.client.tooltips.theme.elements.attribute.AttributeInfo.Kind;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.EnumMap;
import java.util.Map;

public class AttributeIcons {
    public static final int ICON_SIZE = 12;
    private static final Map<Kind, Boolean> FILTER_APPLIED = new EnumMap<>(Kind.class);

    public static void draw(AttributeInfo.Kind kind, float x, float y, float alpha) {
        ResourceLocation rl = locationFor(kind);
        if (rl == null) return;

        try {
            Minecraft.getMinecraft().getTextureManager().bindTexture(rl);
        } catch (Throwable t) {
            return;
        }

        if (!Boolean.TRUE.equals(FILTER_APPLIED.get(kind))) {
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
            FILTER_APPLIED.put(kind, true);
        }

        GL11.glColor4f(1F, 1F, 1F, alpha);
        GL11.glEnable(GL11.GL_BLEND);
        net.minecraft.client.renderer.OpenGlHelper.glBlendFunc(770, 771, 1, 0);

        Tessellator t = Tessellator.instance;
        t.startDrawingQuads();
        t.addVertexWithUV(x, y + ICON_SIZE, 0, 0, 1);
        t.addVertexWithUV(x + ICON_SIZE, y + ICON_SIZE, 0, 1, 1);
        t.addVertexWithUV(x + ICON_SIZE, y, 0, 1, 0);
        t.addVertexWithUV(x, y, 0, 0, 0);
        t.draw();

        GL11.glColor4f(1F, 1F, 1F, 1F);
    }

    private static ResourceLocation locationFor(Kind kind) {
        return kind.texture;
    }
}
