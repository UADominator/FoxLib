package foxiwhitee.FoxLib.recipes.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import foxiwhitee.FoxLib.recipes.json.annotations.*;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RecipeProcessor {
    public static <T extends IJsonRecipe> T process(Class<T> recipeClass, JsonObject json) throws Exception {
        T recipe = recipeClass.getDeclaredConstructor().newInstance();
        JsonRecipe typeAnno = recipeClass.getAnnotation(JsonRecipe.class);

        if (typeAnno == null) {
            throw new RuntimeException("Class " + recipeClass.getName() + " is missing @JsonRecipe annotation");
        }

        int outputCount = 0;
        for (Field field : recipeClass.getDeclaredFields()) {
            field.setAccessible(true);
            boolean isList = List.class.isAssignableFrom(field.getType());

            if (field.isAnnotationPresent(RecipeOutput.class)) {
                outputCount++;
            }

            if (field.isAnnotationPresent(NumberValue.class)) {
                NumberValue anno = field.getAnnotation(NumberValue.class);
                JsonElement el = json.get(anno.value());
                if (validate(el, false, anno.value())) {
                    try {
                        field.set(recipe, isList ? parseNumberList(el, field) : castNumber(el.getAsDouble(), field.getType()));
                    } catch (Exception e) {
                        if (!isList) {
                            field.set(recipe, 0);
                        } else {
                            field.set(recipe, null);
                        }
                    }
                }
            } else if (field.isAnnotationPresent(BooleanValue.class)) {
                BooleanValue anno = field.getAnnotation(BooleanValue.class);
                JsonElement el = json.get(anno.value());
                try {
                    if (validate(el, false, anno.value())) {
                        field.set(recipe, isList ? parseBooleanList(el) : el.getAsBoolean());
                    }
                } catch (Exception e) {
                    if (!isList) {
                        field.set(recipe, false);
                    } else {
                        field.set(recipe, null);
                    }
                }
            } else if (field.isAnnotationPresent(StringValue.class)) {
                StringValue anno = field.getAnnotation(StringValue.class);
                JsonElement el = json.get(anno.value());
                try {
                    if (validate(el, false, anno.value())) {
                        field.set(recipe, isList ? parseStringList(el) : el.getAsString());
                    }
                } catch (Exception e) {
                    if (!isList) {
                        field.set(recipe, false);
                    } else {
                        field.set(recipe, null);
                    }
                }
            } else if (field.isAnnotationPresent(OreValue.class)) {
                if (!typeAnno.hasOreDict()) throw new RuntimeException("OreValue used but hasOreDict is false");
                OreValue anno = field.getAnnotation(OreValue.class);
                JsonElement el = json.get(anno.value());
                if (validate(el, anno.canBeNull(), anno.value())) {
                    field.set(recipe, parseOreInput(el, isList, anno.canBeNull()));
                }
            } else if (field.isAnnotationPresent(FluidValue.class)) {
                FluidValue anno = field.getAnnotation(FluidValue.class);
                JsonElement el = json.get(anno.value());
                if (validate(el, anno.canBeNull(), anno.value())) {
                    field.set(recipe, parseFluid(el, isList, anno.canBeNull()));
                }
            } else if (field.isAnnotationPresent(RecipeValue.class)) {
                RecipeValue anno = field.getAnnotation(RecipeValue.class);
                JsonElement el = json.get(anno.value());
                if (validate(el, anno.canBeNull(), anno.value())) {
                    field.set(recipe, parseSimpleValue(el, isList, anno.canBeNull()));
                }
            }
        }

        if (outputCount != 1) {
            throw new RuntimeException("Recipe " + recipeClass.getSimpleName() + " must have exactly one @RecipeOutput field. Found: " + outputCount);
        }

        return recipe;
    }

    private static boolean validate(JsonElement e, boolean canBeNull, String name) {
        if (e == null || e.isJsonNull()) {
            if (canBeNull) return false;
            throw new RuntimeException("Required field is missing: " + name);
        }
        return true;
    }

    private static Object castNumber(double val, Class<?> targetType) {
        if (targetType == int.class || targetType == Integer.class) return (int) val;
        if (targetType == float.class || targetType == Float.class) return (float) val;
        if (targetType == long.class || targetType == Long.class) return (long) val;
        return val;
    }

    private static List<Object> parseNumberList(JsonElement el, Field field) {
        if (!el.isJsonArray()) throw new RuntimeException("Expected JSON Array for field " + field.getName());
        JsonArray array = el.getAsJsonArray();
        List<Object> list = new ArrayList<>();
        ParameterizedType type = (ParameterizedType) field.getGenericType();
        Class<?> elementClass = (Class<?>) type.getActualTypeArguments()[0];
        for (JsonElement e : array) list.add(castNumber(e.getAsDouble(), elementClass));
        return list;
    }

    private static List<Boolean> parseBooleanList(JsonElement el) {
        if (!el.isJsonArray()) throw new RuntimeException("Expected JSON Array for boolean list");
        List<Boolean> list = new ArrayList<>();
        el.getAsJsonArray().forEach(e -> list.add(e.getAsBoolean()));
        return list;
    }

    private static List<String> parseStringList(JsonElement el) {
        if (!el.isJsonArray()) throw new RuntimeException("Expected JSON Array for string list");
        List<String> list = new ArrayList<>();
        el.getAsJsonArray().forEach(e -> list.add(e.getAsString()));
        return list;
    }

    private static Object parseOreInput(JsonElement el, boolean isList, boolean canBeNull) {
        try {
            if (isList) {
                return Arrays.asList(RecipeUtils.getItems(el.getAsJsonArray(), true, canBeNull));
            } else {
                return RecipeUtils.getOreOrStack(el, true, canBeNull);
            }
        } catch (Exception e) {
            return null;
        }
    }

    private static Object parseFluid(JsonElement el, boolean isList, boolean canBeNull) {
        try {
            if (isList) {
                return Arrays.asList(RecipeUtils.getFluids(el.getAsJsonArray(), canBeNull));
            } else {
                return RecipeUtils.getFluidStack(el, canBeNull);
            }
        } catch (Exception e) {
            return null;
        }
    }

    private static Object parseSimpleValue(JsonElement el, boolean isList, boolean canBeNull) {
        try {
            if (isList) {
                return Arrays.asList(RecipeUtils.getItems(el.getAsJsonArray(), false, canBeNull));
            } else {
                return RecipeUtils.getItemStack(el, canBeNull);
            }
        } catch (Exception e) {
            return null;
        }
    }
}
