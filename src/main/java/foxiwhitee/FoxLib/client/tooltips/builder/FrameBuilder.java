package foxiwhitee.FoxLib.client.tooltips.builder;

import foxiwhitee.FoxLib.client.tooltips.theme.elements.frame.ImageFrame;
import net.minecraft.util.ResourceLocation;

/**
 * Stores texture frame data around tooltip
 */
public final class FrameBuilder {
    private ResourceLocation texture;
    private int realCenterWidth;
    private int width;

    FrameBuilder() {}

    /**
     * Stores texture frame data around tooltip
     * @param texture Місце знаходження вашої рамки для tooltip. Стандартний розмір файлу це 128x32. Але ви можете пропорційно збільшувати чи зменшувати його.
     *                <p>В цьому моді в теці frames ви можете знайти приклад малювання рамки.
     *                Рамка має 6 елементів: верхній лівий кут, верхня середина, правий верхній кут, правий нижній кут, нижня середина, лівий нижній кут.
     *                Кути мають розмір 16х16, а середини 96х16</p>
     *                <p>Кожен елемент в прикладі має по 3 кольори, щоб показати їх розміщення.
     *                Звичайний колір знаходиться на рамці tooltip якщо вона увімкнена.
     *                Темний колір заходиться всередині tooltip.
     *                Світлий колір знаходиться зовні tooltip</p>
     * @param width Texture length. The default is 128px. Can be increased or decreased evenly
     * @param realCenterWidth This is the true length of the element in the middle.
     *                        0-96px. Despite the fact that the length of the texture can vary, you need to calculate how much your heart element will occupy.
     *                        This is done so that small tooltips do not draw a medium object that does not fit
     * @return This
     */
    public FrameBuilder data(ResourceLocation texture, int width, int realCenterWidth) {
        this.texture = texture;
        this.width = width;
        this.realCenterWidth = realCenterWidth;
        return this;
    }

    ImageFrame build() {
        return new ImageFrame(texture, realCenterWidth, width);
    }
}
