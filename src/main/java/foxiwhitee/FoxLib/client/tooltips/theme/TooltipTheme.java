package foxiwhitee.FoxLib.client.tooltips.theme;

import foxiwhitee.FoxLib.client.tooltips.theme.elements.frame.FrameStyle;
import foxiwhitee.FoxLib.client.tooltips.theme.elements.frame.ImageFrame;
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
    private final ImageFrame frame;

    public TooltipTheme(String id, ForObject forObject, String object, FrameStyle style, ParticleConfig fx, SeparatorConfig separatorConfig, boolean drawIcon, ImageFrame frame) {
        this.id = id;
        this.forObject = forObject;
        this.object = object;
        this.style = style;
        this.fx = fx;
        this.separatorConfig = separatorConfig;
        this.drawIcon = drawIcon;
        this.frame = frame;
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
}
