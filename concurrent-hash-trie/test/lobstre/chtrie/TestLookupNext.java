package lobstre.chtrie;

import lobstre.chtrie.ConcurrentHashTrieMap.SNode;


public class TestLookupNext {
    public static void main (final String[] args) {
        final ConcurrentHashTrieMap<Object, Object> bt = new ConcurrentHashTrieMap<Object, Object> ();
        TestHelper.assertTrue (null == bt.lookupNext (null));
        for (int i = 0; i < 5000; i++) {
            bt.put (Integer.valueOf (-i), Integer.valueOf (-i));
            TestHelper.assertTrue (null != bt.lookupNext (null));
            SNode<Object, Object> kvn = null;
            int j = -1;
            while (null != (kvn = bt.lookupNext (kvn))) {
                j++;
            }
            TestHelper.assertEquals (i, j);
        }
    }
}
