package lobstre.chtrie;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BenchMapRemove {
    public static void main (final String[] args) {
        BenchMapRemove.bench (new ConcurrentHashTrieMap<Object, Object> ());
        BenchMapRemove.bench (new HashMap<Object, Object> ());
        BenchMapRemove.bench (Collections.synchronizedMap (new HashMap<Object, Object> ()));
        BenchMapRemove.bench (new ConcurrentHashMap<Object, Object> ());
        BenchMapRemove.bench (BenchMapHelper.getTreeMap ());
        BenchMapMultiThreadPut.bench (Collections.synchronizedMap (BenchMapHelper.getTreeMap ()));
        BenchMapRemove.bench (BenchMapHelper.getSkipListMap ());
    }

    public static void bench (final Map<Object, Object> map) {
        final List<Object> objects = new ArrayList<Object> ();
        final int count = 500 * 1000;
        for (int j = 0; j < count; j++) {
            final Object[] objs = TestMultiThreadMapIterator.getObjects (j);
            for (final Object o : objs) {
                objects.add (o);
                map.put (o, o);
            }
        }
        Collections.shuffle (objects);

        final long begin = System.nanoTime ();
        for (final Object o : objects) {
            map.remove (o);
        }
        final long end = System.nanoTime ();

        BenchMapHelper.logTime (map, count, begin, end);
    }
}
