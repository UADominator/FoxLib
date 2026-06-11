package foxiwhitee.FoxLib.client.tooltips.theme;

import cpw.mods.fml.common.registry.GameData;
import foxiwhitee.FoxLib.FoxLib;
import foxiwhitee.FoxLib.client.tooltips.theme.elements.frame.FrameStyle;
import foxiwhitee.FoxLib.client.tooltips.theme.elements.frame.ImageFrame;
import foxiwhitee.FoxLib.client.tooltips.theme.elements.innerline.InnerLineConfig;
import foxiwhitee.FoxLib.client.tooltips.theme.elements.particle.ParticleConfig;
import foxiwhitee.FoxLib.client.tooltips.theme.elements.particle.ParticleKind;
import foxiwhitee.FoxLib.client.tooltips.theme.elements.separator.SeparatorConfig;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.*;

public class ThemeRegister {
    public static final Map<String, TooltipTheme> TOOLTIPS = new HashMap<>(); // todo

    public static TooltipTheme findTheme(ItemStack stack) {
        if (TOOLTIPS.isEmpty()) {
            FrameStyle style = new FrameStyle(0x141A30, 0x080C20, 0x8C7CD6, 0x3A2A6A, 0x9C8FE0, 0x4533A8, 0xCFC2FF, 0xC8B040, 0xFFE090);
            ParticleConfig cfg = new ParticleConfig(ParticleKind.STAR, 24, 0.08F, 30.0F, 0.70F, 0.70F, 3.0F, 4.0F, 0.0F);
            SeparatorConfig separators = new SeparatorConfig();
            List<Integer> colors = Arrays.asList(0x9C8FE0, 0xCFC2FF, 0x9C8FE0, 0x4533A8);
            separators.addSeparator(0, colors);
            ImageFrame frame = new ImageFrame(new ResourceLocation(FoxLib.MODID, "textures/frames/test.png"), 96, 128);
            InnerLineConfig innerLineConfig = new InnerLineConfig(1, new int[]{0xFFFF00FF, 0xFF0000FF, 0xFF00FFFF});
            ThemeRegister.TOOLTIPS.put("minecraft", new TooltipTheme("test", ForObject.MOD, "minecraft", style,
                cfg, separators, true, false, frame, innerLineConfig));
        }
        if (stack == null) {
            return null;
        }
        String name = getFullName(stack);
        TooltipTheme theme = TOOLTIPS.get(name);
        if (theme == null) {
            return TOOLTIPS.get(getModId(stack));
        }
        return theme;
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
