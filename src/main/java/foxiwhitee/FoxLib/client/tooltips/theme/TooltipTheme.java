package foxiwhitee.FoxLib.client.tooltips.theme;

import foxiwhitee.FoxLib.client.tooltips.theme.elements.frame.FrameStyle;
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

    public TooltipTheme(String id, ForObject forObject, String object, FrameStyle style, ParticleConfig fx, SeparatorConfig separatorConfig) {
        this.id = id;
        this.forObject = forObject;
        this.object = object;
        this.style = style;
        this.fx = fx;
        this.separatorConfig = separatorConfig;
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
}
