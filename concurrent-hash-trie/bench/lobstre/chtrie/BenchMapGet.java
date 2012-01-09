package lobstre.chtrie;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BenchMapGet {
    public static void main (final String[] args) {
        BenchMapGet.bench (new ConcurrentHashTrieMap<Object, Object> ());
        BenchMapGet.bench (new HashMap<Object, Object> ());
        BenchMapGet.bench (Collections.synchronizedMap (new HashMap<Object, Object> ()));
        BenchMapGet.bench (new ConcurrentHashMap<Object, Object> ());
        BenchMapGet.bench (BenchMapHelper.getTreeMap ());
        BenchMapMultiThreadPut.bench (Collections.synchronizedMap (BenchMapHelper.getTreeMap ()));
        BenchMapGet.bench (BenchMapHelper.getSkipListMap ());
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
            map.get (o);
        }
        final long end = System.nanoTime ();
        
        BenchMapHelper.logTime (map, count, begin, end);
    }
}
