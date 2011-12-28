package lobstre.chtrie;

import java.util.Map;

public class TestHashCollisionsRemove {
    public static void main (final String[] args) {
        final Map<Object, Object> bt = new ConcurrentHashTrieMap<Object, Object> ();
        int count = 50000;
        for (int j = 0; j < count; j++) {
            final Object[] objects = TestMultiThreadMapIterator.getObjects (j);
            for (final Object o : objects) {
                bt.put (o, o);
            }
        }
        
        for (int j = 0; j < count; j++) {
            final Object[] objects = TestMultiThreadMapIterator.getObjects (j);
            for (final Object o : objects) {
                bt.remove (o);
            }
        }

        TestHelper.assertEquals (0, bt.size ());
        TestHelper.assertTrue (bt.isEmpty ());
    }
}
