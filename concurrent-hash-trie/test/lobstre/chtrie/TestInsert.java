package lobstre.chtrie;

public class TestInsert {
    public static void main (final String[] args) {
        final BasicTrie bt = new BasicTrie ();
        bt.insert ("a", "a");
        bt.insert ("b", "b");
        bt.insert ("c", "b");
        bt.insert ("d", "b");
        bt.insert ("e", "b");

        for (int i = 0; i < 10000; i++) {
            bt.insert (Integer.valueOf (i), Integer.valueOf (i));
            final Object lookup = bt.lookup (Integer.valueOf (i));
            TestHelper.assertEquals (Integer.valueOf (i), lookup);
        }

        bt.toString ();
    }
}
