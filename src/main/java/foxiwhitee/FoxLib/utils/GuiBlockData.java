package foxiwhitee.FoxLib.utils;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.ReflectionHelper;
import foxiwhitee.FoxLib.api.IGuiBlockData;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;

import java.lang.reflect.Constructor;
import java.util.Objects;

public class GuiBlockData implements IGuiBlockData {
    private final Class<? extends Block> block;
    private final Class<? extends TileEntity> tile;
    private final Class<? extends Container> container;
    private final String gui;

    public GuiBlockData(Class<? extends Block> block, Class<? extends TileEntity> tile, Class<? extends Container> container, String gui) {
        this.block = block;
        this.tile = tile;
        this.container = container;
        this.gui = gui;
    }

    @Override
    public Class<? extends Block> getBlock() {
        return block;
    }

    @Override
    public Class<? extends TileEntity> getTile() {
        return tile;
    }

    @Override
    public Class<? extends Container> getContainer() {
        return container;
    }

    @Override
    public String getGui() {
        return gui;
    }

    @Override
    public Container constructContainer(EntityPlayer player, TileEntity te) {
        if (correctTile(te)) {
            return null;
        }
        try {
            Constructor<?>[] c = this.container.getConstructors();
            if (c.length == 0) {
                throw new IllegalStateException("Invalid Container Class");
            } else {
                Constructor<?> target = this.findConstructor(c, player, te);
                if (target == null) {
                    throw new IllegalStateException("Cannot find " + this.container.getName() + "( " + this.typeName(player) + ", " + this.typeName(te) + " )");
                } else {
                    return (Container) target.newInstance(player, te);
                }
            }
        } catch (Throwable t) {
            throw new IllegalStateException(t);
        }
    }

    private boolean correctTile(TileEntity te) {
        if (this.tile == null) {
            throw new IllegalArgumentException("Unsupported Tile");
        } else {
            return !this.tile.isInstance(te);
        }
    }

    @Override
    public Object constructGui(EntityPlayer player, TileEntity te) {
        if (correctTile(te)) {
            return null;
        }
        try {
            Class<?> clazz = getGuiClass();
            if (clazz == null) {
                return null;
            }
            Constructor<?>[] c = clazz.getConstructors();
            if (c.length == 0) {
                throw new IllegalStateException("Invalid Gui Class");
            } else {
                Container container = constructContainer(player, te);
                Constructor<?> target = this.findConstructor(c, container);
                if (target == null) {
                    throw new IllegalStateException("Cannot find " + clazz.getName() + "( " + this.typeName(container) + " )");
                } else {
                    return target.newInstance(container);
                }
            }
        } catch (Throwable t) {
            throw new IllegalStateException(t);
        }
    }

    private Class<?> getGuiClass() {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
            String start = this.container.getName();
            String guiClass = start.replaceFirst("container.", "client.gui.").replace(this.container.getSimpleName(), gui);
            if (start.equals(guiClass)) {
                throw new IllegalStateException("Unable to find gui class");
            }

            return ReflectionHelper.getClass(this.getClass().getClassLoader(), guiClass);
        }
        return null;
    }

    private Constructor<?> findConstructor(Constructor<?>[] c, EntityPlayer player, TileEntity te) {
        for(Constructor<?> con : c) {
            Class<?>[] types = con.getParameterTypes();
            if (types.length == 2 && types[0].isAssignableFrom(player.getClass()) && types[1].isAssignableFrom(te.getClass())) {
                return con;
            }
        }

        return null;
    }

    private Constructor<?> findConstructor(Constructor<?>[] c, Container container) {
        for(Constructor<?> con : c) {
            Class<?>[] types = con.getParameterTypes();
            if (types.length == 1 && types[0].isAssignableFrom(container.getClass())) {
                return con;
            }
        }

        return null;
    }

    private String typeName(Object inventory) {
        return inventory == null ? "NULL" : inventory.getClass().getName();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        GuiBlockData that = (GuiBlockData) o;
        return Objects.equals(block, that.block) && Objects.equals(tile, that.tile) && Objects.equals(container, that.container) && Objects.equals(gui, that.gui);
    }

    @Override
    public int hashCode() {
        return Objects.hash(block, tile, container, gui);
    }
}
