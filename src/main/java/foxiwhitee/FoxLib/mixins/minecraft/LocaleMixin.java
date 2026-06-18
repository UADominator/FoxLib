package foxiwhitee.FoxLib.mixins.minecraft;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.Locale;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = Locale.class, priority = 1100)
public abstract class LocaleMixin {

    @Shadow
    private boolean field_135029_d;

    /**
     * @author foxiwhitee
     * @reason Add support for the Ukrainian locale for Force Unicode
     */
    @Overwrite
    public boolean isUnicode() {
        Minecraft mc = Minecraft.getMinecraft();
        if ("uk_UA".equals(mc.gameSettings.language)) {
            return mc.gameSettings.forceUnicodeFont;
        }
        return this.field_135029_d;
    }
}
