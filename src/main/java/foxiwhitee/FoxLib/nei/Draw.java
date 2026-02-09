package foxiwhitee.FoxLib.nei;

import codechicken.lib.gui.GuiDraw;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.IIcon;
import net.minecraftforge.fluids.FluidStack;
import org.lwjgl.opengl.GL11;
import java.util.LinkedList;
import java.util.List;

public final class Draw<T> {
    private final List<DrawMethod<T>> methods = new LinkedList<>();
    private final String modId;
    private final HandlerRecipeBuilder<T> builder;
    public int tick;

    public Draw(HandlerRecipeBuilder<T> builder, String modId) {
        this.builder = builder;
        this.modId = modId;
    }

    @FunctionalInterface
    public interface DrawMethod<T> {
        void draw(Draw<T> draw, UniversalRecipeHandler<T>.CachedUniversalRecipe recipe);
    }

    public void draw(UniversalRecipeHandler<T>.CachedUniversalRecipe recipe) {
        GL11.glColor4f(1, 1, 1, 1);
        for (DrawMethod<T> method : methods) {
            method.draw(this, recipe);
        }
    }

    public HandlerRecipeBuilder<T> addMethod(DrawMethod<T> method) {
        methods.add(method);
        return builder;
    }

    public void bind(String texture) {
        bind(modId, texture);
    }

    public void bind(String modId, String texture) {
        GuiDraw.changeTexture(modId + ":textures/" + texture);
    }

    public void drawSome(int x, int y, int tx, int ty, int w, int h) {
        GuiDraw.drawTexturedModalRect(x, y, tx, ty, w, h);
    }

    public void drawFluid(int x, int y, FluidStack fluid, int width, int height) {
        if (fluid == null || fluid.getFluid() == null) return;

        IIcon icon = fluid.getFluid().getStillIcon();
        if (icon == null) icon = fluid.getFluid().getIcon();
        if (icon == null) return;

        Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationBlocksTexture);

        int color = fluid.getFluid().getColor(fluid);
        GL11.glColor4f((color >> 16 & 255) / 255.0F, (color >> 8 & 255) / 255.0F, (color & 255) / 255.0F, 1.0F);

        for (int i = 0; i < width; i += 16) {
            for (int j = 0; j < height; j += 16) {
                int drawWidth = Math.min(width - i, 16);
                int drawHeight = Math.min(height - j, 16);

                drawFluidSection(x + i, y + j, icon, drawWidth, drawHeight);
            }
        }
        GL11.glColor4f(1, 1, 1, 1);
    }

    public void drawString(String text, int x, int y) {
        GuiDraw.drawString(text, x, y, 0x404040, false);
    }

    private void drawFluidSection(int x, int y, IIcon icon, int width, int height) {
        double minU = icon.getMinU();
        double maxV = icon.getMaxV();
        double maxU = minU + (icon.getMaxU() - minU) * width / 16.0;
        double minV = maxV - (maxV - icon.getMinV()) * height / 16.0;

        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(x, y + height, 0, minU, maxV);
        tessellator.addVertexWithUV(x + width, y + height, 0, maxU, maxV);
        tessellator.addVertexWithUV(x + width, y, 0, maxU, minV);
        tessellator.addVertexWithUV(x, y, 0, minU, minV);
        tessellator.draw();
    }
}
