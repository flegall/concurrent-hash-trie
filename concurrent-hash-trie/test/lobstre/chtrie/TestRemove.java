package lobstre.chtrie;

public class TestRemove {
    public static void main (final String[] args) {
        final BasicTrie bt = new BasicTrie ();

        for (int i = 0; i < 10000; i++) {
            bt.insert (Integer.valueOf (i), Integer.valueOf (i));
            final Object lookup = bt.lookup (Integer.valueOf (i));
            TestHelper.assertEquals (Integer.valueOf (i), lookup);
        }
        
        checkAddInsert (bt, 536);
        checkAddInsert (bt, 4341);
        checkAddInsert (bt, 8437);
        
        for (int i = 0; i < 10000; i++) {
            boolean removed = bt.remove (Integer.valueOf (i));
            TestHelper.assertEquals (Boolean.TRUE, Boolean.valueOf (removed));
            final Object lookup = bt.lookup (Integer.valueOf (i));
            TestHelper.assertEquals (null, lookup);
        }

        bt.toString ();
    }

    private static void checkAddInsert (final BasicTrie bt, int k) {
        final Integer v = Integer.valueOf (k);
        bt.remove (v);
        Object foundV = bt.lookup (v);
        TestHelper.assertEquals (null, foundV);
        bt.insert (v, v);
        foundV = bt.lookup (v);
        TestHelper.assertEquals (v, foundV);
        
        bt.insert (v, -1);
        bt.insert (v, v);
    }
}
