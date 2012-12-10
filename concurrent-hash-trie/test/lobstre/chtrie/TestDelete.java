package lobstre.chtrie;

import junit.framework.Assert;

import org.junit.Test;


public class TestDelete {
    @Test
    public void test () {
        final ConcurrentHashTrieMap<Object, Object> bt = new ConcurrentHashTrieMap<Object, Object> ();

        for (int i = 0; i < 10000; i++) {
            Assert.assertEquals (null, bt.insert (Integer.valueOf (i), Integer.valueOf (i), ConcurrentHashTrieMap.noConstraint ()));
            final Object lookup = bt.lookup (Integer.valueOf (i));
            Assert.assertEquals (Integer.valueOf (i), lookup);
        }
        
        checkAddInsert (bt, 536);
        checkAddInsert (bt, 4341);
        checkAddInsert (bt, 8437);
        
        for (int i = 0; i < 10000; i++) {
            boolean removed = null != bt.delete (Integer.valueOf (i), ConcurrentHashTrieMap.noConstraint ());
            Assert.assertEquals (Boolean.TRUE, Boolean.valueOf (removed));
            final Object lookup = bt.lookup (Integer.valueOf (i));
            Assert.assertEquals (null, lookup);
        }

        bt.toString ();
    }

    private static void checkAddInsert (final ConcurrentHashTrieMap<Object, Object> bt, int k) {
        final Integer v = Integer.valueOf (k);
        bt.delete (v, ConcurrentHashTrieMap.noConstraint ());
        Object foundV = bt.lookup (v);
        Assert.assertEquals (null, foundV);
        Assert.assertEquals (null, bt.insert (v, v, ConcurrentHashTrieMap.noConstraint ()));
        foundV = bt.lookup (v);
        Assert.assertEquals (v, foundV);
        
        Assert.assertEquals (v, bt.insert (v, Integer.valueOf (-1), ConcurrentHashTrieMap.noConstraint ()));
        Assert.assertEquals (Integer.valueOf (-1), bt.insert (v, v, ConcurrentHashTrieMap.noConstraint ()));
    }
}
