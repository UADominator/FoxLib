package foxiwhitee.FoxLib.client.tooltips.theme.elements.lines;

public class InnerLineConfig {
    public final float animationSpeed;
    public final int[] colors;

    public InnerLineConfig(float animationSpeed, int[] colors) {
        this.animationSpeed = animationSpeed;
        this.colors = new int[colors.length + 1];
        System.arraycopy(colors, 0, this.colors, 0, colors.length);
        this.colors[colors.length] = this.colors[0];
    }
}
