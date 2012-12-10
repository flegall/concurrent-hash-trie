package lobstre.chtrie;

import java.util.concurrent.ConcurrentMap;

import junit.framework.Assert;

import org.junit.Test;

public class TestConcurrentMapRemove {
    private static final int COUNT = 50*1000;

    @Test
    public void test () {
        final ConcurrentMap<Object, Object> map = new ConcurrentHashTrieMap<Object, Object> ();
        
        for (int i = 0; i < COUNT; i++) {
            Assert.assertFalse (map.remove (i, i));
            Assert.assertTrue (null == map.put (i, i));
            Assert.assertFalse (map.remove (i, "lol"));
            Assert.assertTrue (map.containsKey (i));
            Assert.assertTrue (map.remove (i, i));
            Assert.assertFalse (map.containsKey (i));
            Assert.assertTrue (null == map.put (i, i));
        }
    }
}
