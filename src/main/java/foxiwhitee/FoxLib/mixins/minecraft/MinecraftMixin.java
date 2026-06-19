package foxiwhitee.FoxLib.mixins.minecraft;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(
    value = Minecraft.class,
    priority = 1100
)
public abstract class MinecraftMixin {

    @Dynamic
    @ModifyConstant(
        method = {
            "startGame",
            "func_71384_a"
        },
        require = 0,
        remap = false,
        constant = @Constant(stringValue = "textures/font/ascii.png")
    )
    private String redirectFontTexturePath(String original) {
        return "textures/font/ascii_cyrillic.png";
    }
}
