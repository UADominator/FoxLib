package foxiwhitee.FoxLib.client.tooltips.builder;

import foxiwhitee.FoxLib.client.tooltips.theme.elements.separator.SeparatorConfig;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Stores data about line separators separating text in tooltip
 */
public final class SeparatorBuilder {
    private final Map<Integer, List<Integer>> data = new HashMap<>();

    SeparatorBuilder() {}

    /**
     * Adds a line separator that will separate lines of text if they are present in the tooltip
     * @param position Which line should I put a separator in front of? Start with 0. If you set -1, it will be located at the very end of the
     * @param colors Colors with which the separator line will shimmer
     * @return This
     */
    public SeparatorBuilder applySeparator(int position, List<Integer> colors) {
        data.put(position, colors);
        return this;
    }

    SeparatorConfig build() {
        SeparatorConfig config = new SeparatorConfig();
        for (Map.Entry<Integer, List<Integer>> entry : data.entrySet()) {
            config.addSeparator(entry.getKey(), entry.getValue());
        }
        return config;
    }
}
