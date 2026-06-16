package foxiwhitee.FoxLib.integration.crafttweaker.tooltips;

import foxiwhitee.FoxLib.client.tooltips.builder.*;
import minetweaker.MineTweakerAPI;
import minetweaker.api.data.*;
import net.minecraft.nbt.*;
import net.minecraft.util.ResourceLocation;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.*;

/**
 * Main entry point for MineTweaker / CraftTweaker integration.
 * Exposes the TooltipProcessor framework functionality to the ZenScript environment.
 */
@ZenClass("mods.foxlib.TooltipManager")
@SuppressWarnings("unused")
public final class ZenTooltipManager {
    static final ProcessorBridge BRIDGE = new ProcessorBridge();

    /**
     * Internal bridge bypassing protected member access of TooltipProcessor.
     * Utilizes standard package-private visibility rules within the builder package.
     */
    static final class ProcessorBridge extends TooltipProcessor {

        public ThemeBuilder callCreateTheme(String id) {
            return this.createTheme(id);
        }

        public StyleBuilder callCreateStyle(String id) {
            return this.createStyle(id);
        }

        public SeparatorBuilder callCreateSeparator(String id) {
            return this.createSeparator(id);
        }

        public InnerLineBuilder callCreateInnerLine(String id) {
            return this.createInnerLine(id);
        }

        public GlowOutlineBuilder callCreateGlowOutline(String id) {
            return this.createGlowOutline(id);
        }

        public FrameBuilder callCreateFrame(String id) {
            return this.createFrame(id);
        }

        public void callCreateColorList(String id, List<Integer> hexColors) {
            this.createColorList(id).addAll(hexColors);
        }

        public void callCreateResource(String id, String domain, String path) {
            this.createResource(id, domain, path);
        }

        public ResourceLocation callGetLastResource() {
            return this.getLastResource();
        }

        public void callBuildForMod(ThemeBuilder theme, String mod) {
            this.buildForMod(theme, mod);
        }

        public void callBuildForItem(ThemeBuilder theme, String item, int damage) {
            this.buildForItem(theme, item, damage);
        }

        public void callBuildForNBT(ThemeBuilder theme, NBTTagCompound nbt) {
            this.buildForNBT(theme, nbt);
        }

        public void callBuildForText(ThemeBuilder theme, String text) {
            this.buildForText(theme, text);
        }
    }

    /**
     * Universal data parser transforming ZenScript elements into standard Java signed integer colors.
     * Evaluates raw HEX string definitions cleanly without relying on high-bound integer variables.
     * @param data Universal ZenScript String value holder token
     * @return Extracted alpha-aware integer color map value code
     */
    public static int parseColor(String data) {
        if (data == null) {
            return 0;
        }
        try {
            return (int) Long.parseLong(data, 16);
        } catch (NumberFormatException e) {
            MineTweakerAPI.logError("Invalid HEX color format specifier detected: '" + data + "'. Falling back to black.");
            return 0;
        }
    }

    public static List<Integer> parseStringArray(String[] hexArray) {
        List<Integer> list = new ArrayList<>();
        if (hexArray != null) {
            for (String hex : hexArray) {
                if (hex != null) {
                    try {
                        list.add((int) Long.parseLong(hex.trim(), 16));
                    } catch (NumberFormatException e) {
                        MineTweakerAPI.logError("Invalid HEX color in array: '" + hex + "'. Using 0.");
                        list.add(0);
                    }
                }
            }
        }
        return list;
    }

    /**
     * Registers a color list globally under a shared identity handle string.
     * @param id     Unique identifier string
     * @param colors The array sequence of colors (supports integers and HEX strings)
     */
    @ZenMethod
    public static void createColorList(String id, String[] colors) {
        BRIDGE.callCreateColorList(id, parseStringArray(colors));
    }

    /**
     * Defines a named ResourceLocation resource structure path.
     * @param id     Resource target key string
     * @param domain Mod file system resource domain
     * @param path   Relative domain path mapping asset location
     */
    @ZenMethod
    public static void createResource(String id, String domain, String path) {
        BRIDGE.callCreateResource(id, domain, path);
    }

    /**
     * Allocates a new structural layout theme registry builder profile.
     * @param id Unique identification lookup key
     * @return ZenScript compatible wrapper binding ThemeBuilder functionality
     */
    @ZenMethod
    public static ZenTheme createTheme(String id) {
        return new ZenTheme(BRIDGE.callCreateTheme(id));
    }

    /**
     * Allocates a primitive style layer definition block registry tracking properties.
     * @param id Unique identification lookup key
     * @return ZenScript compatible wrapper binding StyleBuilder functionality
     */
    @ZenMethod
    public static ZenStyle createStyle(String id) {
        return new ZenStyle(BRIDGE.callCreateStyle(id));
    }

    /**
     * Creates a text rendering divider layout configuration setup component.
     * @param id Unique identification lookup key
     * @return ZenScript compatible wrapper binding SeparatorBuilder functionality
     */
    @ZenMethod
    public static ZenSeparator createSeparator(String id) {
        return new ZenSeparator(BRIDGE.callCreateSeparator(id));
    }

    /**
     * Appends an animated looping background border overlay boundary track setup.
     * @param id Unique identification lookup key
     * @return ZenScript compatible wrapper binding InnerLineBuilder functionality
     */
    @ZenMethod
    public static ZenInnerLine createInnerLine(String id) {
        return new ZenInnerLine(BRIDGE.callCreateInnerLine(id));
    }

    /**
     * Sets up an ambient backlighting volumetric light wash shader canvas.
     * @param id Unique identification lookup key
     * @return ZenScript compatible wrapper binding GlowOutlineBuilder functionality
     */
    @ZenMethod
    public static ZenGlowOutline createGlowOutline(String id) {
        return new ZenGlowOutline(BRIDGE.callCreateGlowOutline(id));
    }

    /**
     * Maps an asset file structure location into a 9-patch background window border frame.
     * @param id Unique identification lookup key
     * @return ZenScript compatible wrapper binding FrameBuilder functionality
     */
    @ZenMethod
    public static ZenFrame createFrame(String id) {
        return new ZenFrame(BRIDGE.callCreateFrame(id));
    }

    /**
     * ZenScript property getter mapping for 'lastResource' property lookups.
     * * @return Pure string mapping containing the internal ResourceLocation value
     */
    public static String getLastResource() {
        ResourceLocation res = BRIDGE.callGetLastResource();
        return res == null ? "" : res.toString();
    }

    /**
     * Separate explicit ZenMethod ensuring 'fetchLastResource()' works as a standard function loop.
     * * @return String formatted IData mapping internal ResourceLocation paths
     */
    @ZenMethod
    public static String fetchLastResource() {
        return getLastResource();
    }

    static ResourceLocation fetchLastResourceInternal() {
        return BRIDGE.callGetLastResource();
    }

    /**
     * Binds a completed theme scheme universally over every registry item loaded by a specified mod.
     * @param theme The configured input theme profile container
     * @param modId Target mod registry domain identity key
     */
    @ZenMethod
    public static void buildForMod(ZenTheme theme, String modId) {
        if (theme != null && modId != null) {
            MineTweakerAPI.apply(new TooltipBuildAction(theme, modId, TooltipBuildAction.BuildType.MOD, -1));
        }
    }

    /**
     * Binds a completed theme layout definition targeting a single distinct game item definition string.
     * @param theme    The configured input theme profile container
     * @param itemName Minecraft registry identification tag lookup path
     * @param damage Item damage
     */
    @ZenMethod
    public static void buildForItem(ZenTheme theme, String itemName, int damage) {
        if (theme != null && itemName != null) {
            MineTweakerAPI.apply(new TooltipBuildAction(theme, itemName, TooltipBuildAction.BuildType.ITEM, damage));
        }
    }

    /**
     * Binds a completed theme profile definition evaluating directly against standalone plain tooltip text matching loops.
     * @param theme The configured input theme profile container
     * @param text  The plain text matching criteria token string
     */
    @ZenMethod
    public static void buildForText(ZenTheme theme, String text) {
        if (theme != null && text != null) {
            MineTweakerAPI.apply(new TooltipBuildAction(theme, text, TooltipBuildAction.BuildType.TEXT, -1));
        }
    }

    /**
     * Binds a completed theme profile tracking active items matching conditional structural compound NBT criteria.
     * @param theme The configured input theme profile container
     * @param data  ZenScript key-value pair tree map representing NBT structures
     */
    @ZenMethod
    public static void buildForNBT(ZenTheme theme, IData data) {
        if (theme == null || data == null) {
            return;
        }
        NBTBase parsedNBT = convertDataToNBT(data);
        if (parsedNBT instanceof NBTTagCompound) {
            MineTweakerAPI.apply(new TooltipBuildAction(theme, parsedNBT, TooltipBuildAction.BuildType.NBT, -1));
        }
    }

    /**
     * Recursively transforms complex ZenScript mapping dictionaries back down into primitive Minecraft NBT storage blocks.
     */
    private static NBTBase convertDataToNBT(IData data) {
        if (data == null) {
            return null;
        }
        if (data.asMap() != null) {
            NBTTagCompound compound = new NBTTagCompound();
            for (Map.Entry<String, IData> entry : data.asMap().entrySet()) {
                NBTBase child = convertDataToNBT(entry.getValue());
                if (child != null) {
                    compound.setTag(entry.getKey(), child);
                }
            }
            return compound;
        }
        if (data.asList() != null) {
            NBTTagList list = new NBTTagList();
            for (IData element : data.asList()) {
                NBTBase child = convertDataToNBT(element);
                if (child != null) {
                    list.appendTag(child);
                }
            }
            return list;
        }
        if (data instanceof DataString) {
            return new NBTTagString(data.asString());
        }
        if (data instanceof DataInt) {
            return new NBTTagInt(data.asInt());
        }
        if (data instanceof DataLong) {
            return new NBTTagLong(data.asLong());
        }
        if (data instanceof DataFloat) {
            return new NBTTagFloat(data.asFloat());
        }
        if (data instanceof DataDouble) {
            return new NBTTagDouble(data.asDouble());
        }
        if (data instanceof DataShort) {
            return new NBTTagShort(data.asShort());
        }
        if (data instanceof DataByte) {
            return new NBTTagByte(data.asByte());
        }
        if (data instanceof DataBool) {
            return new NBTTagByte((byte) (data.asBool() ? 1 : 0));
        }
        return new NBTTagString(data.asString());
    }
}
