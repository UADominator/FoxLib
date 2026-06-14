package foxiwhitee.FoxLib.client.tooltips.theme.elements.separator;

import java.awt.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SuppressWarnings("unused")
public class SeparatorConfig {
    private final Set<SeparatorData> separators = new HashSet<>();

    public SeparatorConfig() {}

    public Set<SeparatorData> getSeparators() {
        return Collections.unmodifiableSet(separators);
    }

    public void addSeparator(List<Color> colors, int position) {
        separators.add(new SeparatorData(colors, position));
    }

    public void addSeparator(int position, List<Integer> colors) {
        separators.add(new SeparatorData(position, colors));
    }
}
