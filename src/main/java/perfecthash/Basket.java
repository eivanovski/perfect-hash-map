package perfecthash;

import java.util.ArrayList;

public class Basket {
    private int min = Integer.MAX_VALUE;
    private int max = Integer.MIN_VALUE;

    private final int key;
    private final int bitShift;
    private final ArrayList<IdHolder> objects;

    public Basket(int key, int bitShift) {
        this.key = key;
        this.bitShift = bitShift;
        this.objects = new ArrayList<>();
    }

    public int getKey() {
        return key;
    }

    private int indexOf(IdHolder obj) {
        return obj.getId() >> bitShift;
    }

    public void add(IdHolder obj) {
        final int index = indexOf(obj);
        min = Math.min(min, index);
        max = Math.max(max, index);
        objects.add(obj);
    }

    public int width() {
        return max - min + 1;
    }

    public int putToArray(IdHolder[] dest) {
        for (int index = 0; index < dest.length; index++) {
            if (dest[index] != null) continue;

            final int offset = index - min;
            if (tryToPut(dest, offset)) {
                return offset;
            }
        }
        throw new RuntimeException("Failed to put basket to obj array");
    }

    private boolean tryToPut(IdHolder[] dest, int offset) {
        for (IdHolder obj : objects) {
            final int index = offset + indexOf(obj);
            if (dest[index] != null) return false;
        }
        for (IdHolder obj : objects) {
            dest[offset + indexOf(obj)] = obj;
        }
        return true;
    }
}
