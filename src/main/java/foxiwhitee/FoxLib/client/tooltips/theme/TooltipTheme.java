package foxiwhitee.FoxLib.client.tooltips.theme;

import foxiwhitee.FoxLib.client.tooltips.theme.elements.frame.FrameStyle;
import foxiwhitee.FoxLib.client.tooltips.theme.elements.frame.ImageFrame;
import foxiwhitee.FoxLib.client.tooltips.theme.elements.innerline.InnerLineConfig;
import foxiwhitee.FoxLib.client.tooltips.theme.elements.particle.ParticleConfig;
import foxiwhitee.FoxLib.client.tooltips.theme.elements.separator.SeparatorConfig;

@SuppressWarnings("unused")
public class TooltipTheme {
    private final String id;
    private final ForObject forObject;
    private final String object;
    private final FrameStyle style;
    private final ParticleConfig fx;
    private final SeparatorConfig separatorConfig;
    private final boolean drawIcon;
    private final boolean enableShadow;
    private final ImageFrame frame;
    private final InnerLineConfig innerLineConfig;

    public TooltipTheme(String id, ForObject forObject, String object, FrameStyle style, ParticleConfig fx, SeparatorConfig separatorConfig, boolean drawIcon, boolean enableShadow, ImageFrame frame, InnerLineConfig innerLineConfig) {
        this.id = id;
        this.forObject = forObject;
        this.object = object;
        this.style = style;
        this.fx = fx;
        this.separatorConfig = separatorConfig;
        this.drawIcon = drawIcon;
        this.enableShadow = enableShadow;
        this.frame = frame;
        this.innerLineConfig = innerLineConfig;
    }

    public String getId() {
        return id;
    }

    public ForObject getForObject() {
        return forObject;
    }

    public FrameStyle getStyle() {
        return style;
    }

    public String getObject() {
        return object;
    }

    public boolean isDrawIcon() {
        return drawIcon;
    }

    public boolean isEnableShadow() {
        return enableShadow;
    }

    public SeparatorConfig getSeparatorConfig() {
        return separatorConfig;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public ParticleConfig getFx() {
        return fx;
    }

    public ImageFrame getFrame() {
        return frame;
    }

    public InnerLineConfig getInnerLineConfig() {
        return innerLineConfig;
    }
}
