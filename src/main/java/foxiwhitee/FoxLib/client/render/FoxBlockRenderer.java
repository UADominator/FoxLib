package foxiwhitee.FoxLib.client.render;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import foxiwhitee.FoxLib.api.orientable.IOrientable;
import foxiwhitee.FoxLib.api.orientable.RotationHelper;
import foxiwhitee.FoxLib.block.FoxBaseBlock;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;
import org.lwjgl.opengl.GL11;

public class FoxBlockRenderer implements ISimpleBlockRenderingHandler {
    private final int renderId;

    public FoxBlockRenderer(int id) { this.renderId = id; }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
        if (!(block instanceof FoxBaseBlock)) return false;

        if (world.getTileEntity(x, y, z) instanceof IOrientable ori) {
            ForgeDirection f = ori.getForward();
            ForgeDirection u = ori.getUp();

            renderer.uvRotateBottom = RotationHelper.getUVRotation(0, f, u);
            renderer.uvRotateTop = RotationHelper.getUVRotation(1, f, u);
            renderer.uvRotateNorth = RotationHelper.getUVRotation(2, f, u);
            renderer.uvRotateSouth = RotationHelper.getUVRotation(3, f, u);
            renderer.uvRotateWest = RotationHelper.getUVRotation(4, f, u);
            renderer.uvRotateEast = RotationHelper.getUVRotation(5, f, u);
        }

        boolean result = renderer.renderStandardBlock(block, x, y, z);

        renderer.uvRotateBottom = renderer.uvRotateTop = renderer.uvRotateEast =
            renderer.uvRotateWest = renderer.uvRotateNorth = renderer.uvRotateSouth = 0;
        return result;
    }

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
        Tessellator tessellator = Tessellator.instance;
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);

        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, -1.0F, 0.0F);
        renderer.renderFaceYNeg(block, 0, 0, 0, block.getIcon(0, metadata));
        tessellator.draw();

        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 1.0F, 0.0F);
        renderer.renderFaceYPos(block, 0, 0, 0, block.getIcon(1, metadata));
        tessellator.draw();

        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, -1.0F);
        renderer.renderFaceZNeg(block, 0, 0, 0, block.getIcon(2, metadata));
        tessellator.draw();

        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, 1.0F);
        renderer.renderFaceZPos(block, 0, 0, 0, block.getIcon(3, metadata));
        tessellator.draw();

        tessellator.startDrawingQuads();
        tessellator.setNormal(-1.0F, 0.0F, 0.0F);
        renderer.renderFaceXNeg(block, 0, 0, 0, block.getIcon(4, metadata));
        tessellator.draw();

        tessellator.startDrawingQuads();
        tessellator.setNormal(1.0F, 0.0F, 0.0F);
        renderer.renderFaceXPos(block, 0, 0, 0, block.getIcon(5, metadata));
        tessellator.draw();

        GL11.glTranslatef(0.5F, 0.5F, 0.5F);
    }

    @Override public boolean shouldRender3DInInventory(int modelId) { return true; }
    @Override public int getRenderId() { return renderId; }
}
