package foxiwhitee.FoxLib.client.gui;

import foxiwhitee.FoxLib.FoxLib;
import foxiwhitee.FoxLib.api.button.ITooltipButton;
import foxiwhitee.FoxLib.client.gui.buttons.FoxBaseButton;
import foxiwhitee.FoxLib.utils.helpers.UtilGui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.inventory.Container;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public abstract class FoxBaseGui extends GuiContainer {
    private String modID = FoxLib.MODID;
    private boolean drawingFG = false;

    public FoxBaseGui(Container container, int xSize, int ySize) {
        super(container);
        this.ySize = ySize;
        this.xSize = xSize;
    }

    public void drawFG(int offsetX, int offsetY, int mouseX, int mouseY) {
        this.bindTexture(modID, this.getBackground());
    }

    public void drawBG(int offsetX, int offsetY, int mouseX, int mouseY) {
        this.bindTexture(modID, this.getBackground());
        UtilGui.drawTexture(offsetX, offsetY, 0, 0, xSize, ySize, xSize, ySize, getSizeTexture()[0], getSizeTexture()[1]);
    }

    public void drawBG(int offsetX, int offsetY, int mouseX, int mouseY, float partialTicks) {}

    public void bindTexture(String base, String file) {
        ResourceLocation loc = new ResourceLocation(base, "textures/" + file);
        this.mc.getTextureManager().bindTexture(loc);
    }

    protected abstract String getBackground();

    protected int[] getSizeTexture() {
        return new int[]{512, 512};
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        for(Object c : this.buttonList) {
            if (c instanceof ITooltipButton tooltip) {
                int x = tooltip.xPos();
                int y = tooltip.yPos();
                if (x < mouseX && x + tooltip.getWidth() > mouseX && tooltip.isVisible() && y < mouseY && y + tooltip.getHeight() > mouseY) {
                    if (y < 15) {
                        y = 15;
                    }

                    String msg = tooltip.getMessage();
                    if (msg != null) {
                        this.drawTooltip(x + 11, y + 4, msg);
                    }
                }
            }
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        super.actionPerformed(button);
        if (button instanceof FoxBaseButton foxBaseButton) {
            foxBaseButton.nextText();
        }
    }

    protected final void drawGuiContainerBackgroundLayer(float f, int x, int y) {
        int ox = this.guiLeft;
        int oy = this.guiTop;
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.drawBG(ox, oy, x, y);
        this.drawBG(ox, oy, x, y, f);
    }

    protected final void drawGuiContainerForegroundLayer(int x, int y) {
        int ox = this.guiLeft;
        int oy = this.guiTop;
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        //this.drawFG(ox, oy, x, y);
        drawingFG = true;
        this.drawFG(0, 0, x, y);
        drawingFG = false;
    }

    public final void drawIfInMouse(int mouseX, int mouseY, int x, int y, int w, int h, String str) {
        if (mouseX >= this.guiLeft + x && mouseX <= this.guiLeft + x + w && mouseY >= this.guiTop + y && mouseY <= this.guiTop + y + h)
            drawTooltip(drawingFG ? mouseX - guiLeft : mouseX, drawingFG ? mouseY - guiTop : mouseY, str);
    }

    public void drawTooltip(int mouseX, int mouseY, String message) {
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_DEPTH_TEST);

        String[] lines = message.split("\n");
        if (lines.length > 0) {
            int tooltipWidth = 0;

            for (String line : lines) {
                int lineWidth = this.fontRendererObj.getStringWidth(line);
                if (lineWidth > tooltipWidth) {
                    tooltipWidth = lineWidth;
                }
            }

            int x = mouseX + 12;
            int y = mouseY - 12;
            int tooltipHeight = 8;

            if (lines.length > 1) {
                tooltipHeight += 2 + (lines.length - 1) * 10;
            }

            if (this.guiTop + y + tooltipHeight + 6 > this.height) {
                y = this.height - tooltipHeight - this.guiTop - 6;
            }

            this.zLevel = 300.0F;
            itemRender.zLevel = 300.0F;

            int backgroundColor = 0xF0100010;
            int borderColorStart = 0x505000FF;
            int borderColorEnd = (borderColorStart & 0xFEFEFE) >> 1 | borderColorStart & 0xFF000000;

            this.drawGradientRect(x - 3, y - 4, x + tooltipWidth + 3, y - 3, backgroundColor, backgroundColor);
            this.drawGradientRect(x - 3, y + tooltipHeight + 3, x + tooltipWidth + 3, y + tooltipHeight + 4, backgroundColor, backgroundColor);
            this.drawGradientRect(x - 3, y - 3, x + tooltipWidth + 3, y + tooltipHeight + 3, backgroundColor, backgroundColor);
            this.drawGradientRect(x - 4, y - 3, x - 3, y + tooltipHeight + 3, backgroundColor, backgroundColor);
            this.drawGradientRect(x + tooltipWidth + 3, y - 3, x + tooltipWidth + 4, y + tooltipHeight + 3, backgroundColor, backgroundColor);

            this.drawGradientRect(x - 3, y - 3 + 1, x - 3 + 1, y + tooltipHeight + 3 - 1, borderColorStart, borderColorEnd);
            this.drawGradientRect(x + tooltipWidth + 2, y - 3 + 1, x + tooltipWidth + 3, y + tooltipHeight + 3 - 1, borderColorStart, borderColorEnd);
            this.drawGradientRect(x - 3, y - 3, x + tooltipWidth + 3, y - 3 + 1, borderColorStart, borderColorStart);
            this.drawGradientRect(x - 3, y + tooltipHeight + 2, x + tooltipWidth + 3, y + tooltipHeight + 3, borderColorEnd, borderColorEnd);

            for (int i = 0; i < lines.length; ++i) {
                String currentLine = lines[i];

                if (i == 0) {
                    currentLine = "§f" + currentLine;
                } else {
                    currentLine = "§7" + currentLine;
                }

                this.fontRendererObj.drawStringWithShadow(currentLine, x, y, -1);

                if (i == 0) {
                    y += 2;
                }
                y += 10;
            }

            this.zLevel = 0.0F;
            itemRender.zLevel = 0.0F;
        }

        GL11.glPopAttrib();
    }

    public void setModID(String modID) {
        this.modID = modID;
    }

    protected void drawFluid(FluidTank tank, int x, int y, int width, int height) {
        FluidStack fluid = tank.getFluid();
        if (fluid == null || fluid.amount <= 0) {
            return;
        }

        Fluid fluidType = fluid.getFluid();
        IIcon fluidStill = fluidType.getIcon(fluid);

        if (fluidStill == null) {
            return;
        }

        int fluidHeight = height;
        if (fluidHeight <= 0 && fluid.amount > 0) fluidHeight = 1;

        int fluidColor = fluidType.getColor();

        float red = (float) (fluidColor >> 16 & 255) / 255.0F;
        float green = (float) (fluidColor >> 8 & 255) / 255.0F;
        float blue = (float) (fluidColor & 255) / 255.0F;

        GL11.glColor4f(red, green, blue, 1.0F);

        Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationBlocksTexture);

        int drawY = y + height - fluidHeight;

        for (int currentDrawHeight = 0; currentDrawHeight < fluidHeight; currentDrawHeight += 16) {
            int actualDrawHeight = Math.min(fluidHeight - currentDrawHeight, 16);

            drawTexturedModelRectFromIcon(
                x,
                drawY + fluidHeight - currentDrawHeight - actualDrawHeight,
                fluidStill,
                width,
                actualDrawHeight
            );
        }

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }
}
