package test;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        //System.out.println(System.getProperty("java.ext.dirs"));
        //(new Main()).testSortLinkedList();

        (new Main()).testSet();
    }

    class Test {
        float x;
        float y;

        Test(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }

    class SingleTest {
        float x;

        SingleTest(float x) {
            this.x = x;
        }
    }

    private void testSet() {
        Map<SingleTest, SingleTest> set = new HashMap<>();
        float x = 42;
        float y = 13;

        SingleTest x1 = new SingleTest(x);
        SingleTest y1 = new SingleTest(y);

        addToSet(x1, y1, set);
        addToSet(y1, x1, set);

        System.out.println(set.size());
    }

    private void addToSet(SingleTest x, SingleTest y, Map<SingleTest, SingleTest> set) {
        SingleTest value = set.get(y);
        if (value == null || !value.equals(x)) {
            set.put(x, y);
        }
    }

    private void testSortLinkedList() {
        LinkedList<Test> list = new LinkedList<>();

        list.addLast(new Test(10, 15));
        list.addLast(new Test(5, 8));
        list.addLast(new Test(13, 25));
        list.addLast(new Test(11, 7));

        Collections.sort(list, new Comparator<Test>() {
            @Override
            public int compare(Test o1, Test o2) {
                if (o1.y < o2.y) {
                    return -1;
                } else if (o1.y > o2.y) {
                    return 1;
                }
                return 1;
            }
        });

        for (Test t : list) {
            System.out.println(t.x + " " + t.y);
        }
    }
}
