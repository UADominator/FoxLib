package foxiwhitee.FoxLib.client.tooltips.builder;

/**
 * Saves basic information about the topic when creating a tooltip
 */
@SuppressWarnings("all")
public final class ThemeBuilder {
    private final String id;
    private boolean hasIcon;
    private boolean enableShadow;
    private boolean drawBorder;
    private StyleBuilder styleBuilder;
    private ParticleBuilder particleBuilder;
    private SeparatorBuilder separatorBuilder;
    private FrameBuilder frameBuilder;
    private InnerLineBuilder innerLineBuilder;
    private GlowOutlineBuilder glowOutlineBuilder;

    ThemeBuilder(String id) {
        this.id = id;
    }

    /**
     * Enables drawing in the tooltip of a frame with an object icon. The colors of the frame are the same as those of the edges of the tooltip
     * @return This
     */
    public ThemeBuilder hasIcon() {
        this.hasIcon = true;
        return this;
    }

    /**
     * Enables a light shadow/glow around the tooltip
     * @return This
     */
    public ThemeBuilder enableShadow() {
        this.enableShadow = true;
        return this;
    }

    /**
     * Enables drawing a frame around the tooltip
     * @return This
     */
    public ThemeBuilder drawBorder() {
        this.drawBorder = true;
        return this;
    }

    /**
     * Remembers which style to use
     * @param style Style
     * @return This
     */
    public ThemeBuilder appendStyle(StyleBuilder style) {
        this.styleBuilder = style;
        return this;
    }

    /**
     * Remembers which particles to use
     * @param particles Particles data
     * @return This
     */
    public ThemeBuilder appendParticles(ParticleBuilder particles) {
        this.particleBuilder = particles;
        return this;
    }

    /**
     * Remembers which separators to use
     * @param separator Separator data
     * @return This
     */
    public ThemeBuilder appendSeparator(SeparatorBuilder separator) {
        this.separatorBuilder = separator;
        return this;
    }

    /**
     * Remembers which frame to use
     * @param frame Frame data
     * @return This
     */
    public ThemeBuilder appendFrame(FrameBuilder frame) {
        this.frameBuilder = frame;
        return this;
    }

    /**
     * Remembers which inner line to use
     * @param innerLine Inner line data
     * @return This
     */
    public ThemeBuilder appendInnerLine(InnerLineBuilder innerLine) {
        this.innerLineBuilder = innerLine;
        return this;
    }

    /**
     * Remembers which glow outline to use
     * @param glowOutline Glow outline data
     * @return This
     */
    public ThemeBuilder appendGlowOutline(GlowOutlineBuilder glowOutline) {
        this.glowOutlineBuilder = glowOutline;
        return this;
    }

    String getId() {
        return id;
    }

    boolean isHasIcon() {
        return hasIcon;
    }

    boolean isEnableShadow() {
        return enableShadow;
    }

    boolean isDrawBorder() {
        return drawBorder;
    }

    StyleBuilder getStyle() {
        return styleBuilder;
    }

    ParticleBuilder getParticles() {
        return particleBuilder;
    }

    SeparatorBuilder getSeparator() {
        return separatorBuilder;
    }

    FrameBuilder getFrame() {
        return frameBuilder;
    }

    InnerLineBuilder getInnerLine() {
        return innerLineBuilder;
    }

    GlowOutlineBuilder getGlowOutline() {
        return glowOutlineBuilder;
    }
}
