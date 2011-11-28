package lobstre.chtrie;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TestMultiThreadAddDelete {
    public static void main (final String[] args) {
        final ConcurrentHashTrieMap<Object, Object> bt = new ConcurrentHashTrieMap<Object, Object> ();

        for (int j = 500 * 1000; j < 1000 * 1000; j++) {
            bt.insert (Integer.valueOf (j), Integer.valueOf (j));
            bt.delete (Integer.valueOf (j));
        }
        
        int nThreads = 2;
        final ExecutorService es = Executors.newFixedThreadPool (nThreads);
        for (int i = 0; i < nThreads; i++) {
            es.execute (new Runnable () {
                @Override
                public void run () {
                    for (int j = 0; j < 500 * 1000; j++) {
                        bt.insert (Integer.valueOf (j), Integer.valueOf (j));
                        bt.delete (Integer.valueOf (j));
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
        ;
    }
}
