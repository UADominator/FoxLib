package foxiwhitee.FoxLib.client.tooltips;

import foxiwhitee.FoxLib.config.FoxLibConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class TooltipEngine {
    private static final long DUPLICATE_GUARD_MS = 8L;
    private static final long STACK_VALIDITY_MS = 250L;
    private static final long SLOW_RENDER_THRESHOLD_NS = 30_000_000L;
    private static final int  CONFIRM_HITS = 2;

    private static long lastSuccessfulRenderMs = -1L;
    private static int  lastRenderFingerprint = 0;
    private static ItemStack lastHoveredStack = null;
    private static long lastHoveredStackTimeMs = -1L;

    private static final String[] PROBLEM_SCREENS = {
        "acclimatiser",
        "acclimatizer"
    };
    private static final Set<Integer> SLOW_ITEM_IDS = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private static final Map<Integer, Integer> SLOW_HITS = new ConcurrentHashMap<>();

    private static Class<?> cachedScreenClass;
    private static boolean  cachedScreenIsProblem;

    private static boolean shouldRenderVanillaTooltip = true;

    public static void setShouldRenderVanillaTooltip(boolean shouldRenderVanillaTooltip) {
        TooltipEngine.shouldRenderVanillaTooltip = shouldRenderVanillaTooltip;
    }

    public static boolean isShouldRenderVanillaTooltip() {
        return shouldRenderVanillaTooltip;
    }

    private static boolean isProblemScreen() {
        try {
            GuiScreen s = Minecraft.getMinecraft().currentScreen;
            if (s == null) return false;
            Class<?> c = s.getClass();
            if (c == cachedScreenClass) {
                return cachedScreenIsProblem;
            }
            String name = c.getName().toLowerCase(java.util.Locale.ROOT);
            boolean is = false;
            for (String problemScreen : PROBLEM_SCREENS) {
                if (name.contains(problemScreen)) {
                    is = true;
                    break;
                }
            }
            cachedScreenClass = c;
            cachedScreenIsProblem = is;
            return is;
        } catch (Throwable ignored) {
            return false;
        }
    }

    public static boolean wasRenderedRecently(long now) {
        return lastSuccessfulRenderMs > 0L && (now - lastSuccessfulRenderMs) < DUPLICATE_GUARD_MS;
    }

    public static void setHoveredStack(Object stack) {
        if (stack instanceof ItemStack) {
            lastHoveredStack = ((ItemStack) stack).copy();
            lastHoveredStackTimeMs = System.currentTimeMillis();
        } else {
            lastHoveredStack = null;
            lastHoveredStackTimeMs = -1L;
        }
    }

    public static int createHash(List<String> lines, ItemStack stack) {
        int hash = 1;
        if (stack != null) {
            hash = 31 * hash + System.identityHashCode(stack.getItem());
            hash = 31 * hash + stack.getItemDamage();
            String displayName;
            try {
                displayName = stack.getDisplayName();
            } catch (Throwable t) {
                displayName = null;
            }
            if (displayName != null) {
                hash = 31 * hash + displayName.hashCode();
            }
        } else if (lines != null && !lines.isEmpty()) {
            hash = 31 * hash + lines.get(0).hashCode();
        }
        hash = 31 * hash + (lines == null ? 0 : lines.size());
        return hash;
    }

    public static boolean wasRenderedFor(long now, int signature, int mouseX, int mouseY) {
        if (lastSuccessfulRenderMs <= 0L) {
            return false;
        }
        if ((now - lastSuccessfulRenderMs) >= DUPLICATE_GUARD_MS) {
            return false;
        }
        int fp = signature ^ (mouseX << 16) ^ mouseY;
        return fp == lastRenderFingerprint;
    }

    @SuppressWarnings("unused")
    public static boolean renderVanillaTooltipSimply(Object screen, List<?> lines, int mouseX, int mouseY) {
        return renderVanillaTooltip(screen, lines, mouseX, mouseY, Minecraft.getMinecraft().fontRenderer);
    }

    @SuppressWarnings("unused")
    public static boolean renderVanillaTooltip(Object guiScreen, List<?> lines, int mouseX, int mouseY, Object fontRenderer) {
        try {
            if (lines == null || lines.isEmpty()) {
                shouldRenderVanillaTooltip = true;
                return false;
            }
            if (!FoxLibConfig.enableCustomTooltips) {
                shouldRenderVanillaTooltip = true;
                return false;
            }
            if (isProblemScreen()) {
                shouldRenderVanillaTooltip = true;
                return false;
            }

            FontRenderer renderer = fontRenderer instanceof FontRenderer ? (FontRenderer) fontRenderer
                : Minecraft.getMinecraft().fontRenderer;
            if (renderer == null) {
                shouldRenderVanillaTooltip = true;
                return false;
            }

            List<String> normalized = normalize(lines);
            if (normalized.isEmpty()) {
                shouldRenderVanillaTooltip = true;
                return false;
            }

            long now = System.currentTimeMillis();
            ItemStack stack = getCurrentHoveredStack(now);
            int hash = createHash(normalized, stack);

            int itemId = (stack == null || stack.getItem() == null) ? 0 : System.identityHashCode(stack.getItem());
            if (itemId != 0 && SLOW_ITEM_IDS.contains(itemId)) {
                shouldRenderVanillaTooltip = true;
                return false;
            }

            long renderStart = System.nanoTime();
            boolean result = TooltipRenderer.render(normalized, stack, mouseX, mouseY, renderer, now, hash);
            long elapsed = System.nanoTime() - renderStart;
            if (itemId != 0) {
                if (elapsed > SLOW_RENDER_THRESHOLD_NS) {
                    Integer key = itemId;
                    Integer prev = SLOW_HITS.get(key);
                    int hits = (prev == null ? 0 : prev) + 1;
                    if (hits >= CONFIRM_HITS) {
                        SLOW_ITEM_IDS.add(key);
                        SLOW_HITS.remove(key);
                    } else {
                        SLOW_HITS.put(key, hits);
                    }
                } else {
                    SLOW_HITS.remove(itemId);
                }
            }
            lastSuccessfulRenderMs = now;
            lastRenderFingerprint = hash ^ (mouseX << 16) ^ mouseY;
            shouldRenderVanillaTooltip = !result;
            return result;
        } catch (Throwable ignored) {
            shouldRenderVanillaTooltip = true;
            return false;
        }
    }

    private static List<String> normalize(List<?> lines) {
        List<String> result = new ArrayList<>(lines.size());
        for (Object line : lines) {
            if (line != null) {
                result.add(String.valueOf(line));
            }
        }
        return result;
    }

    private static ItemStack getCurrentHoveredStack(long now) {
        if (lastHoveredStack != null && (now - lastHoveredStackTimeMs) <= STACK_VALIDITY_MS) {
            return lastHoveredStack;
        }
        return null;
    }
}
