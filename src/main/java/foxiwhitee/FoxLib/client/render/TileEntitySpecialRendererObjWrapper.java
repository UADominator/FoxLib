package foxiwhitee.FoxLib.client.render;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

import java.util.HashMap;

@SideOnly(Side.CLIENT)
@SuppressWarnings("unused")
public abstract class TileEntitySpecialRendererObjWrapper<T extends TileEntity> extends TileEntitySpecialRenderer {
    private final Class<T> tileClass;

    private final HashMap<String, Integer> partLists = new HashMap<>();

    private final IModelCustom model;

    private final ResourceLocation texture;

    public TileEntitySpecialRendererObjWrapper(Class<T> tileClass, ResourceLocation obj, ResourceLocation texture) {
        this.tileClass = tileClass;
        this.model = AdvancedModelLoader.loadModel(obj);
        this.texture = texture;
    }

    public TileEntitySpecialRendererObjWrapper(Class<T> tileClass, String modid, String obj, String texture) {
        this(tileClass, new ResourceLocation(modid, obj), new ResourceLocation(modid, texture));
    }

    public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float f) {
        if (tile.getClass().isAssignableFrom(this.tileClass))
            renderAt(this.tileClass.cast(tile), x, y, z, f);
    }

    public abstract void renderAt(T paramT, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4);

    @SuppressWarnings("all")
    protected void createList(String part) {
        if (part == null) part = "all";
        this.partLists.put(part, 1);
    }

    @SuppressWarnings("all")
    protected void renderPart(String part) {
        if (part == null) part = "all";
        if (!this.partLists.containsKey(part)) return;
        if (part.equals("all")) {
            this.model.renderAll();
        } else {
            this.model.renderPart(part);
        }
    }

    public ResourceLocation getTexture() {
        return this.texture;
    }

    public IModelCustom getModel() {
        return this.model;
    }

    public void bindTexture() {
        bindTexture(this.texture);
    }

    public void bindTexture(ResourceLocation texture) {
        (Minecraft.getMinecraft()).renderEngine.bindTexture(texture);
    }
}
