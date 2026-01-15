package foxiwhitee.FoxLib.utils.helpers;

import cpw.mods.fml.common.registry.GameData;
import foxiwhitee.FoxLib.config.FoxLibConfig;
import foxiwhitee.FoxLib.recipes.json.RecipeUtils;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.*;

public class ProductivityBlackListHelper {
    private static final class ItemInBL {
        private final Item item;
        private final int meta;

        ItemInBL(Item item, int meta) {
            this.item = item;
            this.meta = meta;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ItemInBL other)) return false;
            return Item.getIdFromItem(item) == Item.getIdFromItem(other.item) && meta == other.meta;
        }

        @Override
        public int hashCode() {
            return (Item.getIdFromItem(item) * 31) + meta;
        }
    }

    private static final List<ItemInBL> BLACKLIST = new ArrayList<>();
    private static final List<String> BLACKLIST_MODS = new ArrayList<>();

    public static void registerBlackList(String[] strings) {
        for (String s : strings) {
            ItemStack stack = null;
            try {
                stack = RecipeUtils.getItemStack(s, false);
            } catch (RuntimeException ignored) {}
            if (stack != null) {
                BLACKLIST.add(new ItemInBL(stack.getItem(), stack.getItemDamage()));
            }
        }
    }

    public static void registerBlackListByModId(String[] strings) {
        BLACKLIST_MODS.addAll(Arrays.asList(strings));
    }

    public static boolean isInBlackList(ItemStack itemStack) {
        if (itemStack == null) {
            return false;
        }
        Item item = itemStack.getItem();
        String modid = GameData.getItemRegistry().getNameForObject(item).split(":")[0];
        int meta = itemStack.getItemDamage();
        return isRestricted(new ItemInBL(item, meta), modid, FoxLibConfig.productivityModsBlackListInversion, FoxLibConfig.productivityBlackListInversion);
    }

    public static String getRestrictionStatus(ItemStack itemStack) {
        if (itemStack == null) {
            return "";
        }
        Item i = itemStack.getItem();
        String modId = GameData.getItemRegistry().getNameForObject(i).split(":")[0];
        int meta = itemStack.getItemDamage();
        ItemInBL item = new ItemInBL(i, meta);
        boolean isModsWhitelist = FoxLibConfig.productivityModsBlackListInversion;
        boolean isItemsWhitelist = FoxLibConfig.productivityBlackListInversion;
        boolean modInList = BLACKLIST_MODS.contains(modId);
        boolean itemInList = BLACKLIST.contains(item);
        if (!isModsWhitelist) {
            if (modInList) {
                if (isItemsWhitelist) {
                    return itemInList ? "white" : "black";
                } else {
                    return "black";
                }
            }
        } else {
            if (!modInList) {
                return "black";
            }
        }
        if (isItemsWhitelist) {
            return itemInList ? "white" : "";
        } else {
            return itemInList ? "black" : "";
        }
    }

    private static boolean isRestricted(ItemInBL item, String modId, boolean isModsWhitelist, boolean isItemsWhitelist) {
        boolean modInList = BLACKLIST_MODS.contains(modId);
        if (!isModsWhitelist) {
            if (modInList) {
                if (isItemsWhitelist) {
                    return !BLACKLIST.contains(item);
                } else {
                    return true;
                }
            } else {
                if (!isItemsWhitelist) {
                    return BLACKLIST.contains(item);
                }
                return false;
            }
        } else {
            if (!modInList) {
                return true;
            } else {
                if (!isItemsWhitelist) {
                    return BLACKLIST.contains(item);
                }
                return false;
            }
        }
    }

    public static boolean isInBlackList(Item item, int meta) {
        return BLACKLIST.contains(new ItemInBL(item, meta));
    }
}
