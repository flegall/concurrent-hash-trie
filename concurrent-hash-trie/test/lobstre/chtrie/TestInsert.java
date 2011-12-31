package lobstre.chtrie;

public class TestInsert {
    public static void main (final String[] args) {
        final ConcurrentHashTrieMap<Object, Object> bt = new ConcurrentHashTrieMap<Object, Object> ();
        TestHelper.assertEquals (null, bt.insert ("a", "a", ConcurrentHashTrieMap.noConstraint ()));
        TestHelper.assertEquals (null, bt.insert ("b", "b", ConcurrentHashTrieMap.noConstraint ()));
        TestHelper.assertEquals (null, bt.insert ("c", "b", ConcurrentHashTrieMap.noConstraint ()));
        TestHelper.assertEquals (null, bt.insert ("d", "b", ConcurrentHashTrieMap.noConstraint ()));
        TestHelper.assertEquals (null, bt.insert ("e", "b", ConcurrentHashTrieMap.noConstraint ()));

        for (int i = 0; i < 10000; i++) {
            TestHelper.assertEquals (null, bt.insert (Integer.valueOf (i), Integer.valueOf (i), ConcurrentHashTrieMap.noConstraint ()));
            final Object lookup = bt.lookup (Integer.valueOf (i));
            TestHelper.assertEquals (Integer.valueOf (i), lookup);
        }

        bt.toString ();
    }
}
