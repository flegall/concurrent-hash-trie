package lobstre.chtrie;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class TestMapIterator {
    public static void main (final String[] args) {
        for (int i = 0; i < 5000; i++) {
            final Map<Integer, Integer> bt = new ConcurrentHashTrieMap<Integer, Integer> ();
            for (int j = 0; j < i; j++) {
                TestHelper.assertEquals (null, bt.put (Integer.valueOf (j), Integer.valueOf (j)));
            }
            int count = 0;
            final Set<Integer> set = new HashSet<Integer> ();
            for (final Map.Entry<Integer, Integer> e : bt.entrySet ()) {
                set.add (e.getKey ());
                count++;
            }
            for (final Integer j : set) {
                TestHelper.assertTrue (bt.containsKey (j));
            }
            for (final Integer j : bt.keySet ()) {
                TestHelper.assertTrue (set.contains (j));
            }

            TestHelper.assertEquals (i, count);
            TestHelper.assertEquals (i, bt.size ());

            for (final Iterator<Integer> iter = bt.keySet ().iterator (); iter.hasNext ();) {
                final Integer k = iter.next ();
                TestHelper.assertTrue (bt.containsKey (k));
                iter.remove ();
                TestHelper.assertFalse (bt.containsKey (k));
            }
        }
    }
}
