package foxiwhitee.FoxLib.client.tooltips.builder;

import foxiwhitee.FoxLib.client.tooltips.theme.elements.lines.InnerLineConfig;

import java.util.List;

/**
 * Saves data to the inner frame with animation
 */
public class InnerLineBuilder {
    private float animationSpeed;
    private List<Integer> colors;

    InnerLineBuilder() {}

    /**
     * Saves data to the inner frame with animation
     * @param animationSpeed Color Blending Speed
     * @param colors Colors
     * @return This
     */
    public InnerLineBuilder data(float animationSpeed, List<Integer> colors) {
        this.animationSpeed = animationSpeed;
        this.colors = colors;
        return this;
    }

    InnerLineConfig build() {
        int[] colors = new int[this.colors.size()];
        for (int i = 0; i < this.colors.size(); i++) {
            colors[i] = this.colors.get(i);
        }
        return new InnerLineConfig(animationSpeed, colors);
    }
}
