package lobstre.chtrie;

import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;

public class TestCNodeInsertionIncorrectOrder {

    @Test
    public void test () {
        final Map<Object, Object> map = new ConcurrentHashTrieMap<Object, Object> ();
        final Integer z3884 = Integer.valueOf (3884);
        final Integer z4266 = Integer.valueOf (4266);
        map.put (z3884, z3884);
        Assert.assertTrue (null != map.get (z3884));
        
        map.put (z4266, z4266);
        Assert.assertTrue (null != map.get (z3884));
        Assert.assertTrue (null != map.get (z4266));
    }
}
