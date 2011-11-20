package lobstre.chtrie;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TestMultiThreadAddRemoveSimpler {
    public static void main (final String[] args) {
        int nThreads = 2;
        final ExecutorService es = Executors.newFixedThreadPool (nThreads);
        final BasicTrie bt = new BasicTrie ();
        for (int i = 0; i < nThreads; i++) {
            final int threadNo = i;
            es.execute (new Runnable () {
                @Override
                public void run () {
                    final int j = threadNo == 0 ? 0 : 64;
                    for (int i = 0; i < 500 * 1000; i++) {
                        bt.insert (Integer.valueOf (j), Integer.valueOf (j));
                        bt.remove (Integer.valueOf (j));
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
