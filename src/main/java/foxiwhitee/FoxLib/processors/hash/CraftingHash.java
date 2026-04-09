package foxiwhitee.FoxLib.processors.hash;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CraftingHash {
    private final Map<Integer, Integer> hash = new HashMap<>();

    public CraftingHash() {

    }

    public void clear() {
        hash.clear();
    }

    public void put(int idx, int count) {
        hash.put(idx, count);
    }

    public void putAll(Map<Integer, Integer> map) {
        hash.putAll(map);
    }

    public Set<Map.Entry<Integer, Integer>> getEntrySet() {
        return hash.entrySet();
    }

    public int size() {
        return hash.size();
    }

    public void writeToNbt(NBTTagCompound data) {
        NBTTagList list = new NBTTagList();
        for (Map.Entry<Integer, Integer> entry : hash.entrySet()) {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setInteger("idx", entry.getKey());
            tag.setInteger("count", entry.getValue());
            list.appendTag(tag);
        }
        data.setTag("hash", list);
    }

    public void readFromNbt(NBTTagCompound data) {
        NBTTagList list = data.getTagList("hash", 10);
        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound tag = list.getCompoundTagAt(i);
            int idx = tag.getInteger("idx");
            int count = tag.getInteger("count");
            hash.put(idx, count);
        }
    }
}
