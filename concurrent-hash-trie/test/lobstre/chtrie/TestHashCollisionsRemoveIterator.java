package lobstre.chtrie;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import junit.framework.Assert;

import org.junit.Test;

public class TestHashCollisionsRemoveIterator {
    @Test
    public void test () {
        final Map<Object, Object> bt = new ConcurrentHashTrieMap<Object, Object> ();
        int count = 50000;
        for (int j = 0; j < count; j++) {
            bt.put (Integer.valueOf (j), Integer.valueOf (j));
        }
        
        final Collection<Object> list = new ArrayList <Object> ();
        for (final Iterator<Map.Entry<Object, Object>> i = bt.entrySet ().iterator (); i.hasNext ();) {
            final Entry<Object, Object> e = i.next ();
            final Object key = e.getKey ();
            list.add (key);
            i.remove ();
        }

        Assert.assertEquals (0, bt.size ());
        Assert.assertTrue (bt.isEmpty ());
    }
}
