package lobstre.chtrie;

import junit.framework.Assert;

import org.junit.Test;

public class TestInsert {
    @Test
    public void test () {
        final ConcurrentHashTrieMap<Object, Object> bt = new ConcurrentHashTrieMap<Object, Object> ();
        Assert.assertEquals (null, bt.insert ("a", "a", ConcurrentHashTrieMap.noConstraint ()));
        Assert.assertEquals (null, bt.insert ("b", "b", ConcurrentHashTrieMap.noConstraint ()));
        Assert.assertEquals (null, bt.insert ("c", "b", ConcurrentHashTrieMap.noConstraint ()));
        Assert.assertEquals (null, bt.insert ("d", "b", ConcurrentHashTrieMap.noConstraint ()));
        Assert.assertEquals (null, bt.insert ("e", "b", ConcurrentHashTrieMap.noConstraint ()));

        for (int i = 0; i < 10000; i++) {
            Assert.assertEquals (null, bt.insert (Integer.valueOf (i), Integer.valueOf (i), ConcurrentHashTrieMap.noConstraint ()));
            final Object lookup = bt.lookup (Integer.valueOf (i));
            Assert.assertEquals (Integer.valueOf (i), lookup);
        }

        bt.toString ();
    }
}
