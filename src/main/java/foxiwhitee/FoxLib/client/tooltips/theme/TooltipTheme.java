package foxiwhitee.FoxLib.client.tooltips.theme;

import foxiwhitee.FoxLib.client.tooltips.theme.elements.frame.FrameStyle;
import foxiwhitee.FoxLib.client.tooltips.theme.elements.frame.ImageFrame;
import foxiwhitee.FoxLib.client.tooltips.theme.elements.lines.GlowOutlineConfig;
import foxiwhitee.FoxLib.client.tooltips.theme.elements.lines.InnerLineConfig;
import foxiwhitee.FoxLib.client.tooltips.theme.elements.object.ObjectType;
import foxiwhitee.FoxLib.client.tooltips.theme.elements.particle.ParticleConfig;
import foxiwhitee.FoxLib.client.tooltips.theme.elements.separator.SeparatorConfig;

@SuppressWarnings("unused")
public class TooltipTheme{
    private final String id;
    private final ObjectType objectType;
    private final Object object;
    private final FrameStyle style;
    private final ParticleConfig fx;
    private final SeparatorConfig separatorConfig;
    private final ImageFrame frame;
    private final InnerLineConfig innerLineConfig;
    private final GlowOutlineConfig glowOutlineConfig;
    private final boolean drawIcon;
    private final boolean enableShadow;
    private final boolean drawBorder;

    public TooltipTheme(String id, ObjectType objectType, Object object, FrameStyle style, ParticleConfig fx, SeparatorConfig separatorConfig, boolean drawIcon, boolean enableShadow, boolean drawBorder, ImageFrame frame, InnerLineConfig innerLineConfig, GlowOutlineConfig glowOutlineConfig) {
        this.id = id;
        this.objectType = objectType;
        this.object = object;
        this.style = style;
        this.fx = fx;
        this.separatorConfig = separatorConfig;
        this.drawIcon = drawIcon;
        this.enableShadow = enableShadow;
        this.drawBorder = drawBorder;
        this.frame = frame;
        this.innerLineConfig = innerLineConfig;
        this.glowOutlineConfig = glowOutlineConfig;
    }

    public String getId() {
        return id;
    }

    public ObjectType getForObject() {
        return objectType;
    }

    public Object getObject() {
        return object;
    }

    public FrameStyle getStyle() {
        return style;
    }

    public ParticleConfig getFx() {
        return fx;
    }

    public SeparatorConfig getSeparatorConfig() {
        return separatorConfig;
    }

    public ImageFrame getFrame() {
        return frame;
    }

    public InnerLineConfig getInnerLineConfig() {
        return innerLineConfig;
    }

    public GlowOutlineConfig getGlowOutlineConfig() {
        return glowOutlineConfig;
    }

    public boolean isDrawIcon() {
        return drawIcon;
    }

    public boolean isEnableShadow() {
        return enableShadow;
    }

    public boolean isDrawBorder() {
        return drawBorder;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
