package foxiwhitee.FoxLib.client.tooltips.theme;

import cpw.mods.fml.common.registry.GameData;
import foxiwhitee.FoxLib.client.tooltips.theme.elements.object.TooltipObjectKey;
import foxiwhitee.FoxLib.client.tooltips.theme.elements.object.ObjectType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;

import java.util.*;

public class ThemeRegister {
    private static final Comparator<TooltipTheme> THEME_COMPARATOR = Comparator.comparing(TooltipTheme::getForObject).thenComparing(TooltipTheme::getId);

    public static final Map<TooltipTheme, TooltipTheme> TOOLTIPS = new TreeMap<>(THEME_COMPARATOR);

    private static final int CACHE_SIZE = 128;
    private static final Map<TooltipObjectKey, TooltipTheme> CACHE = Collections.synchronizedMap(
        new LinkedHashMap<>(CACHE_SIZE, 0.75F, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<TooltipObjectKey, TooltipTheme> eldest) {
                return size() > CACHE_SIZE;
            }
        }
    );


    public static TooltipTheme findTheme(ItemStack stack, List<String> lines) {
        boolean stackIsNull = stack == null || stack.getItem() == null;

        TooltipObjectKey cacheKey = !stackIsNull ? new TooltipObjectKey(stack) : new TooltipObjectKey(lines.get(0));
        TooltipTheme cached = CACHE.get(cacheKey);
        if (cached != null) {
            return cached;
        }

        for (TooltipTheme theme : TOOLTIPS.keySet()) {
            if (theme.getForObject() != ObjectType.TEXT && stackIsNull) {
                continue;
            }
            if (matchTheme(theme, stack, lines)) {
                CACHE.put(cacheKey, theme);
                return theme;
            }
        }

        return null;
    }

    private static boolean matchTheme(TooltipTheme theme, ItemStack stack, List<String> lines) {
        return switch (theme.getForObject()) {
            case TEXT -> lines.get(0).equals(StatCollector.translateToLocal((String) theme.getObject()));
            case NBT -> checkNBT(stack.getTagCompound(), (NBTTagCompound) theme.getObject());
            case ITEM -> theme.getObject().equals(getFullName(stack));
            case MOD -> theme.getObject().equals(getModId(stack));
        };
    }

    private static boolean checkNBT(NBTTagCompound itemNbt, NBTTagCompound themeNbt) {
        if (themeNbt == null || themeNbt.hasNoTags()) {
            return true;
        }

        if (itemNbt == null || itemNbt.hasNoTags()) {
            return false;
        }

        Set<String> themeKeys = themeNbt.func_150296_c();

        for (String key : themeKeys) {
            if (!itemNbt.hasKey(key)) {
                return false;
            }

            NBTBase themeTag = themeNbt.getTag(key);
            NBTBase itemTag = itemNbt.getTag(key);

            if (themeTag.getId() != itemTag.getId()) {
                return false;
            }

            if (themeTag instanceof NBTTagCompound) {
                if (!checkNBT((NBTTagCompound) itemTag, (NBTTagCompound) themeTag)) {
                    return false;
                }
            } else {
                if (!themeTag.equals(itemTag)) {
                    return false;
                }
            }
        }

        return true;
    }

    public static void registerTheme(TooltipTheme theme) {
        if (theme == null || theme.getId() == null || theme.getForObject() == null) {
            throw new IllegalArgumentException("Theme, its ID and Type cannot be null!");
        }

        TOOLTIPS.put(theme, theme);

        if (!CACHE.isEmpty()) {
            CACHE.clear();
        }
    }

    public static String getModId(ItemStack stack) {
        if (stack == null || stack.getItem() == null) {
            return "minecraft";
        }

        String registryName = getFullName(stack);

        if (registryName != null && registryName.contains(":")) {
            return registryName.split(":")[0];
        }

        return "minecraft";
    }

    public static String getFullName(ItemStack stack) {
        if (stack == null || stack.getItem() == null) {
            return "";
        }

        return GameData.getItemRegistry().getNameForObject(stack.getItem());
    }
}
