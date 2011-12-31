package lobstre.chtrie;

import java.util.concurrent.ConcurrentMap;

public class TestConcurrentMapReplace {
    private static final int COUNT = 50*1000;

    public static void main (String[] args) {
        final ConcurrentMap<Object, Object> map = new ConcurrentHashTrieMap<Object, Object> ();
        
        for (int i = 0; i < COUNT; i++) {
            TestHelper.assertTrue (null == map.replace (i, "lol"));
            TestHelper.assertFalse (map.replace (i, i, "lol2"));
            TestHelper.assertTrue (null == map.put (i, i));
            TestHelper.assertTrue (Integer.valueOf (i).equals (map.replace (i, "lol")));
            TestHelper.assertFalse (map.replace (i, i, "lol2"));
            TestHelper.assertTrue (map.replace (i, "lol", i));
        }
    }
}
