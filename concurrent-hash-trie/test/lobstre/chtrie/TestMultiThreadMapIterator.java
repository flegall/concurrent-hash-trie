package lobstre.chtrie;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TestMultiThreadMapIterator {
    public static void main (final String[] args) {
        final Map<Object, Object> bt = new ConcurrentHashTrieMap<Object, Object> ();
        for (int j = 0; j < 50 * 1000; j++) {
            final Object[] objects = getObjects (j);
            for (final Object o : objects) {
                bt.put (o, o);
            }
        }

        final int nThreads = 7;
        {
            final ExecutorService es = Executors.newFixedThreadPool (7);
            for (int i = 0; i < nThreads; i++) {
                final int threadNo = i;
                es.execute (new Runnable () {
                    @Override
                    public void run () {
                        for (final Iterator<Map.Entry<Object, Object>> i = bt.entrySet ().iterator (); i.hasNext ();) {
                            final Entry<Object, Object> e = i.next ();
                            if (accepts (threadNo, nThreads, e.getKey ())) {
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
        final Map<Integer, Integer> counts = new TreeMap<Integer, Integer> ();
        for (final Object value : bt.values ()) {
            TestHelper.assertTrue (value instanceof String);
            if (value instanceof String) {
                final String[] splits = ((String) value).split (":");
                TestHelper.assertEquals ("TEST", splits [0]);
                final int slot = Integer.parseInt (splits [1]);
                Integer sum = counts.get (slot);
                if (null == sum) {
                    sum = new Integer (0);
                }
                counts.put (slot, new Integer (sum.intValue () + 1));
            }
            count++;
        }
        TestHelper.assertEquals (50000 + 2000 + 1000 + 100, count);
        TestHelper.assertEquals (9223, counts.get (0).intValue ());
        TestHelper.assertEquals (7316, counts.get (1).intValue ());
        TestHelper.assertEquals (7313, counts.get (2).intValue ());
        TestHelper.assertEquals (7314, counts.get (3).intValue ());
        TestHelper.assertEquals (7313, counts.get (4).intValue ());
        TestHelper.assertEquals (7311, counts.get (5).intValue ());
        TestHelper.assertEquals (7310, counts.get (6).intValue ());

        for (final Iterator<Map.Entry<Object, Object>> i = bt.entrySet ().iterator (); i.hasNext ();) {
            final Entry<Object, Object> e = i.next ();
            final Object key = e.getKey ();
            key.toString ();
            i.remove ();
        }

        count = 0;
        for (final Object value : bt.keySet ()) {
            value.toString ();
            count++;
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

    private static Object[] getObjects (final int j) {
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
