package foxiwhitee.FoxLib.mixins.nei;

import codechicken.nei.guihook.GuiContainerManager;
import foxiwhitee.FoxLib.client.tooltips.TooltipEngine;
import net.minecraft.client.gui.FontRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = GuiContainerManager.class, remap = false)
public abstract class GuiContainerManagerMixin {

    @Inject(
        method = "drawPagedTooltip",
        at = @At("HEAD"),
        cancellable = true
    )
    private static void onNeiRenderToolTip(FontRenderer font, int x, int y, List<String> list, CallbackInfo ci) {
        if (TooltipEngine.renderNeiTooltip(x, y, list)) {
            ci.cancel();
        }
    }

}
