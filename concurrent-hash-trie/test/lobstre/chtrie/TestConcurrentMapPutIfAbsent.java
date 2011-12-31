package lobstre.chtrie;

import java.util.concurrent.ConcurrentMap;

public class TestConcurrentMapPutIfAbsent {
    private static final int COUNT = 50*1000;

    public static void main (String[] args) {
        final ConcurrentMap<Object, Object> map = new ConcurrentHashTrieMap<Object, Object> ();
        
        for (int i = 0; i < COUNT; i++) {
            TestHelper.assertTrue (null == map.putIfAbsent (i, i));
            TestHelper.assertTrue (Integer.valueOf (i).equals (map.putIfAbsent (i, i)));
        }
    }
}
