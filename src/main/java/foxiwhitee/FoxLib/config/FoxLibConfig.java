package foxiwhitee.FoxLib.config;

@Config(folder = "Fox-Mods", name = "FoxLib")
public class FoxLibConfig {
    @ConfigValue(desc = "Enable tooltips?")
    public static boolean enableTooltips = true;

    @ConfigValue(desc = "Enables the hand command. The command prints the name of the item in the hand to the chat in the format <modId:itemName.meta>. Available to everyone")
    public static boolean enableHandCommand = true;

}
