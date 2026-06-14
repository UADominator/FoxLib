package foxiwhitee.FoxLib.client.tooltips.builder;

import cpw.mods.fml.common.Loader;
import foxiwhitee.FoxLib.client.tooltips.theme.ThemeRegister;
import foxiwhitee.FoxLib.client.tooltips.theme.TooltipTheme;
import foxiwhitee.FoxLib.client.tooltips.theme.elements.object.ObjectType;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
* Class for easy creation and registration of custom tooltips.
* To create a custom tooltip, you need to inherit from this class and have an annotation TooltipConfiguration
*/
@SuppressWarnings("all")
public abstract class TooltipProcessor {
    private final Map<String, ThemeBuilder> builders = new LinkedHashMap<>();
    private final Map<String, StyleBuilder> styleBuilders = new LinkedHashMap<>();
    private final Map<String, ParticleBuilder> particleBuilders = new LinkedHashMap<>();
    private final Map<String, SeparatorBuilder> separatorBuilders = new LinkedHashMap<>();
    private final Map<String, InnerLineBuilder> innerLineBuilders = new LinkedHashMap<>();
    private final Map<String, GlowOutlineBuilder> glowOutlineBuilders = new LinkedHashMap<>();
    private final Map<String, FrameBuilder> frameBuilders = new LinkedHashMap<>();
    private final Map<String, List<Integer>> colors = new LinkedHashMap<>();
    private final Map<String, ResourceLocation> resources = new LinkedHashMap<>();

    public TooltipProcessor() {

    }

    /**
     * Creates a ThemeBuilder and saves it for easy calling
     * @param id Unique theme ID
     * @return Created ThemeBuilder
     */
    protected final ThemeBuilder createTheme(String id) {
        ThemeBuilder builder = new ThemeBuilder(id);
        builders.put(id, builder);
        return builder;
    }

    /**
     * Creates a StyleBuilder and stores by localName for easy invocation
     * @param localName Unique name in the middle of the TooltipProcessor to conveniently call StyleBuilder from the cache
     * @return Created StyleBuilder
     */
    protected final StyleBuilder createStyle(String localName) {
        StyleBuilder builder = new StyleBuilder();
        styleBuilders.put(localName, builder);
        return builder;
    }

    /**
     * Creates a ParticleBuilder and stores by localName for easy invocation
     * @param localName Unique name in the middle of the TooltipProcessor to conveniently call ParticleBuilder from the cache
     * @return Created ParticleBuilder
     */
    protected final ParticleBuilder createParticles(String localName, ParticleBuilder.Kind kind) {
        ParticleBuilder builder = new ParticleBuilder(kind);
        particleBuilders.put(localName, builder);
        return builder;
    }

    /**
     * Creates a regular list with Integer to conveniently record colors and use in various settings
     * @param localName Unique name in the middle of the TooltipProcessor to conveniently call List from the cache
     * @return Created List
     */
    protected final List<Integer> createColorList(String localName) {
        List<Integer> list = new ArrayList<>();
        colors.put(localName, list);
        return list;
    }

    /**
     * Creates a SeparatorBuilder and stores by localName for easy invocation
     * @param localName Unique name in the middle of the TooltipProcessor to conveniently call SeparatorBuilder from the cache
     * @return Created SeparatorBuilder
     */
    protected final SeparatorBuilder createSeparator(String localName) {
        SeparatorBuilder builder = new SeparatorBuilder();
        separatorBuilders.put(localName, builder);
        return builder;
    }

    /**
     * Creates a ResourceLocation and stores by localName for easy invocation
     * @param localName Unique name in the middle of the TooltipProcessor to conveniently call ResourceLocation from the cache
     * @param modId Mod ID
     * @param path Texture path, including folders starting with "textures". It is also necessary to specify the file format
     * @return Created ResourceLocation
     */
    protected final ResourceLocation createResource(String localName, String modId, String path) {
        ResourceLocation resourceLocation = new ResourceLocation(modId, path);
        resources.put(localName, resourceLocation);
        return resourceLocation;
    }

    /**
     * Creates a FrameBuilder and stores by localName for easy invocation
     * @param localName Unique name in the middle of the TooltipProcessor to conveniently call FrameBuilder from the cache
     * @return Created FrameBuilder
     */
    protected final FrameBuilder createFrame(String localName) {
        FrameBuilder builder = new FrameBuilder();
        frameBuilders.put(localName, builder);
        return builder;
    }

    /**
     * Creates a InnerLineBuilder and stores by localName for easy invocation
     * @param localName Unique name in the middle of the TooltipProcessor to conveniently call InnerLineBuilder from the cache
     * @return Created InnerLineBuilder
     */
    protected final InnerLineBuilder createInnerLine(String localName) {
        InnerLineBuilder builder = new InnerLineBuilder();
        innerLineBuilders.put(localName, builder);
        return builder;
    }

    /**
     * Creates a GlowOutlineBuilder and stores by localName for easy invocation
     * @param localName Unique name in the middle of the TooltipProcessor to conveniently call GlowOutlineBuilder from the cache
     * @return Created GlowOutlineBuilder
     */
    protected final GlowOutlineBuilder createGlowOutline(String localName) {
        GlowOutlineBuilder builder = new GlowOutlineBuilder();
        glowOutlineBuilders.put(localName, builder);
        return builder;
    }

    /**
     * Find all the methods with the TooltipConfigurationMethod annotation, check all the necessary dependencies and call
     */
    public final void run() {
        Class<?> clazz = getClass();
        Method[] methods = clazz.getDeclaredMethods();
        for (Method m : methods) {
            if (m.isAnnotationPresent(TooltipConfigurationMethod.class)) {
                TooltipConfigurationMethod annotation = m.getAnnotation(TooltipConfigurationMethod.class);
                if (verifyDependencies(annotation)) {
                    try {
                        m.invoke(this);
                    } catch (IllegalAccessException | InvocationTargetException ignored) {
                    }
                }
            }
        }
    }

    /**
     * Check if the method has all the necessary dependencies
     * @param annotation Annotation above the method
     * @return If all the necessary dependencies are loaded
     */
    private boolean verifyDependencies(TooltipConfigurationMethod annotation) {
        for (String mod : annotation.needMods()) {
            if (!Loader.isModLoaded(mod)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Find ThemeBuilder by its ID
     * @param id Unique theme ID
     * @return ThemeBuilder
     */
    protected final ThemeBuilder getTheme(String id) {
        return builders.get(id);
    }

    /**
     * Find the last created ThemeBuilder
     * @return Last created ThemeBuilder
     */
    protected final ThemeBuilder getLastTheme() {
        return builders.values().stream()
            .reduce((first, second) -> second)
            .orElse(null);
    }

    /**
     * Find StyleBuilder by its localName
     * @param localName Unique name in the middle of the TooltipProcessor to conveniently call StyleBuilder from the cache
     * @return StyleBuilder
     */
    protected final StyleBuilder getStyle(String localName) {
        return styleBuilders.get(localName);
    }

    /**
     * Find the last created StyleBuilder
     * @return Last created StyleBuilder
     */
    protected final StyleBuilder getLastStyle() {
        return styleBuilders.values().stream()
            .reduce((first, second) -> second)
            .orElse(null);
    }

    /**
     * Find ParticleBuilder by its localName
     * @param localName Unique name in the middle of the TooltipProcessor to conveniently call ParticleBuilder from the cache
     * @return ParticleBuilder
     */
    protected final ParticleBuilder getParticles(String localName) {
        return particleBuilders.get(localName);
    }

    /**
     * Find the last created ParticleBuilder
     * @return Last created ParticleBuilder
     */
    protected final ParticleBuilder getLastParticles() {
        return particleBuilders.values().stream()
            .reduce((first, second) -> second)
            .orElse(null);
    }

    /**
     * Find List by its localName
     * @param localName Unique name in the middle of the TooltipProcessor to conveniently call List from the cache
     * @return List
     */
    protected final List<Integer> getColors(String localName) {
        return colors.get(localName);
    }

    /**
     * Find the last created List
     * @return Last created List
     */
    protected final List<Integer> getLastColors() {
        return colors.values().stream()
            .reduce((first, second) -> second)
            .orElse(null);
    }

    /**
     * Find SeparatorBuilder by its localName
     * @param localName Unique name in the middle of the TooltipProcessor to conveniently call SeparatorBuilder from the cache
     * @return SeparatorBuilder
     */
    protected final SeparatorBuilder getSeparator(String localName) {
        return separatorBuilders.get(localName);
    }

    /**
     * Find the last created SeparatorBuilder
     * @return Last created SeparatorBuilder
     */
    protected final SeparatorBuilder getLastSeparator() {
        return separatorBuilders.values().stream()
            .reduce((first, second) -> second)
            .orElse(null);
    }

    /**
     * Find ResourceLocation by its localName
     * @param localName Unique name in the middle of the TooltipProcessor to conveniently call ResourceLocation from the cache
     * @return ResourceLocation
     */
    protected final ResourceLocation getResource(String localName) {
        return resources.get(localName);
    }

    /**
     * Find the last created ResourceLocation
     * @return Last created ResourceLocation
     */
    protected final ResourceLocation getLastResource() {
        return resources.values().stream()
            .reduce((first, second) -> second)
            .orElse(null);
    }

    /**
     * Find FrameBuilder by its localName
     * @param localName Unique name in the middle of the TooltipProcessor to conveniently call FrameBuilder from the cache
     * @return FrameBuilder
     */
    protected final FrameBuilder getFrame(String localName) {
        return frameBuilders.get(localName);
    }

    /**
     * Find the last created FrameBuilder
     * @return Last created FrameBuilder
     */
    protected final FrameBuilder getLastFrame() {
        return frameBuilders.values().stream()
            .reduce((first, second) -> second)
            .orElse(null);
    }

    /**
     * Find InnerLineBuilder by its localName
     * @param localName Unique name in the middle of the TooltipProcessor to conveniently call InnerLineBuilder from the cache
     * @return InnerLineBuilder
     */
    protected final InnerLineBuilder getInnerLine(String localName) {
        return innerLineBuilders.get(localName);
    }

    /**
     * Find the last created InnerLineBuilder
     * @return Last created InnerLineBuilder
     */
    protected final InnerLineBuilder getLastInnerLine() {
        return innerLineBuilders.values().stream()
            .reduce((first, second) -> second)
            .orElse(null);
    }

    /**
     * Find GlowOutlineBuilder by its localName
     * @param localName Unique name in the middle of the TooltipProcessor to conveniently call GlowOutlineBuilder from the cache
     * @return GlowOutlineBuilder
     */
    protected final GlowOutlineBuilder getGlowOutline(String localName) {
        return glowOutlineBuilders.get(localName);
    }

    /**
     * Find the last created GlowOutlineBuilder
     * @return Last created GlowOutlineBuilder
     */
    protected final GlowOutlineBuilder getLastGlowOutline() {
        return glowOutlineBuilders.values().stream()
            .reduce((first, second) -> second)
            .orElse(null);
    }

    /**
     * Creates and registers a theme using ThemeBuilder and Object
     * @param theme ThemeBuilder that stores theme data
     * @param text The text for which the topic will be applied. You can write not localized, and it will be localized by the mod when checked. The subject only applies to the text at the beginning
     * @return This
     */
    protected final TooltipProcessor buildForText(ThemeBuilder theme, String text) {
        try {
            ThemeRegister.registerTheme(build(theme, text, ObjectType.TEXT));
        } catch (Exception ignored) {}
        return this;
    }

    /**
     * Creates and registers a theme using ThemeBuilder and Object
     * @param theme ThemeBuilder that stores theme data
     * @param nbt The subject will be applied to an item that has all the keys from nbt and the same values. Attention! This does not mean an exact nbt match
     * @return This
     */
    protected final TooltipProcessor buildForNBT(ThemeBuilder theme, NBTTagCompound nbt) {
        try {
            ThemeRegister.registerTheme(build(theme, nbt, ObjectType.NBT));
        } catch (Exception ignored) {}
        return this;
    }

    /**
     * Creates and registers a theme using ThemeBuilder and Object
     * @param theme ThemeBuilder that stores theme data
     * @param item The subject will be applied to the specified subject. In the value of item, write "modId:itemName"
     * @return This
     */
    protected final TooltipProcessor buildForItem(ThemeBuilder theme, String item) {
        try {
            ThemeRegister.registerTheme(build(theme, item, ObjectType.ITEM));
        } catch (Exception ignored) {}
        return this;
    }

    /**
     * Creates and registers a theme using ThemeBuilder and Object
     * @param theme ThemeBuilder that stores theme data
     * @param mod The theme will be applied to all items in the specified mod. If you want to apply something to Minecraft, just write "minecraft"
     * @return This
     */
    protected final TooltipProcessor buildForMod(ThemeBuilder theme, String mod) {
        try {
            ThemeRegister.registerTheme(build(theme, mod, ObjectType.MOD));
        } catch (Exception ignored) {}
        return this;
    }

    private TooltipTheme build(ThemeBuilder theme, Object object, ObjectType type) {
        boolean hasIcon = type != ObjectType.TEXT && theme.isHasIcon();

        return new TooltipTheme(theme.getId(), type, object, theme.getStyle().build(),
            theme.getParticles() == null ? null : theme.getParticles().build(),
            theme.getSeparator() == null ? null : theme.getSeparator().build(),
            hasIcon, theme.isEnableShadow(), theme.isDrawBorder(),
            theme.getFrame() == null ? null : theme.getFrame().build(),
            theme.getInnerLine() == null ? null : theme.getInnerLine().build(),
            theme.getGlowOutline() == null ? null : theme.getGlowOutline().build());
    }
}
