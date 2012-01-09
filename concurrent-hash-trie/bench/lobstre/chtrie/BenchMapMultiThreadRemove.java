package lobstre.chtrie;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

public class BenchMapMultiThreadRemove {
    private static final int NTHREADS = 7;

    public static void main (final String[] args) {
        BenchMapMultiThreadRemove.bench (new ConcurrentHashTrieMap<Object, Object> ());
        BenchMapMultiThreadRemove.bench (Collections.synchronizedMap (new HashMap<Object, Object> ()));
        BenchMapMultiThreadRemove.bench (new ConcurrentHashMap<Object, Object> ());
        BenchMapMultiThreadRemove.bench (Collections.synchronizedMap (BenchMapHelper.getTreeMap ()));
        BenchMapMultiThreadRemove.bench (BenchMapHelper.getSkipListMap ());
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

        final CountDownLatch cdl = new CountDownLatch (NTHREADS);

        final long begin = System.nanoTime ();
        for (int i = 0; i < NTHREADS; i++) {
            final int threadId = i;
            new Thread (new Runnable () {
                @Override
                public void run () {
                    for (int j = 0; j < objects.size (); j++) {
                        if (j % NTHREADS == threadId) {
                            final Object o = objects.get (j);
                            map.remove (o);
                        }
                    }
                    cdl.countDown ();
                }
            }).start ();
        }
        try {
            cdl.await ();
        } catch (final InterruptedException e) {
            e.printStackTrace ();
            return;
        }
        final long end = System.nanoTime ();

        BenchMapHelper.logTime (map, count, begin, end);
    }
}
