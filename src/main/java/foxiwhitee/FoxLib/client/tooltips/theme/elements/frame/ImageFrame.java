package foxiwhitee.FoxLib.client.tooltips.theme.elements.frame;

import net.minecraft.util.ResourceLocation;

public class ImageFrame {
    public final ResourceLocation texture;
    public final int realCenterWidth;
    public final int width;

    public ImageFrame(ResourceLocation texture, int realCenterWidth, int width) {
        this.texture = texture;
        this.realCenterWidth = realCenterWidth;
        this.width = width;
    }
}
