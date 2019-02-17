package perfecthash;

import java.util.*;
import java.util.stream.IntStream;

public class PerfectHashMap {
    private final int bitShift;
    private final int mask;

    private final int[] offset;
    private final IdHolder[] objects;

    private PerfectHashMap(int bitShift, int[] offset, IdHolder[] objects) {
        this.bitShift = bitShift;
        this.mask = getMask(bitShift);
        this.offset = offset;
        this.objects = objects;
    }

    public IdHolder get(int id) {
        final int basket = id & mask;
        final int index = offset[basket] + (id >> bitShift);
        if (0 <= index && index < objects.length) {
            final IdHolder object = objects[index];
            if (object != null && object.getId() == id) {
                return object;
            }
        }
        return null;
    }

    private static int getMask(int bitShift) {
        return (1 << bitShift) - 1;
    }

    public static PerfectHashMap build(IdHolder[] source) {
        final int basketCount = Integer.highestOneBit(source.length);
        final int bitShift = Integer.numberOfTrailingZeros(basketCount);
        final int mask = basketCount - 1;

        final Map<Integer, Basket> basketMap = new HashMap<>();
        for (IdHolder obj : source) {
            basketMap.computeIfAbsent(obj.getId() & mask, key -> new Basket(key, bitShift))
                    .add(obj);
        }

        final List<Basket> baskets = new ArrayList<>(basketMap.values());
        Collections.shuffle(baskets);
        baskets.sort(Comparator.comparingInt(Basket::width).reversed());

        final int capacity = baskets.stream()
                .mapToInt(Basket::width)
                .sum();
        final IdHolder[] objects = new IdHolder[capacity];

        final int[] offsets = new int[basketCount];
        for (Basket basket : baskets) {
            offsets[basket.getKey()] = basket.putToArray(objects);
        }

        final int length = IntStream.range(0, capacity)
                .filter(i -> objects[i] != null)
                .max().orElse(-1) + 1;
        return new PerfectHashMap(bitShift, offsets, Arrays.copyOfRange(objects, 0, length));
    }
}
