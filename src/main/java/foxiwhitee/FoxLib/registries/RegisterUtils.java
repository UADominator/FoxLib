package foxiwhitee.FoxLib.registries;

import com.google.common.reflect.ClassPath;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class RegisterUtils {
    public static <T> List<Class<? extends T>> findClasses(String pkg, Class<T> find) {
        try {
            return ClassPath.from(RegisterUtils.class.getClassLoader())
                    .getTopLevelClassesRecursive(pkg)
                    .stream()
                    .map(ClassPath.ClassInfo::load)
                    .filter(find::isAssignableFrom)
                    .filter(clazz -> !Modifier.isAbstract(clazz.getModifiers()))
                    .filter(clazz -> !clazz.isAnnotationPresent(Deprecated.class))
                    .filter(clazz -> !clazz.isInterface())
                    .sorted((a, b) -> a.getSimpleName().compareTo(b.getSimpleName()))
                    .map(clazz -> {
                        @SuppressWarnings("unchecked")
                        Class<? extends T> safeClass = (Class<? extends T>) clazz;
                        return safeClass;
                    })
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public static <T> List<Class<? extends T>> findClasses(String pkg, Class<T> find, String filter) {
        try {
            return ClassPath.from(RegisterUtils.class.getClassLoader())
                    .getTopLevelClassesRecursive(pkg)
                    .stream()
                    .filter(i -> i.getName().contains(filter))
                    .map(ClassPath.ClassInfo::load)
                    .filter(find::isAssignableFrom)
                    .filter(clazz -> !Modifier.isAbstract(clazz.getModifiers()))
                    .filter(clazz -> !clazz.isAnnotationPresent(Deprecated.class))
                    .filter(clazz -> !clazz.isInterface())
                    .sorted((a, b) -> a.getSimpleName().compareTo(b.getSimpleName()))
                    .map(clazz -> {
                        @SuppressWarnings("unchecked")
                        Class<? extends T> safeClass = (Class<? extends T>) clazz;
                        return safeClass;
                    })
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public static void registerTile(Class<? extends TileEntity> tile) {
        GameRegistry.registerTileEntity(tile, tile.getSimpleName());
    }

    @SafeVarargs
    public static void registerTiles(Class<? extends TileEntity>... tiles) {
        Arrays.stream(tiles).forEach(RegisterUtils::registerTile);
    }

    public static void registerBlock(Block block) {
        GameRegistry.registerBlock(block, ItemBlock.class, block.getUnlocalizedName().replace("tile.", ""));
    }

    public static void registerBlock(Block block, Class<? extends ItemBlock> itemBlock) {
        GameRegistry.registerBlock(block, itemBlock, block.getUnlocalizedName().replace("tile.", ""));
    }

    public static void registerBlock(Class<? extends ItemBlock> itemBlock, Block block) {
        registerBlock(block, itemBlock);
    }

    public static void registerBlocks(Block... blocks) {
        for (Block block : blocks) {
            RegisterUtils.registerBlock(block);
        }
    }

    public static void registerBlocks(Class<? extends ItemBlock> itemBlock, Block... blocks) {
        for (Block block : blocks) {
            RegisterUtils.registerBlock(block, itemBlock);
        }
    }

    public static void registerItem(Item item) {
        GameRegistry.registerItem(item, item.getUnlocalizedName().replace("item.", ""));
    }

    public static void registerItem(Item item, String name) {
        GameRegistry.registerItem(item, name);
    }

    public static void registerItems(Item... items) {
        for (Item item : items) {
            RegisterUtils.registerItem(item);
        }
    }

    public static void registerItemRenderer(Item item, IItemRenderer render) {
        MinecraftForgeClient.registerItemRenderer(item, render);
    }

    public static void registerItemRendererFromBlock(Block block, IItemRenderer render) {
        MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(block), render);
    }

    public static void registerTileRenderer(Class<? extends TileEntity> tile, TileEntitySpecialRenderer render) {
        ClientRegistry.bindTileEntitySpecialRenderer(tile, render);
    }

    public static void registerEntityRender(Class<? extends Entity> entity, Render render) {
        RenderingRegistry.registerEntityRenderingHandler(entity, render);
    }
}
