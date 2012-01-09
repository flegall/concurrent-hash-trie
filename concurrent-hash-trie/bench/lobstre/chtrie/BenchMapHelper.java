package lobstre.chtrie;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentSkipListMap;


public class BenchMapHelper {

    public static void logTime (final Map<Object, Object> map, final int count, final long begin, final long end) {
        final double time = end - begin;
        final double perPut = time / (double) (count);
        
        System.out.println (map.getClass () + " : " + perPut);
    }
    
    public static ConcurrentSkipListMap<Object, Object> getSkipListMap () {
        return new ConcurrentSkipListMap<Object, Object> (getComparator ());
    }

    public static TreeMap<Object, Object> getTreeMap () {
        return new TreeMap<Object, Object> (getComparator ());
    }

    private static Comparator<Object> getComparator () {
        return new Comparator<Object>() {
            @SuppressWarnings("unchecked")
            @Override
            public int compare (Object o1, Object o2) {
                final Comparable<Object> c1 = (Comparable<Object>) o1;
                final Comparable<Object> c2 = (Comparable<Object>) o2;
                if (o1.getClass ().equals (o2.getClass ())) {
                    return c1.compareTo (c2);
                } else {
                    return c1.getClass ().getName ().compareTo (c2.getClass ().getName ());
                }
            }
        };
    }
}
