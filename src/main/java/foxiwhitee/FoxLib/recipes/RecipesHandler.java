package foxiwhitee.FoxLib.recipes;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import cpw.mods.fml.common.discovery.ASMDataTable;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import foxiwhitee.FoxLib.api.FoxLibApi;
import foxiwhitee.FoxLib.integration.crafttweaker.DynamicIntegration;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class RecipesHandler {
    private static final Map<String, List<String>> RECIPES_NAMES = new HashMap<>();
    private static final Map<String, Class<?>> RECIPES_ClASS = new HashMap<>();

    public static void loadRecipesLocations(FMLPreInitializationEvent event) {
        ASMDataTable asm = event.getAsmData();
        Set<ASMDataTable.ASMData> data = asm.getAll(RecipesLocation.class.getName());

        for (ASMDataTable.ASMData entry : data) {
            try {
                Class<?> clazz = Class.forName(entry.getClassName());
                Field field = clazz.getDeclaredField(entry.getObjectName());
                RecipesLocation ann = field.getAnnotation(RecipesLocation.class);
                field.setAccessible(true);
                String[] array = (String[]) field.get(null);
                if (RECIPES_NAMES.containsKey(ann.modId())) {
                    List<String> temp = RECIPES_NAMES.get(ann.modId());
                    temp.addAll(Arrays.asList(array));
                    RECIPES_NAMES.put(ann.modId(), temp);
                } else {
                    RECIPES_NAMES.put(ann.modId(), Arrays.asList(array));
                }
                RECIPES_ClASS.put(ann.modId(), clazz);
            } catch (Exception e) {
                System.err.println("Failed: " + e);
            }
        }
    }

    public static void init() {
        List<JsonObject> recipes = new ArrayList<>();
        RECIPES_NAMES.forEach((s, strings) -> {
            strings.forEach(s1 -> {
                try {
                    recipes.addAll(loadJsonResources(RECIPES_ClASS.get(s), "assets/" + s + "/" + s1));
                } catch (IOException | URISyntaxException e) {
                    e.printStackTrace();
                }

            });
        });
        recipes.forEach(jsonObject -> {
            if (jsonObject.has("crafts")) {
                JsonArray array = jsonObject.getAsJsonArray("crafts");
                for (int i = 0; i < array.size(); i++) {
                    JsonObject object = array.get(i).getAsJsonObject();
                    createCraft(object);
                }
            } else {
                createCraft(jsonObject);
            }
        });
    }

    private static void createCraft(JsonObject jsonObject) {
        if (jsonObject.has("type")) {
            String type = jsonObject.get("type").getAsString();
            for (int i = 0; i < FoxLibApi.instance.registries().registerJsonRecipe().size(); i++) {
                Map.Entry<String, Class<? extends IJsonRecipe>> entry = FoxLibApi.instance.registries().registerJsonRecipe().getEntry(i);
                if (type.equals(entry.getKey())) {
                    Constructor<? extends IJsonRecipe> constructor = null;
                    try {
                        constructor = entry.getValue().getDeclaredConstructor();
                    } catch (NoSuchMethodException e) {
                        throw new RuntimeException(e);
                    }
                    constructor.setAccessible(true);
                    try {
                        IJsonRecipe recipe = constructor.newInstance();
                        recipe.create(jsonObject);
                        recipe.register();
                        if (recipe.hasMineTweakerIntegration()) {
                            DynamicIntegration.DYNAMIC_TYPES.add(recipe);
                        }
                        return;
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    public static List<JsonObject> loadJsonResources(Class<?> clazz, String path)
        throws IOException, URISyntaxException {

        List<JsonObject> result = new ArrayList<>();
        Gson gson = new Gson();

        if (path.startsWith("/")) path = path.substring(1);
        if (!path.endsWith("/")) path += "/";

        ClassLoader classLoader = clazz.getClassLoader();
        if (classLoader == null) {
            throw new IllegalStateException("ClassLoader not found for class " + clazz.getName());
        }

        URL dirURL = classLoader.getResource(path);
        if (dirURL == null) {
            throw new FileNotFoundException("Resource path not found: " + path);
        }

        String protocol = dirURL.getProtocol();

        if (protocol.equals("file")) {
            File folder = new File(dirURL.toURI());
            File[] files = folder.listFiles((dir, name) -> name.endsWith(".json"));
            if (files != null) {
                for (File file : files) {
                    try (Reader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
                        JsonObject obj = gson.fromJson(reader, JsonObject.class);
                        if (obj != null) result.add(obj);
                    }
                }
            }
            return result;
        }

        if (protocol.equals("jar")) {
            JarURLConnection connection = (JarURLConnection) dirURL.openConnection();

            try (JarFile jar = connection.getJarFile()) {
                Enumeration<JarEntry> entries = jar.entries();
                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    String name = entry.getName();

                    if (name.startsWith(path) && name.endsWith(".json") && !entry.isDirectory()) {
                        try (InputStream is = jar.getInputStream(entry);
                             Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
                            JsonObject obj = gson.fromJson(reader, JsonObject.class);
                            if (obj != null) result.add(obj);
                        }
                    }
                }
            }
            return result;
        }

        throw new UnsupportedOperationException("Unsupported protocol: " + protocol);
    }
}
