package foxiwhitee.FoxLib.client.tooltips.theme.elements.lines;

public class GlowOutlineConfig {
    public final int glowSize;
    public final float animationSpeed;
    public final int[] colors;

    public GlowOutlineConfig(int glowSize, float animationSpeed, int[] colors) {
        this.glowSize = glowSize;
        this.animationSpeed = animationSpeed;
        this.colors = new int[colors.length + 1];
        System.arraycopy(colors, 0, this.colors, 0, colors.length);
        this.colors[colors.length] = this.colors[0];
    }
}
