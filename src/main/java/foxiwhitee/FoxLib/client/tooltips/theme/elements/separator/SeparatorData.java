package foxiwhitee.FoxLib.client.tooltips.theme.elements.separator;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SeparatorData implements Comparable<SeparatorData>{
    public final List<Color> colors;
    public final int position;

    public SeparatorData(List<Color> colors, int position) {
        this.colors = colors;
        this.position = position;
    }

    public SeparatorData(int position, List<Integer> colors) {
        this.colors = new ArrayList<>(colors.size());
        for (int c : colors) {
            this.colors.add(new Color(c | 0xFF000000, true));
        }
        this.position = position;
    }

    @Override
    public int compareTo(SeparatorData other) {
        int p1 = this.position;
        int p2 = other.position;

        if (p1 == -1 && p2 == -1) {
            return 0;
        }
        if (p1 == -1) {
            return 1;
        }
        if (p2 == -1) {
            return -1;
        }

        return Integer.compare(p1, p2);
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        SeparatorData that = (SeparatorData) object;
        return position == that.position && Objects.equals(colors, that.colors);
    }

    @Override
    public int hashCode() {
        return Objects.hash(colors, position);
    }
}
