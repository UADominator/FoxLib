package foxiwhitee.FoxLib.asm;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import foxiwhitee.FoxLib.FoxLib;

import java.util.Map;

@SuppressWarnings("unused")
@IFMLLoadingPlugin.Name(FoxLib.MODNAME)
@IFMLLoadingPlugin.MCVersion("1.7.10")
@IFMLLoadingPlugin.TransformerExclusions({ "foxiwhitee.FoxLib.asm." })
@IFMLLoadingPlugin.SortingIndex(11000)
public class FoxLibAsmCore implements IFMLLoadingPlugin {

    @Override
    public String[] getASMTransformerClass() {
        return new String[] {
            "foxiwhitee.FoxLib.asm.transform.GuiScreenTransformer",
            "foxiwhitee.FoxLib.asm.transform.GuiContainerTransformer",
            "foxiwhitee.FoxLib.asm.transform.NeiTooltipTransformer"
        };
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {
    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
