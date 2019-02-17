package perfecthash;

import org.junit.Test;

import java.util.Random;
import java.util.stream.IntStream;

public class PerfectHashMapTest {
    private static final int TAG_LIMIT = 50_000;
    private static final int COUNT = 1020;

    private final Random random = new Random();

    @Test
    public void test1() {
        final TestObj[] objs = IntStream.generate(() -> random.nextInt(TAG_LIMIT))
                .distinct()
                .limit(COUNT)
                .mapToObj(TestObj::new).toArray(TestObj[]::new);
        final PerfectHashMap perfectHashMap = PerfectHashMap.build(objs);


        int countFound = 0;
        for (int i = 0; i < TAG_LIMIT; i++) {
            final IdHolder obj = perfectHashMap.get(i);
            if (obj != null) {
                if (i != obj.getId()) {
                    System.out.println("for id = " + i + " obj with id = " + obj.getId() + " was found");
                } else {
                    countFound++;
                }
            }
        }

        if (COUNT != countFound) {
            System.out.println("not all objects was found. Found " + countFound + " of " + COUNT);
        }
    }

    private static class TestObj implements IdHolder {
        final int id;

        private TestObj(int id) {
            this.id = id;
        }

        @Override
        public int getId() {
            return id;
        }
    }
}