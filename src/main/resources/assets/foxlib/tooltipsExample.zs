import mods.foxlib.TooltipManager;

# Whoever reads this.
# I've been programming for the eighth time.
# I'm glad this crap works.
# There are some differences between the code version and this one.
# In general, there should be all the differences here.
# Anyway, go read the code

val styleTest = TooltipManager.createStyle("test")
    .bg("141A30", "080C40", 0.94)
    .border("8C7CE6", "3A2D6E")
    .accent("9C9000", "CFC7FF");

val partTest = TooltipManager.createParticles("test", "STAR")
    .data(24, 0.08, "000000") # Якщо тут третім параметром йшов колір — передаємо рядком
    .speed(30.0, 3.0, 4.0)
    .life(0.70, 0.70);

val sepTest = TooltipManager.createSeparator("test")
    .applySeparator(0, ["9C9000", "CFC7FF", "9C9000", "453328"]);

val lineTest = TooltipManager.createInnerLine("test")
    .data(0.6, ["FFFF00FF", "FF0000FF"]);

TooltipManager.createResource("frame_test", "foxlib", "textures/gui/frames/test.png");

val dummyRes = TooltipManager.fetchLastResource();

val frameTest = TooltipManager.createFrame("test")
    .data(dummyRes, 128, 96);

val themeTest = TooltipManager.createTheme("test")
    .appendStyle(styleTest)
    .appendParticles(partTest)
    .appendSeparator(sepTest)
    .hasIcon()
    .enableShadow()
    .appendInnerLine(lineTest);

TooltipManager.buildForItem(themeTest, "minecraft:stone");

val styleRare = TooltipManager.createStyle("rare")
    .all("281C32", "140A06", 0.94, "C08000", "604008", "E0A030", "FFA000");

val partRare = TooltipManager.createParticles("rare", "SPARK")
    .all(24, 0.08, 30.0, 0.70, 0.70, 3.0, 4.0, "000000"); # Останній аргумент — колір (строка)

val sepRare = TooltipManager.createSeparator("rare")
    .applySeparator(0, ["E0A030", "FFA000", "E0A030", "823017"]);

val themeRare = TooltipManager.createTheme("rare")
    .appendStyle(styleRare)
    .appendParticles(partRare)
    .appendSeparator(sepRare)
    .hasIcon()
    .enableShadow()
    .appendInnerLine(lineTest);

TooltipManager.buildForNBT(themeRare, {rarity: "rare"});
