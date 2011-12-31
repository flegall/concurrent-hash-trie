package lobstre.chtrie;

import java.util.concurrent.ConcurrentMap;

public class TestConcurrentMapRemove {
    private static final int COUNT = 50*1000;

    public static void main (String[] args) {
        final ConcurrentMap<Object, Object> map = new ConcurrentHashTrieMap<Object, Object> ();
        
        for (int i = 0; i < COUNT; i++) {
            TestHelper.assertFalse (map.remove (i, i));
            TestHelper.assertTrue (null == map.put (i, i));
            TestHelper.assertFalse (map.remove (i, "lol"));
            TestHelper.assertTrue (map.containsKey (i));
            TestHelper.assertTrue (map.remove (i, i));
            TestHelper.assertFalse (map.containsKey (i));
            TestHelper.assertTrue (null == map.put (i, i));
        }
    }
}
