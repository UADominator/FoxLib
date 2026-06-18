package foxiwhitee.FoxLib.mixins.minecraft;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(value = Minecraft.class, priority = 1100)
public abstract class MinecraftMixin {

    @ModifyConstant(
        method = "startGame",
        constant = @Constant(stringValue = "textures/font/ascii.png")
    )
    private String redirectFontTexturePath(String original) {
        return "textures/font/ascii_fat.png";
    }
}
