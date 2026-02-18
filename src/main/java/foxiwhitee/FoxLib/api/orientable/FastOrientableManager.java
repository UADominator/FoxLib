package foxiwhitee.FoxLib.api.orientable;

import net.minecraftforge.common.util.ForgeDirection;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

public class FastOrientableManager {
    private static final AtomicInteger ID_GENERATOR = new AtomicInteger(0);

    private static final ThreadLocal<ForgeDirection[]> FORWARD_STORAGE = ThreadLocal.withInitial(() -> createArray(1024));
    private static final ThreadLocal<ForgeDirection[]> UP_STORAGE = ThreadLocal.withInitial(() -> createArray(1024));

    private static ForgeDirection[] createArray(int size) {
        ForgeDirection[] array = new ForgeDirection[size];
        Arrays.fill(array, ForgeDirection.UNKNOWN);
        return array;
    }

    public static int nextId() {
        return ID_GENERATOR.getAndIncrement();
    }

    public static ForgeDirection getForward(int id) {
        ForgeDirection[] array = FORWARD_STORAGE.get();
        return (id < array.length) ? array[id] : ForgeDirection.UNKNOWN;
    }

    public static ForgeDirection getUp(int id) {
        ForgeDirection[] array = UP_STORAGE.get();
        return (id < array.length) ? array[id] : ForgeDirection.UNKNOWN;
    }

    public static void set(int id, ForgeDirection forward, ForgeDirection up) {
        setInArray(FORWARD_STORAGE, id, forward != null ? forward : ForgeDirection.UNKNOWN);
        setInArray(UP_STORAGE, id, up != null ? up : ForgeDirection.UNKNOWN);
    }

    private static void setInArray(ThreadLocal<ForgeDirection[]> tl, int id, ForgeDirection dir) {
        ForgeDirection[] array = tl.get();
        if (id >= array.length) {
            int newSize = array.length;
            while (newSize <= id) newSize <<= 1;
            array = Arrays.copyOf(array, newSize);
            Arrays.fill(array, id, array.length, ForgeDirection.UNKNOWN);
            tl.set(array);
        }
        array[id] = (dir == null) ? ForgeDirection.UNKNOWN : dir;
    }
}
