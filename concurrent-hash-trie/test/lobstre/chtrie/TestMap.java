package lobstre.chtrie;

import java.util.Map;

public class TestMap {
    public static void main (String[] args) {
        final Map<Object, Object> bt = new ConcurrentHashTrieMap<Object, Object> ();
        TestHelper.assertEquals (null, bt.put ("a", "a"));
        TestHelper.assertEquals (null, bt.put ("b", "b"));
        TestHelper.assertEquals (null, bt.put ("c", "b"));
        TestHelper.assertEquals (null, bt.put ("d", "b"));
        TestHelper.assertEquals (null, bt.put ("e", "b"));
        
        for (final Map.Entry<Object, Object> e : bt.entrySet ()) {
            System.out.println (e.getKey ());
            System.out.println (e.getValue ());
        }
    }
}
