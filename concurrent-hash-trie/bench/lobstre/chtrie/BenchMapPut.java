package lobstre.chtrie;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BenchMapPut {
    public static void main (final String[] args) {
        BenchMapPut.bench (new ConcurrentHashTrieMap<Object, Object> ());
        BenchMapPut.bench (new HashMap<Object, Object> ());
        BenchMapPut.bench (Collections.synchronizedMap (new HashMap<Object, Object> ()));
        BenchMapPut.bench (new ConcurrentHashMap<Object, Object> ());
        BenchMapPut.bench (BenchMapHelper.getTreeMap ());
        BenchMapMultiThreadPut.bench (Collections.synchronizedMap (BenchMapHelper.getTreeMap ()));
        BenchMapPut.bench (BenchMapHelper.getSkipListMap ());
    }

    public static void bench (final Map<Object, Object> map) {
        final List<Object> objects = new ArrayList<Object> ();
        final int count = 500 * 1000;
        for (int j = 0; j < count; j++) {
            final Object[] objs = TestMultiThreadMapIterator.getObjects (j);
            for (final Object o : objs) {
                objects.add (o);
            }
        }
        Collections.shuffle (objects);
        
        final long begin = System.nanoTime ();
        for (final Object o : objects) {
            map.put (o, o);
        }
        final long end = System.nanoTime ();
        
        BenchMapHelper.logTime (map, count, begin, end);
    }
}
