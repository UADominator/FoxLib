package foxiwhitee.FoxLib.client.tooltips.theme.elements.attribute;

import com.google.common.collect.Multimap;
import foxiwhitee.FoxLib.FoxLib;
import foxiwhitee.FoxLib.client.tooltips.theme.ThemeRegister;
import foxiwhitee.FoxLib.config.FoxLibConfig;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.*;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.ResourceLocation;

import java.lang.reflect.Field;
import java.util.*;

public class AttributeInfo {
    public enum Kind {
        ATTACK_DAMAGE("attack_damage", 0xFFFF6464),
        ARMOR("armor", 0xFFC8D0E8),
        DURABILITY("durability"),
        FUEL("fuel", 0xFFFF9040),
        HUNGER("hunger", 0xFFD09060),
        SATURATION("saturation", 0xFFFFD060),
        MOVEMENT_SPEED("movement_speed", 0xFFA0F0FF),
        MAX_HEALTH("max_health", 0xFFFF8080),
        KNOCKBACK_RESISTANCE("knockback_resistance", 0xFFFFC080),
        TOOL_SPEED("tool_speed", 0xFFC0FFC0),
        RANGED_DAMAGE("ranged_damage", 0xFFFFB060),
        PERCENT_DAMAGE("percent_damage", 0xFFC080FF);

        public final ResourceLocation texture;
        public final int color;

        Kind(String textureName, int color) {
            this.color = color;
            this.texture = new ResourceLocation(FoxLib.MODID, "textures/attributes/" + textureName + ".png");
        }

        Kind(String textureName) {
            this(textureName,0);
        }
    }

    public static final class Attr {
        public final Kind kind;
        public final String value;
        public final int color;

        public Attr(Kind kind, String value, int color) {
            this.kind = kind;
            this.value = value;
            this.color = color;
        }
        public Attr(Kind kind, String value) {
            this(kind, value, kind.color);
        }
    }

    public static final class DupGuard {
        private static final Map<Kind, String[]> KEYWORDS = new EnumMap<>(AttributeInfo.Kind.class);

        private static void init() {
            if (KEYWORDS.isEmpty()) {
                KEYWORDS.put(AttributeInfo.Kind.ATTACK_DAMAGE, FoxLibConfig.damageAttributes);
                KEYWORDS.put(AttributeInfo.Kind.ARMOR, FoxLibConfig.armorAttributes);
                KEYWORDS.put(AttributeInfo.Kind.DURABILITY, FoxLibConfig.durabilityAttributes);
                KEYWORDS.put(AttributeInfo.Kind.FUEL, FoxLibConfig.fuelAttributes);
            }
        }

        private final java.util.Set<AttributeInfo.Kind> suppressed = java.util.EnumSet.noneOf(AttributeInfo.Kind.class);

        public static DupGuard from(List<String> lines) {
            init();
            DupGuard guard = new DupGuard();
            if (lines == null) return guard;

            for (String line : lines) {
                if (line == null) continue;
                String clean = stripFmtLower(line);

                for (Map.Entry<AttributeInfo.Kind, String[]> entry : KEYWORDS.entrySet()) {
                    if (containsAny(clean, entry.getValue())) {
                        guard.suppressed.add(entry.getKey());
                    }
                }
            }
            return guard;
        }

        public boolean suppresses(AttributeInfo.Kind kind) {
            return suppressed.contains(kind);
        }

        public boolean isEmpty() { return suppressed.isEmpty(); }

        private static boolean containsAny(String src, String[] needles) {
            for (String needle : needles) {
                if (src.contains(needle)) return true;
            }
            return false;
        }

        private static String stripFmtLower(String text) {
            return text.replaceAll("§.", "").toLowerCase(java.util.Locale.ROOT).trim();
        }
    }

    private static final int RAW_CACHE_LIMIT = 512;
    private static final LinkedHashMap<Long, List<Attr>> RAW_CACHE = new LinkedHashMap<>(128, 0.75F, true) {
            @Override
            protected boolean removeEldestEntry(java.util.Map.Entry<Long, List<Attr>> eldest) {
                return size() > RAW_CACHE_LIMIT;
            }
        };

    private static Set<String> percentWeapon = null;

    public static List<Attr> compute(ItemStack stack, List<String> displayLines, List<String> originalLines) {
        if (stack == null || stack.getItem() == null) {
            return Collections.emptyList();
        }
        Item item = stack.getItem();

        long key = makeKey(stack);
        List<Attr> staticAttrs = RAW_CACHE.get(key);
        if (staticAttrs == null) {
            staticAttrs = computeRaw(item, stack);
            RAW_CACHE.put(key, staticAttrs);
        }

        List<Attr> dynamicAttrs = new ArrayList<>();
        if (canHaveModifierAttrs(item, stack)) {
            addModifierAttrs(item, stack, dynamicAttrs);
            addCombatFallback(item, dynamicAttrs);
        }

        boolean isPctWeapon = isPercentDamageWeapon(stack);

        StringBuilder allText = new StringBuilder();
        for (String line : originalLines) {
            allText.append(line);
        }

        float bestDamage = parseTextDamage(allText.toString());
        if (bestDamage == 0 && !isPctWeapon) {
            bestDamage = findDamageValueInRaw(dynamicAttrs);
        }

        List<Attr> result = new ArrayList<>(staticAttrs.size() + dynamicAttrs.size() + 3);
        if (bestDamage != 0) {
            result.add(new Attr(Kind.ATTACK_DAMAGE, formatDamageValue(bestDamage)));
        }
        if (isPctWeapon) {
            float pct = parsePercentDamage(originalLines);
            if (pct > 0F) {
                result.add(new Attr(Kind.PERCENT_DAMAGE, "+" + Math.round(pct) + "%"));
            }
        }
        for (Attr a : dynamicAttrs) {
            if (a.kind != Kind.ATTACK_DAMAGE) result.add(a);
        }
        result.addAll(staticAttrs);
        addDurabilityAttr(stack, result);

        String ranged = parseRangedDamage(originalLines);
        if (ranged != null) {
            result.add(new Attr(Kind.RANGED_DAMAGE, ranged));
        }

        DupGuard guard = DupGuard.from(displayLines);
        List<Attr> filtered = new ArrayList<>(result.size());
        for (Attr a : result) {
            if (!guard.isEmpty() && guard.suppresses(a.kind)) {
                continue;
            }
            if (isZeroDisplay(a.value)) {
                continue;
            }
            filtered.add(a);
        }

        filtered = applyInfinityOverrides(stack, filtered);

        return filtered;
    }

    private static long makeKey(ItemStack stack) {
        Item item = stack.getItem();
        int meta = stack.isItemStackDamageable() ? 0 : stack.getItemDamage();
        return ((long) System.identityHashCode(item) << 32) | (meta & 0xFFFFFFFFL);
    }

    private static List<Attr> computeRaw(Item item, ItemStack stack) {
        List<Attr> result = new ArrayList<>();
        addFuelAttr(stack, result);
        addFoodAttrs(item, stack, result);
        addArmorAttr(item, result);
        addToolSpeedAttr(item, result);
        return result;
    }

    private static void addToolSpeedAttr(Item item, List<Attr> result) {
        try {
            if (item instanceof ItemTool) {
                float speed = ((ItemTool) item).func_150913_i().getEfficiencyOnProperMaterial();
                if (speed > 1.0F) {
                    result.add(new Attr(Kind.TOOL_SPEED, formatFloat(speed, 1)));
                }
            } else if (item instanceof ItemShears) {
                result.add(new Attr(Kind.TOOL_SPEED, "2.0"));
            }
        } catch (Throwable ignored) {
        }
    }

    private static void addArmorAttr(Item item, List<Attr> result) {
        if (!(item instanceof ItemArmor armor)) return;
        try {
            if (armor.damageReduceAmount > 0) {
                result.add(new Attr(Kind.ARMOR, "+" + armor.damageReduceAmount));
            }
        } catch (Throwable ignored) {
        }
    }

    private static void addFoodAttrs(Item item, ItemStack stack, List<Attr> result) {
        try {
            ItemFood food = (ItemFood) item;
            int hunger = food.func_150905_g(stack);
            float satMod = food.func_150906_h(stack);
            float saturation = hunger * satMod * 2.0F;
            result.add(new Attr(Kind.HUNGER, "+" + hunger));
            result.add(new Attr(Kind.SATURATION, formatFloat(saturation, 1)));
        } catch (Throwable ignored) {
        }
    }

    private static void addFuelAttr(ItemStack stack, List<Attr> result) {
        try {
            int burn = TileEntityFurnace.getItemBurnTime(stack);
            if (burn > 0) {
                double items = burn / 200.0;
                String value;
                if (items >= 10) {
                    value = String.valueOf((int) Math.round(items));
                } else if (items == Math.floor(items)) {
                    value = String.valueOf((int) items);
                } else {
                    value = String.format(Locale.ROOT, "%.1f", items);
                }
                result.add(new Attr(Kind.FUEL, value));
            }
        } catch (Throwable ignored) {
        }
    }

    private static String formatFloat(float v, int decimals) {
        if (decimals <= 0) {
            return Integer.toString(Math.round(v));
        }
        if (decimals == 1) {
            return String.format(Locale.ROOT, "%.1f", v);
        }
        return String.format(Locale.ROOT, "%.2f", v);
    }

    private static boolean canHaveModifierAttrs(Item item, ItemStack stack) {
        if (item instanceof ItemSword) {
            return true;
        }
        if (item instanceof ItemTool) {
            return true;
        }
        if (item instanceof ItemHoe) {
            return true;
        }
        if (item instanceof ItemShears) {
            return true;
        }
        if (item instanceof ItemBow) {
            return true;
        }
        if (item instanceof ItemArmor) {
            return true;
        }
        return stack.getTagCompound() != null;
    }

    private static void addModifierAttrs(Item item, ItemStack stack, List<Attr> out) {
        try {
            Multimap<String, AttributeModifier> mods = item.getAttributeModifiers(stack);
            if (mods == null || mods.isEmpty()) return;

            String dmgKey = SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName();
            String spdKey = SharedMonsterAttributes.movementSpeed.getAttributeUnlocalizedName();
            String hpKey = SharedMonsterAttributes.maxHealth.getAttributeUnlocalizedName();
            String kbKey = SharedMonsterAttributes.knockbackResistance.getAttributeUnlocalizedName();

            double[] dmg = sumByOp(mods.get(dmgKey));
            double[] spd = sumByOp(mods.get(spdKey));
            double[] hp  = sumByOp(mods.get(hpKey));
            double[] kb  = sumByOp(mods.get(kbKey));

            if (dmg[0] > 0.0001) {
                out.add(new Attr(Kind.ATTACK_DAMAGE, "+" + formatFloat((float) dmg[0], 0)));
            }
            double spdPct = (spd[1] + spd[2]) * 100.0;
            if (Math.abs(spd[0]) > 0.0001) {
                spdPct += spd[0] * 100.0;
            }
            if (spdPct > 0.0001) {
                out.add(new Attr(Kind.MOVEMENT_SPEED, "+" + formatFloat((float) spdPct, 0) + "%"));
            }
            if (hp[0] > 0.0001) {
                out.add(new Attr(Kind.MAX_HEALTH, "+" + formatFloat((float) hp[0], 0)));
            } else {
                double hpPct = (hp[1] + hp[2]) * 100.0;
                if (hpPct > 0.0001) {
                    out.add(new Attr(Kind.MAX_HEALTH, "+" + formatFloat((float) hpPct, 0) + "%"));
                }
            }
            double kbPct = (kb[1] + kb[2]) * 100.0;
            if (Math.abs(kb[0]) > 0.0001) kbPct += kb[0] * 100.0;
            if (kbPct > 0.0001) {
                out.add(new Attr(Kind.KNOCKBACK_RESISTANCE, "+" + formatFloat((float) kbPct, 0) + "%"));
            }
        } catch (Throwable ignored) {
        }
    }

    private static double[] sumByOp(Collection<AttributeModifier> mods) {
        double[] sums = new double[3];
        if (mods == null || mods.isEmpty()) return sums;
        for (AttributeModifier m : mods) {
            int op = m.getOperation();
            if (op >= 0 && op < 3) sums[op] += m.getAmount();
        }
        return sums;
    }

    private static void addCombatFallback(Item item, List<Attr> out) {
        boolean isSword = item instanceof ItemSword;
        boolean isTool = item instanceof ItemTool;
        if (!isSword && !isTool) return;

        float dmg = readDirectDamage(item);
        if (dmg <= 0F) return;

        removeKind(out, Kind.ATTACK_DAMAGE);
        out.add(0, new Attr(Kind.ATTACK_DAMAGE, "+" + formatFloat(dmg, 0)));
    }

    @SuppressWarnings("all")
    private static float readDirectDamage(Item item) {
        try {
            if (item instanceof ItemSword) {
                return ((ItemSword) item).func_150931_i();
            }
            if (item instanceof ItemTool) {
                try {
                    Field f;
                    try {
                        f = ItemTool.class.getDeclaredField("damageVsEntity");
                    } catch (NoSuchFieldException primary) {
                        try {
                            f = ItemTool.class.getDeclaredField("field_77865_bY");
                        } catch (NoSuchFieldException ignored) {
                            f = null;
                        }
                    }
                    if (f != null) {
                        f.setAccessible(true);
                        return f.getFloat(item);
                    }
                } catch (Throwable t) {
                    return ((ItemTool) item).func_150913_i().getDamageVsEntity() + 1F;
                }
            }
        } catch (Throwable ignored) {
        }
        return 0F;
    }

    @SuppressWarnings("all")
    private static void removeKind(List<Attr> list, Kind kind) {
        list.removeIf(attr -> attr.kind == kind);
    }

    private static boolean isPercentDamageWeapon(ItemStack item) {
        if (percentWeapon == null) {
            percentWeapon = new HashSet<>(Arrays.asList(FoxLibConfig.itemsWithPercentDamage));
        }

        try {
            String n = ThemeRegister.getFullName(item);
            if (percentWeapon.contains(n)) {
                return true;
            }
        } catch (Throwable ignored) {
        }
        return false;
    }

    public static float parseTextDamage(String cleanLine) {
        if (cleanLine == null || cleanLine.isEmpty()) {
            return 0.0F;
        }

        for (String blacklisted : FoxLibConfig.damageBlacklist) {
            if (cleanLine.contains(blacklisted)) {
                return 0.0F;
            }
        }

        boolean hasKeyword = false;
        for (String keyword : FoxLibConfig.damageKeywords) {
            if (cleanLine.contains(keyword)) {
                hasKeyword = true;
                break;
            }
        }

        if (!hasKeyword) {
            if (!cleanLine.startsWith("+") && !cleanLine.startsWith("-")) {
                return 0.0F;
            }
        }

        try {
            String numberText = cleanLine.replaceAll("[^0-9_\\-.,]", "");
            if (numberText.isEmpty()) {
                return 0.0F;
            }

            numberText = numberText.replace(',', '.');

            return Float.parseFloat(numberText);
        } catch (NumberFormatException e) {
            return 0.0F;
        }
    }

    private static float findDamageValueInRaw(List<Attr> raw) {
        for (Attr a : raw) {
            if (a.kind != Kind.ATTACK_DAMAGE) continue;
            String s = a.value;
            int start = 0;
            if (start < s.length() && (s.charAt(start) == '+' || s.charAt(start) == '-')) start++;
            int end = start;
            while (end < s.length()) {
                char c = s.charAt(end);
                if ((c >= '0' && c <= '9') || c == '.') end++;
                else break;
            }
            if (end > start) {
                try {
                    return Float.parseFloat(s.substring(start, end));
                } catch (Throwable t) {
                    return 0;
                }
            }
        }
        return 0;
    }

    private static String formatDamageValue(float v) {
        if (v == Math.floor(v) && !Float.isInfinite(v)) {
            return "+" + (long) v;
        }
        return "+" + String.format(Locale.ROOT, "%.1f", v);
    }

    private static float parsePercentDamage(List<String> lines) {
        if (lines == null || lines.isEmpty()) {
            return 0;
        }

        for (String raw : lines) {
            if (raw == null) {
                continue;
            }

            String l = stripFmtLower(raw);
            if (l.isEmpty()) {
                continue;
            }

            boolean isFlavor = false;
            for (String reject : FoxLibConfig.damageBlacklist) {
                if (l.contains(reject)) {
                    isFlavor = true;
                    break;
                }
            }
            if (isFlavor) {
                continue;
            }

            if (!l.startsWith("+")) {
                continue;
            }

            if (!isPercentageNumber(l)) {
                continue;
            }

            boolean isOtherStat = false;
            for (String stat : FoxLibConfig.percentDamageBlacklist) {
                if (l.contains(stat)) {
                    isOtherStat = true;
                    break;
                }
            }
            if (isOtherStat) {
                continue;
            }

            float n = extractNumberAt(l);
            if (n  > 0F) {
                return n;
            }
        }
        return 0;
    }

    private static String stripFmtLower(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        return AttributeFilter.stripFmt(text).toLowerCase(Locale.ROOT).trim();
    }

    private static boolean isPercentageNumber(String l) {
        int from = 1;
        while (from < l.length()) {
            char c = l.charAt(from);
            if (c == ' ' || c == ':' || c == '-' || c == '=' || c == '\t') {
                from++;
            } else {
                break;
            }
        }
        boolean sawDigit = false;
        while (from < l.length()) {
            char c = l.charAt(from);
            if ((c >= '0' && c <= '9') || c == '.' || c == ',') {
                from++;
                sawDigit = true;
            } else {
                break;
            }
        }
        if (!sawDigit) {
            return false;
        }
        return from < l.length() && l.charAt(from) == '%';
    }

    private static float extractNumberAt(String l) {
        int from = 1;
        while (from < l.length()) {
            char c = l.charAt(from);
            if (c == ' ' || c == ':' || c == '-' || c == '=' || c == '\t') {
                from++;
            } else {
                break;
            }
        }
        if (from >= l.length()) {
            return 0;
        }

        StringBuilder token = new StringBuilder();
        boolean started = false;
        for (int i = from; i < l.length(); i++) {
            char c = l.charAt(i);
            if (c >= '0' && c <= '9') {
                token.append(c);
                started = true;
            } else if (started && (c == '.' || c == ',' || c == ' ' || c == '\u00A0')) {
                token.append(c);
            } else if (started) {
                break;
            } else {
                break;
            }
        }
        if (token.toString().isEmpty()) {
            return 0;
        }
        return normalizeAndParse(token.toString());
    }

    private static float normalizeAndParse(String s) {
        if (s == null || s.isEmpty()) {
            return 0;
        }
        int lastSep = -1;
        for (int i = s.length() - 1; i >= 0; i--) {
            char c = s.charAt(i);
            if (c == '.' || c == ',' || c == ' ') {
                lastSep = i;
                break;
            }
        }
        String normalized;
        if (lastSep < 0) {
            normalized = s;
        } else {
            int afterSep = s.length() - lastSep - 1;
            boolean isDecimal = (afterSep == 1 || afterSep == 2);
            StringBuilder out = new StringBuilder(s.length());
            for (int i = 0; i < s.length(); i++) {
                char c = s.charAt(i);
                if (c == '.' || c == ',' || c == ' ') {
                    if (isDecimal && i == lastSep) out.append('.');
                } else {
                    out.append(c);
                }
            }
            normalized = out.toString();
        }
        if (normalized.isEmpty()) {
            return 0;
        }
        try {
            return Float.parseFloat(normalized);
        } catch (Throwable t) {
            return 0;
        }
    }

    private static void addDurabilityAttr(ItemStack stack, List<Attr> out) {
        try {
            if (!stack.isItemStackDamageable()) {
                return;
            }
            int max = stack.getMaxDamage();
            if (max <= 0) {
                return;
            }
            int left = max - stack.getItemDamage();
            float ratio = (float) left / (float) max;
            int color;
            if (ratio > 0.66F) {
                color = 0xFF80E080;
            } else if (ratio > 0.33F) {
                color = 0xFFE0E080;
            } else if (ratio > 0.15F) {
                color = 0xFFE0A040;
            } else {
                color = 0xFFE05050;
            }
            out.add(new Attr(Kind.DURABILITY, left + "/" + max, color));
        } catch (Throwable ignored) {
        }
    }

    private static String parseRangedDamage(List<String> lines) {
        if (lines == null || lines.isEmpty()) {
            return null;
        }

        for (String raw : lines) {
            if (raw == null) {
                continue;
            }

            String l = stripFmtLower(raw);
            if (l.isEmpty()) {
                continue;
            }

            boolean hasRangedKeyword = false;
            for (String keyword : FoxLibConfig.rangedDamageKeywords) {
                if (l.contains(keyword)) {
                    hasRangedKeyword = true;
                    break;
                }
            }
            if (!hasRangedKeyword) {
                continue;
            }

            int firstStart = -1, firstEnd = -1;
            for (int i = 0; i < l.length(); i++) {
                if (l.charAt(i) >= '0' && l.charAt(i) <= '9') {
                    firstStart = i;
                    break;
                }
            }
            if (firstStart < 0) {
                continue;
            }

            firstEnd = getSecondEnd(l, firstStart, firstEnd);
            String firstNum = l.substring(firstStart, firstEnd).replace(',', '.');

            int secondStart = -1, secondEnd = -1;
            for (int i = firstEnd; i < l.length(); i++) {
                if (l.charAt(i) >= '0' && l.charAt(i) <= '9') {
                    secondStart = i;
                    break;
                }
            }
            if (secondStart > 0) {
                secondEnd = getSecondEnd(l, secondStart, secondEnd);
            }

            try {
                int a = Math.round(Float.parseFloat(firstNum));
                if (secondStart > 0 && secondEnd > secondStart) {
                    String secondNum = l.substring(secondStart, secondEnd).replace(',', '.');
                    int b = Math.round(Float.parseFloat(secondNum));
                    if (b > a) {
                        return a + "-" + b;
                    }
                }
                return "+" + a;
            } catch (Throwable ignored) {
            }
        }
        return null;
    }

    private static int getSecondEnd(String l, int secondStart, int secondEnd) {
        for (int i = secondStart; i < l.length(); i++) {
            char c = l.charAt(i);
            if ((c >= '0' && c <= '9') || c == '.' || c == ',') {
                secondEnd = i + 1;
            } else {
                break;
            }
        }
        return secondEnd;
    }

    private static boolean isZeroDisplay(String v) {
        if (v == null) {
            return true;
        }
        int start = 0, end = v.length();
        if (end > 0 && (v.charAt(0) == '+' || v.charAt(0) == '-')) {
            start = 1;
        }
        if (end > start && v.charAt(end - 1) == '%') {
            end--;
        }
        if (start >= end) {
            return false;
        }
        for (int i = start; i < end; i++) {
            char c = v.charAt(i);
            if (c == '0' || c == '.' || c == ',') {
                continue;
            }
            if (c == '/') {
                return false;
            }
            return false;
        }
        return true;
    }

    private static List<Attr> applyInfinityOverrides(ItemStack itemS, List<Attr> attrs) {
        try {
            String name = ThemeRegister.getFullName(itemS);
            Item item = itemS.getItem();
            boolean avaritia = name.startsWith("avaritia:") || name.contains(".avaritia.") || name.startsWith("avaritia.");
            if (!avaritia || !name.contains("infinity")) {
                return attrs;
            }

            boolean isSword = item instanceof ItemSword;
            boolean isArmor = item instanceof ItemArmor;
            boolean isTool = (item instanceof ItemTool) || (item instanceof ItemHoe) || (item instanceof ItemShears);

            String INF = "∞";
            List<Attr> out = new ArrayList<>(attrs.size() + 2);
            boolean hasDamage = false, hasArmor = false, hasDur = false, hasToolSpeed = false;
            for (Attr a : attrs) {
                if (a.kind == Kind.ATTACK_DAMAGE && isSword) {
                    out.add(new Attr(Kind.ATTACK_DAMAGE, INF, a.color));
                    hasDamage = true;
                } else if (a.kind == Kind.ARMOR && isArmor) {
                    out.add(new Attr(Kind.ARMOR, INF, a.color));
                    hasArmor = true;
                } else if (a.kind == Kind.TOOL_SPEED && isTool) {
                    out.add(new Attr(Kind.TOOL_SPEED, INF, a.color));
                    hasToolSpeed = true;
                } else if (a.kind == Kind.DURABILITY) {
                    out.add(new Attr(Kind.DURABILITY, INF, a.color));
                    hasDur = true;
                } else {
                    out.add(a);
                }
            }
            if (isSword && !hasDamage) {
                out.add(0, new Attr(Kind.ATTACK_DAMAGE, INF));
            }
            if (isArmor && !hasArmor) {
                out.add(new Attr(Kind.ARMOR, INF));
            }
            if (isTool && !hasToolSpeed) {
                out.add(new Attr(Kind.TOOL_SPEED, INF));
            }
            if (!hasDur) {
                out.add(new Attr(Kind.DURABILITY, INF, 0xFF80E080));
            }
            return out;
        } catch (Throwable t) {
            return attrs;
        }
    }
}
