package lobstre.chtrie;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TestMultiThreadMapIterator {
    public static void main (final String[] args) {
        final Map<Object, Object> bt = new ConcurrentHashTrieMap<Object, Object> ();
        for (int j = 0; j < 50 * 1000; j++) {
            Object[] objects = getObjects (j);
            for (Object o : objects) {
                bt.put (o, o);
            }
        }
        
        final int nThreads = 7;
        final ExecutorService es = Executors.newFixedThreadPool (nThreads);
        for (int i = 0; i < nThreads; i++) {
            final int threadNo = i;
            es.execute (new Runnable () {
                @Override
                public void run () {
                }
            });
        }

        es.shutdown ();
        try {
            es.awaitTermination (3600L, TimeUnit.SECONDS);
        } catch (final InterruptedException e) {
            e.printStackTrace ();
        }
    }

    private static Object[] getObjects (int j) {
        final Collection<Object> results = new LinkedList<Object> ();
        results.add (Integer.valueOf (j));
        if (j < 2000) {
            results.add (Character.valueOf ((char) j));
        }
        if (j < 1000) {
            results.add (Short.valueOf ((short) j));
        }
        if (j < 100) {
            results.add (Byte.valueOf ((byte) j));
        }
        
        return results.toArray ();
    }
}
