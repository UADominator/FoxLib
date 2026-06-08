package foxiwhitee.FoxLib.client.tooltips.theme;

@SuppressWarnings("unused")
public class TooltipTheme {
    private final String id;
    private final ForObject forObject;
    private final String object;
    private final FrameStyle style;
    private final ParticleConfig fx;

    public TooltipTheme(String id, ForObject forObject, String object, FrameStyle style, ParticleConfig fx) {
        this.id = id;
        this.forObject = forObject;
        this.object = object;
        this.style = style;
        this.fx = fx;
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

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public ParticleConfig getFx() {
        return fx;
    }
}
