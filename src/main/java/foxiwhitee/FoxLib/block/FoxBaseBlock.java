package foxiwhitee.FoxLib.block;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import foxiwhitee.FoxLib.api.orientable.IOrientable;
import foxiwhitee.FoxLib.api.orientable.RotationHelper;
import foxiwhitee.FoxLib.tile.FoxBaseInvTile;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FoxBaseBlock extends Block {
    private static int staticRenderId;
    protected IIcon topIcon;
    protected IIcon downIcon;
    protected IIcon frontIcon;
    protected IIcon backIcon;
    protected IIcon eastIcon;
    protected IIcon westIcon;

    protected final String name;
    protected int renderId = 0;

    public FoxBaseBlock(String modID, String name) {
        super(Material.rock);
        this.name = name;
        this.setBlockName(name);
        this.setBlockTextureName(modID + ":" + getFolder() + name);
        this.lightOpacity = 1;
        this.setHardness(2.0F);
        this.setResistance(10.0F);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register) {
        String base = this.getTextureName();
        this.topIcon = register.registerIcon(base);
        this.downIcon = addIfExists(register, base + "Down", topIcon);
        IIcon sideIcon = addIfExists(register, base + "Side", topIcon);
        this.frontIcon = addIfExists(register, base + "Front", sideIcon);
        this.backIcon = addIfExists(register, base + "Back", sideIcon);
        this.eastIcon = addIfExists(register, base + "East", sideIcon);
        this.westIcon = addIfExists(register, base + "West", sideIcon);
    }

    private IIcon addIfExists(IIconRegister register, String name, IIcon defaultIcon) {
        if (exists(name)) {
            return register.registerIcon(name);
        } else {
            return defaultIcon;
        }
    }

    private boolean exists(String texturePath) {
        String domain = texturePath.split(":")[0];
        String path = texturePath.split(":")[1];
        return getClass().getResource("/assets/" + domain + "/textures/blocks/" + path + ".png") != null;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int metadata) {
        return switch (side) {
            case 0 -> downIcon;
            case 2 -> backIcon;
            case 3 -> frontIcon;
            case 4 -> westIcon;
            case 5 -> eastIcon;
            default -> topIcon;
        };
    }

    public IIcon getIcon(IBlockAccess w, int x, int y, int z, int s) {
        TileEntity te = w.getTileEntity(x, y, z);
        if (te instanceof IOrientable ori) {
            ForgeDirection sideWorld = ForgeDirection.getOrientation(s);
            ForgeDirection logicalSide = mapRotation(ori, sideWorld);
            return getIcon(logicalSide.ordinal(), w.getBlockMetadata(x, y, z));
        }
        return getIcon(s, w.getBlockMetadata(x, y, z));
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof IOrientable tile && tile.canBeRotated()) {
            ForgeDirection forward;
            ForgeDirection up;

            int l = MathHelper.floor_double((double)(entity.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
            ForgeDirection horizontal = (l == 0) ? ForgeDirection.SOUTH : (l == 1) ? ForgeDirection.WEST : (l == 2) ? ForgeDirection.NORTH : ForgeDirection.EAST;

            if (entity.rotationPitch > 65.0F && hasUpDownRotate()) {
                forward = ForgeDirection.UP;
                up = horizontal;
            } else if (entity.rotationPitch < -65.0F && hasUpDownRotate()) {
                forward = ForgeDirection.DOWN;
                up = horizontal.getOpposite();
            } else {
                forward = horizontal.getOpposite();
                up = ForgeDirection.UP;
            }

            tile.setOrientation(forward, up);
        }
    }

    public final boolean rotateBlock(final World w, final int x, final int y, final int z, final ForgeDirection axis) {
        final IOrientable rotatable = this.getOrientable(w, x, y, z);

        if (rotatable != null && rotatable.canBeRotated()) {
            ForgeDirection forward = rotatable.getForward();
            ForgeDirection up = rotatable.getUp();

            for (int rs = 0; rs < 4; rs++) {
                forward = RotationHelper.rotateAround(forward, axis);
                up = RotationHelper.rotateAround(up, axis);

                rotatable.setOrientation(forward, up);
                return true;
            }
        }

        return super.rotateBlock(w, x, y, z, axis);
    }

    public IOrientable getOrientable(final IBlockAccess w, final int x, final int y, final int z) {
        TileEntity tile = getTileEntity(w, x, y, z);
        if (tile instanceof IOrientable ori) {
            return ori;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public TileEntity getTileEntity(final IBlockAccess w, final int x, final int y, final int z) {
        return w.getTileEntity(x, y, z);
    }

    public ForgeDirection mapRotation(IOrientable ori, ForgeDirection dir) {
        ForgeDirection forward = ori.getForward();
        ForgeDirection up = ori.getUp();
        if (forward != null && up != null) {
            ForgeDirection west = getWest(forward, up);

            if (dir == forward) {
                return ForgeDirection.SOUTH;
            } else if (dir == forward.getOpposite()) {
                return ForgeDirection.NORTH;
            } else if (dir == up) {
                return ForgeDirection.UP;
            } else if (dir == up.getOpposite()) {
                return ForgeDirection.DOWN;
            } else if (dir == west) {
                return ForgeDirection.WEST;
            } else if (dir == west.getOpposite()) {
                return ForgeDirection.EAST;
            } else {
                return ForgeDirection.UNKNOWN;
            }
        } else {
            return dir;
        }
    }

    private ForgeDirection getWest(ForgeDirection forward, ForgeDirection up) {
        int west_x = forward.offsetY * up.offsetZ - forward.offsetZ * up.offsetY;
        int west_y = forward.offsetZ * up.offsetX - forward.offsetX * up.offsetZ;
        int west_z = forward.offsetX * up.offsetY - forward.offsetY * up.offsetX;
        ForgeDirection west = ForgeDirection.UNKNOWN;

        for(ForgeDirection dx : ForgeDirection.VALID_DIRECTIONS) {
            if (dx.offsetX == west_x && dx.offsetY == west_y && dx.offsetZ == west_z) {
                west = dx;
            }
        }
        return west;
    }

    public void breakBlock(World world, int x, int y, int z, Block block, int b) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof FoxBaseInvTile invTile) {
            ArrayList<ItemStack> drops = new ArrayList<>();
            invTile.getDrops(world, x, y, z, drops);
            spawnDrops(world, x, y, z, drops);
        }
        super.breakBlock(world, x, y, z, block, b);
    }

    public static void spawnDrops(World w, int x, int y, int z, List<ItemStack> drops) {
        if (FMLCommonHandler.instance().getEffectiveSide().isServer()) {
            for(ItemStack i : drops) {
                if (i != null && i.stackSize > 0) {
                    double offset_x = ((double) (getRandomInt() % 32 - 16) / 82);
                    double offset_y = ((double) (getRandomInt() % 32 - 16) / 82);
                    double offset_z = ((double) (getRandomInt() % 32 - 16) / 82);
                    EntityItem ei = new EntityItem(w, 0.5 + offset_x + x, 0.5 + offset_y + y, 0.2 + offset_z + z, i.copy());
                    w.spawnEntityInWorld(ei);
                }
            }
        }
    }

    private static int getRandomInt() {
        return Math.abs(new Random().nextInt());
    }

    @Override
    public final int getRenderType() {
        return renderId == 0 ? staticRenderId : renderId;
    }

    protected boolean hasUpDownRotate() {
        return false;
    }

    public String getFolder() {
        return "";
    }

    public static void setStaticRenderId(int staticRenderId) {
        FoxBaseBlock.staticRenderId = staticRenderId;
    }
}
