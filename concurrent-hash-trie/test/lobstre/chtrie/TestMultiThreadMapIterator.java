package lobstre.chtrie;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TestMultiThreadMapIterator {
    private static final int NTHREADS = 7;

    public static void main (final String[] args) {
        final Map<Object, Object> bt = new ConcurrentHashTrieMap<Object, Object> ();
        for (int j = 0; j < 50 * 1000; j++) {
            final Object[] objects = getObjects (j);
            for (final Object o : objects) {
                bt.put (o, o);
            }
        }

        {
            final ExecutorService es = Executors.newFixedThreadPool (NTHREADS);
            for (int i = 0; i < NTHREADS; i++) {
                final int threadNo = i;
                es.execute (new Runnable () {
                    @Override
                    public void run () {
                        for (final Iterator<Map.Entry<Object, Object>> i = bt.entrySet ().iterator (); i.hasNext ();) {
                            final Entry<Object, Object> e = i.next ();
                            if (accepts (threadNo, NTHREADS, e.getKey ())) {
                                e.setValue ("TEST:" + threadNo);
                            }
                        }
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

        int count = 0;
        for (final Object value : bt.values ()) {
            TestHelper.assertTrue (value instanceof String);
            count++;
        }
        TestHelper.assertEquals (50000 + 2000 + 1000 + 100, count);
        
        final ConcurrentHashMap<Object, Object> removed = new ConcurrentHashMap<Object, Object> ();

        {
            final ExecutorService es = Executors.newFixedThreadPool (NTHREADS);
            for (int i = 0; i < NTHREADS; i++) {
                final int threadNo = i;
                es.execute (new Runnable () {
                    @Override
                    public void run () {
                        for (final Iterator<Map.Entry<Object, Object>> i = bt.entrySet ().iterator (); i.hasNext ();) {
                            final Entry<Object, Object> e = i.next ();
                            Object key = e.getKey ();
                            if (accepts (threadNo, NTHREADS, key)) {
                                if (null == bt.get (key)) {
                                    System.out.println (key);
                                }
                                i.remove ();
                                if (null != bt.get (key)) {
                                    System.out.println (key);
                                }
                                removed.put (key, key);
                            }
                        }
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

        count = 0;
        for (final Object value : bt.keySet ()) {
            value.toString ();
            count++;
        }
        for (final Object o : bt.keySet ()) {
            if (!removed.contains (bt.get (o))) {
                System.out.println ();
            }
        }
        TestHelper.assertEquals (0, count);
        TestHelper.assertEquals (0, bt.size ());
        TestHelper.assertTrue (bt.isEmpty ());
    }

    protected static boolean accepts (final int threadNo, final int nThreads, final Object key) {
        int val = 0;
        if (key instanceof Integer) {
            val = ((Integer) key).intValue ();
        }
        if (key instanceof Character) {
            val = Math.abs (Character.getNumericValue ((Character) key) + 1);
        }
        if (key instanceof Short) {
            val = ((Short) key).intValue () + 2;
        }
        if (key instanceof Byte) {
            val = ((Byte) key).intValue () + 3;
        }
        return val % nThreads == threadNo;
    }

    static Object[] getObjects (final int j) {
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
