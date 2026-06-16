package foxiwhitee.FoxLib.client.tooltips.theme;

import foxiwhitee.FoxLib.client.tooltips.theme.elements.frame.FrameStyle;
import foxiwhitee.FoxLib.client.tooltips.theme.elements.frame.ImageFrame;
import foxiwhitee.FoxLib.client.tooltips.theme.elements.lines.GlowOutlineConfig;
import foxiwhitee.FoxLib.client.tooltips.theme.elements.lines.InnerLineConfig;
import foxiwhitee.FoxLib.client.tooltips.theme.elements.object.ObjectType;
import foxiwhitee.FoxLib.client.tooltips.theme.elements.separator.SeparatorConfig;

@SuppressWarnings("unused")
public class TooltipTheme{
    private final String id;
    private final ObjectType objectType;
    private final Object object;
    private int itemDamage = -1;
    private final FrameStyle style;
    private final SeparatorConfig separatorConfig;
    private final ImageFrame frame;
    private final InnerLineConfig innerLineConfig;
    private final GlowOutlineConfig glowOutlineConfig;
    private final boolean drawIcon;
    private final boolean enableShadow;
    private final boolean drawBorder;
    private final boolean enableParticles;

    public TooltipTheme(String id, ObjectType objectType, Object object, FrameStyle style, SeparatorConfig separatorConfig, boolean drawIcon, boolean enableShadow, boolean drawBorder, ImageFrame frame, InnerLineConfig innerLineConfig, GlowOutlineConfig glowOutlineConfig, boolean enableParticles) {
        this.id = id;
        this.objectType = objectType;
        this.object = object;
        this.style = style;
        this.enableParticles = enableParticles;
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

    public int getItemDamage() {
        return itemDamage;
    }

    public void setItemDamage(int itemDamage) {
        this.itemDamage = itemDamage;
    }

    public FrameStyle getStyle() {
        return style;
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

    public boolean isEnableParticles() {
        return enableParticles;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
