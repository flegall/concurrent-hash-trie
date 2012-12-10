package lobstre.chtrie;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import junit.framework.Assert;

import org.junit.Test;

public class TestMultiThreadInserts {
    @Test
    public void test () {
        final int nThreads = 2;
        final ExecutorService es = Executors.newFixedThreadPool (nThreads);
        final ConcurrentHashTrieMap<Object, Object> bt = new ConcurrentHashTrieMap<Object, Object> ();
        for (int i = 0; i < nThreads; i++) {
            final int threadNo = i;
            es.execute (new Runnable () {
                @Override
                public void run () {
                    for (int j = 0; j < 500 * 1000; j++) {
                        if (j % nThreads == threadNo) {
                            bt.insert (Integer.valueOf (j), Integer.valueOf (j), ConcurrentHashTrieMap.noConstraint ());
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
        
        for (int j = 0; j < 500 * 1000; j++) {
            final Object lookup = bt.lookup (Integer.valueOf (j));
            Assert.assertEquals (Integer.valueOf (j), lookup);
        }
    }
}
