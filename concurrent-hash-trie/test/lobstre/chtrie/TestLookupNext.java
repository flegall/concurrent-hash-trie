package lobstre.chtrie;

import junit.framework.Assert;

import org.junit.Test;

import lobstre.chtrie.ConcurrentHashTrieMap.SNode;


public class TestLookupNext {
    @Test
    public void test () {
        final ConcurrentHashTrieMap<Object, Object> bt = new ConcurrentHashTrieMap<Object, Object> ();
        Assert.assertTrue (null == bt.lookupNext (null));
        for (int i = 0; i < 5000; i++) {
            bt.put (Integer.valueOf (-i), Integer.valueOf (-i));
            Assert.assertTrue (null != bt.lookupNext (null));
            SNode<Object, Object> kvn = null;
            int j = -1;
            while (null != (kvn = bt.lookupNext (kvn))) {
                j++;
            }
            Assert.assertEquals (i, j);
        }
    }
}
