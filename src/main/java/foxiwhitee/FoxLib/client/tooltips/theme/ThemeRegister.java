package foxiwhitee.FoxLib.client.tooltips.theme;

import cpw.mods.fml.common.registry.GameData;
import foxiwhitee.FoxLib.FoxLib;
import foxiwhitee.FoxLib.client.tooltips.theme.elements.frame.FrameStyle;
import foxiwhitee.FoxLib.client.tooltips.theme.elements.frame.ImageFrame;
import foxiwhitee.FoxLib.client.tooltips.theme.elements.innerline.InnerLineConfig;
import foxiwhitee.FoxLib.client.tooltips.theme.elements.object.TooltipObjectKey;
import foxiwhitee.FoxLib.client.tooltips.theme.elements.object.ObjectType;
import foxiwhitee.FoxLib.client.tooltips.theme.elements.particle.ParticleConfig;
import foxiwhitee.FoxLib.client.tooltips.theme.elements.particle.ParticleKind;
import foxiwhitee.FoxLib.client.tooltips.theme.elements.separator.SeparatorConfig;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
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
        if (TOOLTIPS.isEmpty()) { // todo
            FrameStyle style = new FrameStyle(0x141A30, 0x080C20, 0x8C7CD6, 0x3A2A6A, 0x9C8FE0, 0xCFC2FF, 0.94f);
            ParticleConfig cfg = new ParticleConfig(ParticleKind.STAR, 24, 0.08F, 30.0F, 0.70F, 0.70F, 3.0F, 4.0F, 0.0F);
            SeparatorConfig separators = new SeparatorConfig();
            List<Integer> colors = Arrays.asList(0x9C8FE0, 0xCFC2FF, 0x9C8FE0, 0x4533A8);
            separators.addSeparator(0, colors);
            ImageFrame frame = new ImageFrame(new ResourceLocation(FoxLib.MODID, "textures/frames/test.png"), 96, 128);
            InnerLineConfig innerLineConfig = new InnerLineConfig(1, new int[]{0xFFFF00FF, 0xFF0000FF, 0xFF00FFFF});
            TooltipTheme theme1 = new TooltipTheme("test", ObjectType.MOD, "minecraft", style,
                cfg, separators, true, false, false, frame, innerLineConfig);
            registerTheme(theme1);

            style = new FrameStyle(0x1A0A30, 0x0C0420, 0x8030C0, 0x301050, 0xB060FF, 0xE0C0FF, 0.94f);
            cfg = new ParticleConfig(ParticleKind.EMBER, 24, 0.08F, 30.0F, 0.70F, 0.70F, 3.0F, 4.0F, 0.0F);
            separators = new SeparatorConfig();
            colors = Arrays.asList(0xB060FF, 0xE0C0FF, 0xB060FF, 0x8a14ff);
            separators.addSeparator(0, colors);
            TooltipTheme theme2 = new TooltipTheme("stone", ObjectType.ITEM,  "minecraft:stone", style,
                cfg, separators, true, true, false, frame, innerLineConfig);
            registerTheme(theme2);

            style = new FrameStyle(0x281C12, 0x140A06, 0xC08040, 0x604018, 0xE0A050, 0xFFD080, 0.94f);
            cfg = new ParticleConfig(ParticleKind.SPARK, 24, 0.08F, 30.0F, 0.70F, 0.70F, 3.0F, 4.0F, 0.0F);
            separators = new SeparatorConfig();
            colors = Arrays.asList(0xE0A050, 0xFFD080, 0xE0A050, 0x822f17);
            separators.addSeparator(0, colors);
            NBTTagCompound tag = new NBTTagCompound();
            tag.setString("rarity", "rare");
            TooltipTheme theme3 = new TooltipTheme("stone", ObjectType.NBT,  tag, style,
                cfg, separators, true, true, false, null, innerLineConfig);
            registerTheme(theme3);

            TooltipTheme theme4 = new TooltipTheme("tab", ObjectType.TEXT, "itemGroup.appliedenergistics2", style,
                cfg, null, false, true, false, null, innerLineConfig);
            registerTheme(theme4);
        }
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
