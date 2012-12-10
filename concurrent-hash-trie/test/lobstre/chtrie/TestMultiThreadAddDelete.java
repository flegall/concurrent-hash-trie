package lobstre.chtrie;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import junit.framework.Assert;

import org.junit.Test;

public class TestMultiThreadAddDelete {
    private static final int RETRIES = 1;
    private static final int N_THREADS = 7;
    private static final int COUNT =  50 * 1000;

    @Test
    public void test () {
        for (int j = 0; j < RETRIES; j++) {
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
            
            Assert.assertEquals (COUNT, bt.size ());
            Assert.assertFalse (bt.isEmpty ());
            
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
            
            
            Assert.assertEquals (0, bt.size ());
            Assert.assertTrue (bt.isEmpty ());
            
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
            
            Assert.assertEquals (0, bt.size ());
            if (!bt.isEmpty ()) {
                System.out.println ();
            }
            Assert.assertTrue (bt.isEmpty ());
        }
    }
}
