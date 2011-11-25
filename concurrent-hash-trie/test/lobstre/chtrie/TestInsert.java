package lobstre.chtrie;

public class TestInsert {
    public static void main (final String[] args) {
        final BasicTrie bt = new BasicTrie ();
        TestHelper.assertEquals (null, bt.insert ("a", "a"));
        TestHelper.assertEquals (null, bt.insert ("b", "b"));
        TestHelper.assertEquals (null, bt.insert ("c", "b"));
        TestHelper.assertEquals (null, bt.insert ("d", "b"));
        TestHelper.assertEquals (null, bt.insert ("e", "b"));

        for (int i = 128; i < 10000; i++) {
            TestHelper.assertEquals (null, bt.insert (Integer.valueOf (i), Integer.valueOf (i)));
            final Object lookup = bt.lookup (Integer.valueOf (i));
            TestHelper.assertEquals (Integer.valueOf (i), lookup);
        }

        bt.toString ();
    }
}
