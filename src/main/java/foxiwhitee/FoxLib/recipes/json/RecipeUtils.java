package foxiwhitee.FoxLib.recipes.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import foxiwhitee.FoxLib.utils.helpers.StackOreDict;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RecipeUtils {

    public static ItemStack getItemStack(JsonElement element, boolean nullable) {
        if (element.isJsonPrimitive()) {
            return getItemStack(element.getAsString(), nullable);
        } else if (element.isJsonObject()) {
            JsonObject object = element.getAsJsonObject();
            ItemStack stack = getItemStack(object.get("value").getAsString(), nullable);
            if (stack == null) {
                return null;
            }
            stack.setTagCompound(toNBT(object.get("tag").getAsJsonObject()));
            return stack;
        }
        return null;
    }

    public static ItemStack getItemStack(String name, boolean nullable) throws RuntimeException {
        if (name.equals("null")) {
            return null;
        }
        Parsed info = parse(name);
        Item item = (Item) Item.itemRegistry.getObject(info.modId + ":" + info.name);
        if (item != null) {
            return new ItemStack(item, info.count, info.meta);
        }
        if (nullable) {
            return null;
        }
        throw new RuntimeException("Item not found: " + name);
    }

    private static Parsed parse(String input) {
        Pattern pattern = Pattern.compile("^<([\\w-]+):([\\w.-]*?)(?:\\.(\\d+))?(?::(\\d+))?>$");
        Matcher matcher = pattern.matcher(input);

        if (matcher.matches()) {
            String first = matcher.group(1);
            String second = matcher.group(2);
            int dotNumber = matcher.group(3) != null ? Integer.parseInt(matcher.group(3)) : 0;
            int colonNumber = matcher.group(4) != null ? Integer.parseInt(matcher.group(4)) : 1;
            return new Parsed(first, second, dotNumber, colonNumber);
        }

        throw new RuntimeException("ItemStack should have the form <modId:name.meta:count> where meta and count are optional");
    }

    public static FluidStack getFluidStack(JsonElement element, boolean nullable) {
        if (element.isJsonPrimitive()) {
            return getFluidStack(element.getAsString(), nullable);
        } else if (element.isJsonObject()) {
            JsonObject object = element.getAsJsonObject();
            FluidStack stack = getFluidStack(object.get("value").getAsString(), nullable);
            if (stack == null) {
                return null;
            }
            stack.tag = toNBT(object.get("tag").getAsJsonObject());
            return stack;
        }
        return null;
    }

    public static FluidStack getFluidStack(String name, boolean nullable) throws RuntimeException {
        if (name.equals("null")) {
            return null;
        }
        ParsedFluid info = parseFluid(name);
        FluidStack fluidStack = createFluidStack(info.name, info.count);
        if (fluidStack != null) {
            return fluidStack;
        }
        if (nullable) {
            return null;
        }
        throw new RuntimeException("Fluid not found: " + name);
    }

    private static ParsedFluid parseFluid(String input) {
        Pattern pattern = Pattern.compile("^<([\\w.-]+)(?::(\\d+))?>$");
        Matcher matcher = pattern.matcher(input);

        if (matcher.matches()) {
            String fluidName = matcher.group(1);
            int amount = matcher.group(2) != null ? Integer.parseInt(matcher.group(2)) : 1000;

            return new ParsedFluid(fluidName, amount);
        }

        throw new RuntimeException("FluidStack should have the form <name:amount> where amount is optional");
    }

    public static Object getOreOrStack(JsonElement element, boolean oreDict, boolean nullable) {
        String name = element.getAsString();
        if (oreDict && name.startsWith("<ore:")) {
            ParsedOre parsed = parseOredict(name);
            return new StackOreDict(parsed.name, parsed.count);
        } else {
            return getItemStack(name, nullable);
        }
    }

    private static ParsedOre parseOredict(String input) {
        Pattern pattern = Pattern.compile("^<([\\w-]+):([\\w.-]*?)(?::(\\d+))?>$");
        Matcher matcher = pattern.matcher(input);
        if (matcher.matches()) {
            String second = matcher.group(2);
            int colonNumber = matcher.group(3) != null ? Integer.parseInt(matcher.group(3)) : 1;
            return new ParsedOre(second, colonNumber);
        } else {
            throw new RuntimeException("Oredict should have the form <ore:name:count> where count are optional");
        }
    }

    public static Object[] getItems(JsonArray inp, boolean oreDict, boolean nullable) throws RuntimeException {
        Object[] items;
        items = new Object[inp.size()];
        for (int i = 0; i < inp.size(); i++) {
            if (inp.get(i).isJsonObject()) {
                items[i] = getItemStack(inp.get(i), nullable);
                continue;
            }
            items[i] = getOreOrStack(inp.get(i), oreDict, nullable);
        }
        return items;
    }

    public static FluidStack[] getFluids(JsonArray inp, boolean nullable) throws RuntimeException {
        FluidStack[] inputs;
        inputs = new FluidStack[inp.size()];
        for (int i = 0; i < inp.size(); i++) {
            inputs[i] = getFluidStack(inp.get(i), nullable);
        }
        return inputs;
    }

    private static NBTTagCompound toNBT(JsonObject json) {
        return toNBTInternal(json, "");
    }

    private static NBTTagCompound toNBTInternal(JsonObject json, String path) {
        NBTTagCompound compound = new NBTTagCompound();

        for (Map.Entry<String, JsonElement> entrySet : json.entrySet()) {
            String key = entrySet.getKey();
            JsonElement element = entrySet.getValue();
            String currentPath = path.isEmpty() ? key : path + "." + key;

            if (element.isJsonObject()) {
                compound.setTag(key, toNBTInternal(element.getAsJsonObject(), currentPath));

            } else if (element.isJsonArray()) {
                JsonArray array = element.getAsJsonArray();
                NBTTagList list = new NBTTagList();

                for (int i = 0; i < array.size(); i++) {
                    JsonElement entry = array.get(i);

                    if (!entry.isJsonObject()) {
                        throw new IllegalArgumentException("Array at " + currentPath + " contains non-object element at index " + i);
                    }

                    list.appendTag(toNBTInternal(entry.getAsJsonObject(), currentPath + "[" + i + "]"));
                }

                compound.setTag(key, list);

            } else if (element.isJsonPrimitive()) {
                JsonPrimitive primitive = element.getAsJsonPrimitive();

                if (primitive.isBoolean()) {
                    compound.setBoolean(key, primitive.getAsBoolean());
                } else if (primitive.isNumber()) {
                    Number num = primitive.getAsNumber();

                    if (num.toString().contains(".")) {
                        compound.setDouble(key, num.doubleValue());
                    } else {
                        long l = num.longValue();
                        if (l >= Integer.MIN_VALUE && l <= Integer.MAX_VALUE) {
                            compound.setInteger(key, (int) l);
                        } else {
                            compound.setLong(key, l);
                        }
                    }
                } else if (primitive.isString()) {
                    compound.setString(key, primitive.getAsString());
                }
            } else {
                throw new IllegalArgumentException("Unsupported JSON element at " + currentPath);
            }
        }

        return compound;
    }

    public static FluidStack createFluidStack(String fluidName, int amount) {
        Fluid fluid = FluidRegistry.getFluid(fluidName);

        if (fluid != null) {
            return new FluidStack(fluid, amount);
        } else {
            return null;
        }
    }

    private static class ParsedOre {
        private final String name;
        private final int count;

        private ParsedOre(String name, int count) {
            this.name = name;
            this.count = count;
        }
    }

    private static class ParsedFluid {
        private final String name;
        private final int count;

        private ParsedFluid(String name, int count) {
            this.name = name;
            this.count = count;
        }
    }

    private static class Parsed {
        private final String modId, name;
        private final int meta, count;

        private Parsed(String modId, String name, int meta, int count) {
            this.modId = modId;
            this.name = name;
            this.meta = meta;
            this.count = count;
        }
    }

}
