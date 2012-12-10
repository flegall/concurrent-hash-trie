package lobstre.chtrie;

import java.util.concurrent.ConcurrentMap;

import junit.framework.Assert;

import org.junit.Test;

public class TestConcurrentMapPutIfAbsent {
    private static final int COUNT = 50*1000;

    @Test
    public void test () {
        final ConcurrentMap<Object, Object> map = new ConcurrentHashTrieMap<Object, Object> ();
        
        for (int i = 0; i < COUNT; i++) {
            Assert.assertTrue (null == map.putIfAbsent (i, i));
            Assert.assertTrue (Integer.valueOf (i).equals (map.putIfAbsent (i, i)));
        }
    }
}
