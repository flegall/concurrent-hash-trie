package lobstre.chtrie;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TestMultiThreadAddDelete {
    private static final int N_THREADS = 7;
    private static final int COUNT = 10 * 1000;

    public static void main (final String[] args) {
        for (int j = 0; j < 20; j++) {
            final Map<Object, Object> bt = new ConcurrentHashTrieMap <Object, Object> ();
            
            {
                final ExecutorService es = Executors.newFixedThreadPool (N_THREADS);
                for (int i = 0; i < N_THREADS; i++) {
                    final int threadNo = i;
                    es.execute (new Runnable () {
                        @Override
                        public void run () {
                            for (int j = 0; j < COUNT; j++) {
                                if (j % N_THREADS == threadNo) {
                                    bt.put (Integer.valueOf (j), Integer.valueOf (j));
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
            
            TestHelper.assertEquals (COUNT, bt.size ());
            TestHelper.assertFalse (bt.isEmpty ());
            
            {
                final ExecutorService es = Executors.newFixedThreadPool (N_THREADS);
                for (int i = 0; i < N_THREADS; i++) {
                    final int threadNo = i;
                    es.execute (new Runnable () {
                        @Override
                        public void run () {
                            for (int j = 0; j < COUNT; j++) {
                                if (j % N_THREADS == threadNo) {
                                    bt.remove (Integer.valueOf (j));
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
            
            
            TestHelper.assertEquals (0, bt.size ());
            TestHelper.assertTrue (bt.isEmpty ());
            
            {
                final ExecutorService es = Executors.newFixedThreadPool (N_THREADS);
                for (int i = 0; i < N_THREADS; i++) {
                    final int threadNo = i;
                    es.execute (new Runnable () {
                        @Override
                        public void run () {
                            for (int j = 0; j < COUNT; j++) {
                                if (j % N_THREADS == threadNo) {
                                    try {
                                        bt.put (Integer.valueOf (j), Integer.valueOf (j));
                                        if (!bt.containsKey (Integer.valueOf (j))) {
                                            System.out.println (j);
                                        }
                                        bt.remove (Integer.valueOf (j));
                                        if (bt.containsKey (Integer.valueOf (j))) {
                                            System.out.println (-j);
                                        }
                                    } catch (Throwable t) {
                                        t.printStackTrace ();
                                    }
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
            
            TestHelper.assertEquals (0, bt.size ());
            if (!bt.isEmpty ()) {
                System.out.println ();
            }
            TestHelper.assertTrue (bt.isEmpty ());
        }
    }
}
