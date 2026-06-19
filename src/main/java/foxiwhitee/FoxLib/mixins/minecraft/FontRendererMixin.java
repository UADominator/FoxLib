package foxiwhitee.FoxLib.mixins.minecraft;

import net.minecraft.client.gui.FontRenderer;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(FontRenderer.class)
public abstract class FontRendererMixin {

    @Dynamic
    @ModifyConstant(
        // TODO: fuck reobf in dev or real minecraft environment. here only 3 methods
        method = {
            "func_78278_a",
            "func_78263_a",
            "func_78255_a",
            "renderCharAtPos",
            "renderStringAtPos",
            "getCharWidth"
        },
        require = 0,
        remap = false,
        constant = @Constant(stringValue = "ÀÁÂÈÊËÍÓÔÕÚßãõğİıŒœŞşŴŵžȇ\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000ÇüéâäàåçêëèïîìÄÅÉæÆôöòûùÿÖÜø£Ø×ƒáíóúñÑªº¿®¬½¼¡«»░▒▓│┤╡╢╖╕╣║╗╝╜╛┐└┴┬├─┼╞╟╚╔╩╦╠═╬╧╨╤╥╙╘╒╓╫╪┘┌█▄▌▐▀αβΓπΣσμτΦΘΩδ∞∅∈∩≡±≥≤⌠⌡÷≈°∙·√ⁿ²■\u0000")
    )
    private String redirectFontCharacters(String original) {
        return
            "\u0000☺☻♥♦♣♠•◘○◙♂♀♪♫☼" +
                "►◄↕‼¶§▬↨↑↓→←∟↔▲▼" +
                " !\"#$%&'()*+,-./" +
                "0123456789:;<=>?" +
                "@ABCDEFGHIJKLMNO" +
                "PQRSTUVWXYZ[\\]^_" +
                "`abcdefghijklmno" +
                "pqrstuvwxyz{|}~⌂" +
                "АБВГДЕЖЗИЙКЛМНОП" +
                "РСТУФХЦЧШЩҐІЬЄЮЯ" +
                "абвгдежзийклмноп" +
                "рстуфхцчшщґіьєюя" +
                "Її\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u2116■" +
                "░▒▓│┤╡╢╖╕╣║╗╝╜╛┐" +
                "└┴┬├─┼╞╟╚╔╩╦╠═╬╧" +
                "╨╤╥╙╘╒╓╫╪┘┌█▄▌▐▀"
            ;
    }
}
