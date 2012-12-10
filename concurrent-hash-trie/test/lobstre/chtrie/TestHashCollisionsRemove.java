package lobstre.chtrie;

import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;

public class TestHashCollisionsRemove {
    @Test
    public void test () {
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

        Assert.assertEquals (0, bt.size ());
        Assert.assertTrue (bt.isEmpty ());
    }
}
