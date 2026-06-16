package foxiwhitee.FoxLib.client.tooltips.builder.test;

import foxiwhitee.FoxLib.FoxLib;
import foxiwhitee.FoxLib.client.tooltips.builder.TooltipConfigurationMethod;
import foxiwhitee.FoxLib.client.tooltips.builder.TooltipProcessor;
import net.minecraft.nbt.NBTTagCompound;

import java.util.Arrays;

//@TooltipConfiguration
@SuppressWarnings("all")
public class Test extends TooltipProcessor {

    @TooltipConfigurationMethod
    public void cfg() {
        createStyle("test")
            .bg(0x141A30, 0x080C20, 0.94f)
            .border(0x8C7CD6, 0x3A2A6A)
            .accent(0x9C8FE0, 0xCFC2FF);

        createColorList("separator")
            .addAll(Arrays.asList(0x9C8FE0, 0xCFC2FF, 0x9C8FE0, 0x4533A8));

        createColorList("innerLine")
            .addAll(Arrays.asList(0xFFFF00FF, 0xFF0000FF, 0xFF00FFFF));

        createColorList("glowOutline")
            .addAll(Arrays.asList(0xFF0000ff, 0xFFff0000));

        createSeparator("test")
            .applySeparator(0, getColors("separator"));

        createResource("frame", FoxLib.MODID, "textures/frames/test.png");

        createFrame("test")
            .data(getLastResource(), 128, 96);

        createInnerLine("test")
            .data(1, getColors("innerLine"));

        createGlowOutline("test")
            .data(6, 0.4f, getColors("glowOutline"));

        createTheme("test")
            .hasIcon()
            .enableParticles()
            .appendStyle(getLastStyle())
            .appendSeparator(getLastSeparator())
            .appendFrame(getLastFrame())
            .appendInnerLine(getLastInnerLine())
            .appendGlowOutline(getLastGlowOutline());

        buildForMod(getLastTheme(), "minecraft");


        createColorList("stoneSeparator")
            .addAll(Arrays.asList(0xB060FF, 0xE0C0FF, 0xB060FF, 0x8a14ff));

        buildForItem(createTheme("stone")
                .appendStyle(createStyle("stone")
                    .all(0x1A0A30, 0x0C0420, 0.94f, 0x8030C0, 0x301050, 0xB060FF, 0xE0C0FF))
                .appendSeparator(createSeparator("stone")
                    .applySeparator(0, getColors("stoneSeparator")))
                .hasIcon()
                .enableShadow()
                .enableParticles()
                .appendInnerLine(getLastInnerLine()),
            "minecraft:stone");

        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("rarity", "rare");

        createColorList("rareSeparator")
            .addAll(Arrays.asList(0xE0A050, 0xFFD080, 0xE0A050, 0x822f17));

        buildForNBT(createTheme("rare")
            .appendStyle(createStyle("rare")
                .all(0x281C12, 0x140A06, 0.94f, 0xC08040, 0x604018, 0xE0A050, 0xFFD080))
            .appendSeparator(createSeparator("rare")
                .applySeparator(0, getLastColors()))
            .hasIcon()
            .enableShadow()
            .enableParticles()
            .appendInnerLine(getLastInnerLine()),
            tag);

        buildForText(createTheme("tab")
                .enableShadow()
                .appendInnerLine(getLastInnerLine())
                .appendStyle(getLastStyle())
                .enableParticles(),
            "itemGroup.appliedenergistics2");
    }
}
