package foxiwhitee.FoxLib.client.tooltips.attribute;

import foxiwhitee.FoxLib.config.FoxLibConfig;
import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.*;

public class AttributeFilter {

    private static List<String> compiledTemplates = null;

    private static void ensureCompiled() {
        if (compiledTemplates != null) return;

        compiledTemplates = new ArrayList<>(FoxLibConfig.tooltipsAttributes.length);
        for (String raw : FoxLibConfig.tooltipsAttributes) {
            String processed = raw;
            if (processed.startsWith("L+")) {
                String key = processed.substring(2);
                try {
                    processed = I18n.format(key);
                } catch (Throwable t) {
                    processed = key;
                }
            }
            processed = stripFmt(processed).toLowerCase(Locale.ROOT).trim();
            compiledTemplates.add(processed);
        }
    }

    public static List<String> stripVanillaAttributes(ItemStack stack, List<String> lines) {
        if (lines == null || lines.isEmpty()) return lines;

        ensureCompiled();

        int n = lines.size();

        String[] norm = new String[n];
        for (int i = 0; i < n; i++) {
            String raw = lines.get(i);
            if (raw != null) {
                norm[i] = stripFmt(raw).toLowerCase(Locale.ROOT).trim();
            }
        }

        Set<String> enchantNames = collectEnchantNames(stack);

        boolean anyDrop = false;
        for (int i = 1; i < n; i++) {
            String l = norm[i];
            if (l == null) {
                continue;
            }
            if (enchantNames.contains(l)) {
                continue;
            }

            if (matchesAnyTemplate(l)) {
                anyDrop = true;
                break;
            }
        }

        if (!anyDrop) {
            return lines;
        }

        List<String> out = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            String raw = lines.get(i);
            if (raw == null) {
                continue;
            }
            if (i == 0) {
                out.add(raw);
                continue;
            }

            String l = norm[i];
            if (enchantNames.contains(l)) {
                out.add(raw);
                continue;
            }
            if (matchesAnyTemplate(l)) {
                continue;
            }

            out.add(raw);
        }

        return out;
    }

    private static Set<String> collectEnchantNames(ItemStack stack) {
        if (stack == null) {
            return Collections.emptySet();
        }
        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null) {
            return Collections.emptySet();
        }
        if (!tag.hasKey("ench") && !tag.hasKey("StoredEnchantments")) {
            return Collections.emptySet();
        }

        Map<Integer, Integer> enchants;
        try {
            enchants = EnchantmentHelper.getEnchantments(stack);
        } catch (Throwable t) {
            return Collections.emptySet();
        }
        if (enchants.isEmpty()) {
            return Collections.emptySet();
        }

        Set<String> names = new HashSet<>();
        for (Map.Entry<Integer, Integer> entry : enchants.entrySet()) {
            int id, lvl;
            try {
                id = entry.getKey();
                lvl = entry.getValue();
            } catch (Throwable t) {
                continue;
            }
            if (id < 0 || id >= Enchantment.enchantmentsList.length) {
                continue;
            }
            Enchantment ench = Enchantment.enchantmentsList[id];
            if (ench == null) {
                continue;
            }
            try {
                String full = ench.getTranslatedName(lvl);
                if (full != null) {
                    names.add(full.toLowerCase(Locale.ROOT).trim());
                }
            } catch (Throwable ignored) {}
        }
        return names;
    }

    private static boolean matchesAnyTemplate(String line) {
        if (looksLikePureNumber(line)) {
            return true;
        }

        for (String template : compiledTemplates) {
            if (matchTemplate(line, template)) {
                return true;
            }
        }
        return false;
    }

    private static boolean matchTemplate(String line, String template) {
        if (template.isEmpty()) {
            return line.isEmpty();
        }

        int lIdx = 0;
        int tIdx = 0;
        int lLen = line.length();
        int tLen = template.length();

        while (tIdx < tLen) {
            if (tIdx < tLen - 1 && template.charAt(tIdx) == '%' && template.charAt(tIdx + 1) == 's') {
                tIdx += 2;
                if (tIdx == tLen) {
                    return isNumericPart(line.substring(lIdx));
                }

                char nextTargetChar = template.charAt(tIdx);
                int nextLIdx = line.indexOf(nextTargetChar, lIdx);
                if (nextLIdx == -1) {
                    return false;
                }

                String digits = line.substring(lIdx, nextLIdx);
                if (!isNumericPart(digits)) {
                    return false;
                }

                lIdx = nextLIdx;
            } else {
                if (lIdx >= lLen || line.charAt(lIdx) != template.charAt(tIdx)) {
                    return false;
                }
                lIdx++;
                tIdx++;
            }
        }

        return lIdx == lLen;
    }

    private static boolean isNumericPart(String s) {
        s = s.trim();
        if (s.isEmpty()) {
            return false;
        }
        boolean hasDigit = false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c >= '0' && c <= '9') {
                hasDigit = true;
            } else if (!(c == '.' || c == ',' || c == '+' || c == '-' || c == '%')) {
                return false;
            }
        }
        return hasDigit;
    }

    private static boolean looksLikePureNumber(String l) {
        if (l.isEmpty()) {
            return false;
        }
        boolean hasDigit = false;
        for (int i = 0; i < l.length(); i++) {
            char c = l.charAt(i);
            if (c >= '0' && c <= '9') {
                hasDigit = true;
            } else if (c != '.' && c != ',') {
                return false;
            }
        }
        return hasDigit;
    }

    public static String stripFmt(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder(text.length());
        boolean skip = false;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (skip) {
                skip = false;
                continue;
            }
            if (c == '§') {
                skip = true;
                continue;
            }
            sb.append(c);
        }
        return sb.toString();
    }
}
