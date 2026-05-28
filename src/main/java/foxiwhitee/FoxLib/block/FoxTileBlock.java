package foxiwhitee.FoxLib.block;

import foxiwhitee.FoxLib.FoxLib;
import foxiwhitee.FoxLib.api.FoxLibApi;
import foxiwhitee.FoxLib.tile.FoxBaseTile;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

@SuppressWarnings("unused")
public class FoxTileBlock extends FoxBaseBlock implements ITileEntityProvider {
    private Class<? extends TileEntity> tileEntityType;
    private boolean hasTile;

    public FoxTileBlock(Material material, String modID, String name, Class<? extends TileEntity> tileEntityClass) {
        super(material, modID, name);
        this.tileEntityType = tileEntityClass;
        this.hasTile = tileEntityClass != null;
    }

    public FoxTileBlock(String modID, String name, Class<? extends TileEntity> tileEntityClass) {
        this(Material.rock, modID, name, tileEntityClass);
    }

    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int p_149727_6_, float p_149727_7_, float p_149727_8_, float p_149727_9_) {
        TileEntity tile = world.getTileEntity(x, y, z);
        int id = FoxLibApi.instance.registries().registerGui().getIdByBlock(getClass());
        if (hasTile && tileEntityType.isInstance(tile) && id != -1) {
            if (!world.isRemote) {
                player.openGui(FoxLib.instance, id, world, x, y, z);
            }
            return true;
        }
        return hasTile;
    }

    public TileEntity getTileEntity(final IBlockAccess w, final int x, final int y, final int z) {
        TileEntity te = w.getTileEntity(x, y, z);
        if (this.tileEntityType != null && this.tileEntityType.isInstance(te)) {
            return te;
        }

        return this.tileEntityType == null ? te : null;
    }

    public void setTileEntityType(Class<? extends TileEntity> tileEntityType) {
        this.tileEntityType = tileEntityType;
        this.hasTile = tileEntityType != FoxBaseTile.class;
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        Constructor<? extends TileEntity> constructor;
        try {
            constructor = tileEntityType.getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        constructor.setAccessible(true);
        try {
            return constructor.newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
