import java.util.Iterator;

public class IterablePratice {
    public void test(){
        Iterable<Integer> iter = () ->
                new Iterator<Integer>() {
                    int i = 0;
                    final static int MAX = 10;
                    public boolean hasNext() {
                        return i < MAX;
                    }

                    public Integer next() {
                        return ++i;
                    }
                };
        for (Integer i :iter){
            System.out.println(i);
        }
    }
}
