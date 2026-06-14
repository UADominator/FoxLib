package foxiwhitee.FoxLib.config;

@Config(folder = "Fox-Mods", name = "FoxLib")
public class FoxLibConfig {
    @ConfigValue(desc = "Enable tooltips?")
    public static boolean enableTooltips = true;

    @ConfigValue(desc = "Enables the hand command. The command prints the name of the item in the hand to the chat in the format <modId:itemName.meta>. Available to everyone")
    public static boolean enableHandCommand = true;

    @ConfigValue(category = "Tooltips", desc = "Enables improved tooltips")
    public static boolean enableCustomTooltips = true;

    @ConfigValue(category = "Tooltips.Animations", min = "0.25", max = "4", desc = "Appear-animation speed multiplier. 0.25 = default (600ms slow majestic), 1.0 = 150ms, 4.0 = 37ms instant")
    public static double animationSpeed = 0.25F;

    @ConfigValue(category = "Tooltips.Attributes", desc = "The text below the tooltip will be considered as attributes and deleted (later, regardless of the deleted text, it will be added as an icon). Use L+ at the beginning to mark the text that needs to be localized.")
    public static String[] tooltipsAttributes = {
        "L+attribute.modifier.plus.0",
        "L+attribute.modifier.plus.1",
        "L+attribute.modifier.take.0",

        "protect: %s",
        "захист: %s",
        "armor: %s",
        "броня: %s",
        "шкода: %s",
        "урон: %s",
        "damage: %s",
        "сила атаки: %s",
        "міцність: %s / %s",
        "міцність: %s/%s",
        "durability: %s / %s",
        "durability: %s/%s",
        "застосувань: %s",
        "ефективність: %s",
        "час горіння: %s",
        "fuel: %s",
        "burn time: %s",
        "ситість: %s",
        "насичення: %s",
        "hunger: %s",
        "+%s attack damage",
        "+%s сила атаки"
    };

    @ConfigValue(category = "Tooltips.Attributes", desc = "Attribute text that will be understood as damage")
    public static String[] damageAttributes = {
        "шкода",
        "урон",
        "damage",
        "attack damage",
        "сила атаки"
    };

    @ConfigValue(category = "Tooltips.Attributes", desc = "Attribute text that will be understood as armor")
    public static String[] armorAttributes = {
        "protect",
        "armor",
        "броня",
        "захист"
    };

    @ConfigValue(category = "Tooltips.Attributes", desc = "Attribute text that will be understood as fuel")
    public static String[] fuelAttributes = {
        "fuel",
        "час горіння"
    };

    @ConfigValue(category = "Tooltips.Attributes", desc = "Attribute text that will be understood as durability")
    public static String[] durabilityAttributes = {
        "міцність",
        "застосувань",
        "durability"
    };

    @ConfigValue(category = "Tooltips.Attributes", desc = "These items have damage as %. Write as modId:itemName")
    public static String[] itemsWithPercentDamage = {
        "DraconicEvolution:draconicSword",
        "DraconicEvolution:wyvernSword",
        "DraconicEvolution:draconicDistructionStaff"
    };

    @ConfigValue(category = "Tooltips.Attributes", desc = "These lines will not be marked as attributes")
    public static String[] damageBlacklist = {
        "killed",
        "kills",
        "вбито",
        "вбивств",
        "tier",
        "рівень",
        "level",
        "lvl",
        "cooldown",
        "перезарядка",
        "шанс",
        "chance"
    };

    @ConfigValue(category = "Tooltips.Attributes", desc = "These words are used as keywords to find damage")
    public static String[] damageKeywords = {
        "attack damage",
        "projectile damage",
        "ranged damage",
        "урон",
        "атака",
        "шкода"
    };

    @ConfigValue(category = "Tooltips.Attributes", desc = "These lines will not be marked as percent damage attribute")
    public static String[] percentDamageBlacklist = {
        "ефективність", "efficien",
        "швидкість копання", "mining speed", "dig speed",
        "крит", "crit", "критичний",
        "швидкість атаки", "attack speed",
        "міцність", "durability",
        "захист", "armor",
        "опір", "resistance"
    };

    @ConfigValue(category = "Tooltips.Attributes", desc = "These lines will be marked as ranged damage attribute")
    public static String[] rangedDamageKeywords = {
        "ranged damage",
        "ranged attack",
        "шкода в дальньому бою",
        "шкода дальнього бою",
        "атака дальнього бою",
        "дальній бій"
    };
}
