package lobstre.chtrie;

public class TestRemove {
    public static void main (final String[] args) {
        final BasicTrie bt = new BasicTrie ();

        for (int i = 0; i < 10000; i++) {
            bt.insert (Integer.valueOf (i), Integer.valueOf (i));
            final Object lookup = bt.lookup (Integer.valueOf (i));
            TestHelper.assertEquals (Integer.valueOf (i), lookup);
        }
        
        for (int i = 0; i < 10000; i++) {
            bt.remove (Integer.valueOf (i));
        }

        bt.toString ();
    }
}
