package foxiwhitee.FoxLib.mixins.minecraft;

import foxiwhitee.FoxLib.client.tooltips.TooltipEngine;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(GuiScreen.class)
public abstract class GuiScreenMixin {

    @Inject(
        method = {"func_146279_a(Ljava/lang/String;II)V", "drawCreativeTabHoveringText"},
        at = @At("HEAD"),
        cancellable = true,
        remap = false
    )
    private void onDrawCreativeTabHoveringText(String tabName, int mouseX, int mouseY, CallbackInfo ci) {
        if (TooltipEngine.renderCreativeTab(this, tabName, mouseX, mouseY)) {
            ci.cancel();
        }
    }

    @Inject(
        method = "func_146283_a(Ljava/util/List;II)V",
        at = @At("HEAD"),
        cancellable = true
    )
    private void onDrawHoveringTextSimple(List<String> textLines, int x, int y, CallbackInfo ci) {
        if (TooltipEngine.renderVanillaTooltipSimply(this, textLines, x, y)) {
            ci.cancel();
        }
    }

    @Inject(
        method = "drawHoveringText",
        at = @At("HEAD"),
        cancellable = true,
        remap = false
    )
    private void onDrawHoveringTextComplex(List<String> textLines, int x, int y, FontRenderer font, CallbackInfo ci) {
        if (TooltipEngine.renderVanillaTooltip(this, textLines, x, y, font)) {
            ci.cancel();
        }
    }

    @Inject(
        method = "renderToolTip",
        at = @At("HEAD")
    )
    private void onRenderToolTip(ItemStack stack, int x, int y, CallbackInfo ci) {
        TooltipEngine.setHoveredStack(stack);
    }
}
