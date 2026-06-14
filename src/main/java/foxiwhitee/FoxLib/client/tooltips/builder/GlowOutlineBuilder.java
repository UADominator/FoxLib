package foxiwhitee.FoxLib.client.tooltips.builder;

import foxiwhitee.FoxLib.client.tooltips.theme.elements.lines.GlowOutlineConfig;

import java.util.List;

/**
 * Stores glow data around the tooltip
 */
public class GlowOutlineBuilder {
    private int glowSize;
    private float animationSpeed;
    private List<Integer> colors;

    GlowOutlineBuilder() {}

    /**
     * Stores glow data around the tooltip
     * @param glowSize Glow size
     * @param animationSpeed Color Blending Speed
     * @param colors Colors
     * @return This
     */
    public GlowOutlineBuilder data(int glowSize, float animationSpeed, List<Integer> colors) {
        this.glowSize = glowSize;
        this.animationSpeed = animationSpeed;
        this.colors = colors;
        return this;
    }

    GlowOutlineConfig build() {
        int[] colors = new int[this.colors.size()];
        for (int i = 0; i < this.colors.size(); i++) {
            colors[i] = this.colors.get(i);
        }
        return new GlowOutlineConfig(glowSize, animationSpeed, colors);
    }
}
