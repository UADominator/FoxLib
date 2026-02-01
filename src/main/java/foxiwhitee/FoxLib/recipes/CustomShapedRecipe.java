package foxiwhitee.FoxLib.recipes;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;

import java.util.List;

public class CustomShapedRecipe extends ShapedOreRecipe {
    private final boolean strictNBT;
    protected int width;
    protected int height;
    protected Object[] input;

    public CustomShapedRecipe(ItemStack result, boolean strictNBT, Object[] fullGrid) {
        super(result, "ABA", "BAB", "ABA", 'A', result, 'B', result);
        this.strictNBT = strictNBT;
        this.calculateBounds(fullGrid);
        this.resolveOreDict();
    }

    public CustomShapedRecipe(ItemStack result, boolean strictNBT, int width, int height, Object[] input) {
        super(result, "A", 'A', result);
        this.width = width;
        this.height = height;
        this.input = input;
        this.strictNBT = strictNBT;
        this.resolveOreDict();
    }

    private void resolveOreDict() {
        for (int i = 0; i < input.length; i++) {
            if (input[i] instanceof String) {
                input[i] = OreDictionary.getOres((String) input[i]);
            }
        }
    }

    private void calculateBounds(Object[] fullGrid) {
        int minX = 3, minY = 3, maxX = 0, maxY = 0;
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                Object stack = fullGrid[y * 3 + x];
                if (stack != null) {
                    if (x < minX) minX = x;
                    if (x > maxX) maxX = x;
                    if (y < minY) minY = y;
                    if (y > maxY) maxY = y;
                }
            }
        }
        this.width = maxX - minX + 1;
        this.height = maxY - minY + 1;
        this.input = new Object[width * height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                this.input[y * width + x] = fullGrid[(y + minY) * 3 + (x + minX)];
            }
        }
    }

    @Override
    public boolean matches(InventoryCrafting inv, World world) {
        for (int x = 0; x <= 3 - width; x++) {
            for (int y = 0; y <= 3 - height; y++) {
                if (checkMatch(inv, x, y, false)) return true;
                if (checkMatch(inv, x, y, true)) return true;
            }
        }
        return false;
    }

    private boolean checkMatch(InventoryCrafting inv, int startX, int startY, boolean mirror) {
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                int subX = x - startX;
                int subY = y - startY;
                Object target = null;

                if (subX >= 0 && subY >= 0 && subX < width && subY < height) {
                    if (mirror) target = input[width - subX - 1 + subY * width];
                    else target = input[subX + subY * width];
                }

                ItemStack slotStack = inv.getStackInRowAndColumn(x, y);

                if (target == null) {
                    if (slotStack != null) return false;
                    continue;
                }

                if (target instanceof ItemStack targetStack) {
                    if (slotStack == null || slotStack.getItem() != targetStack.getItem()) return false;
                    if (targetStack.getItemDamage() != OreDictionary.WILDCARD_VALUE && targetStack.getItemDamage() != slotStack.getItemDamage()) return false;

                    if (strictNBT && !ItemStack.areItemStackTagsEqual(slotStack, targetStack)) return false;

                } else if (target instanceof List) {
                    List<ItemStack> oreList = (List<ItemStack>) target;
                    boolean found = false;
                    for (ItemStack ore : oreList) {
                        if (OreDictionary.itemMatches(ore, slotStack, false)) {
                            if (!strictNBT || ItemStack.areItemStackTagsEqual(slotStack, ore)) {
                                found = true;
                                break;
                            }
                        }
                    }
                    if (!found) return false;
                }
            }
        }
        return true;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return super.getRecipeOutput();
    }

    @Override
    public int getRecipeSize() {
        return width * height;
    }

    @Override
    public Object[] getInput() {
        return input;
    }
}
