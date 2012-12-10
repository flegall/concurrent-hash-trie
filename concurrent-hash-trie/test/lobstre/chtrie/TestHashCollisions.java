package lobstre.chtrie;

import junit.framework.Assert;

import org.junit.Test;

public class TestHashCollisions {
    @Test
    public void test () {
        final ConcurrentHashTrieMap<Object, Object> bt = new ConcurrentHashTrieMap<Object, Object> ();

        insertStrings (bt);
        insertChars (bt);
        insertInts (bt);
        insertBytes (bt);

        removeStrings (bt);
        removeChars (bt);
        removeInts (bt);
        removeBytes (bt);

        insertStrings (bt);
        insertInts (bt);
        insertBytes (bt);
        insertChars (bt);

        removeBytes (bt);
        removeStrings (bt);
        removeChars (bt);
        removeInts (bt);

        insertStrings (bt);
        insertInts (bt);
        insertBytes (bt);
        insertChars (bt);

        removeStrings (bt);
        removeChars (bt);
        removeInts (bt);
        removeBytes (bt);

        insertStrings (bt);
        insertInts (bt);
        insertBytes (bt);
        insertChars (bt);

        removeChars (bt);
        removeInts (bt);
        removeBytes (bt);
        removeStrings (bt);

        insertStrings (bt);
        insertInts (bt);
        insertBytes (bt);
        insertChars (bt);

        removeInts (bt);
        removeBytes (bt);
        removeStrings (bt);
        removeChars (bt);

        System.out.println (bt);
    }

    private static void insertChars (final ConcurrentHashTrieMap<Object, Object> bt) {
        Assert.assertEquals (null, bt.insert ('a', 'a', ConcurrentHashTrieMap.noConstraint ()));
        Assert.assertEquals (null, bt.insert ('b', 'b', ConcurrentHashTrieMap.noConstraint ()));
        Assert.assertEquals (null, bt.insert ('c', 'c', ConcurrentHashTrieMap.noConstraint ()));
        Assert.assertEquals (null, bt.insert ('d', 'd', ConcurrentHashTrieMap.noConstraint ()));
        Assert.assertEquals (null, bt.insert ('e', 'e', ConcurrentHashTrieMap.noConstraint ()));

        Assert.assertEquals ((Object) 'a', bt.insert ('a', 'a', ConcurrentHashTrieMap.noConstraint ()));
        Assert.assertEquals ((Object) 'b', bt.insert ('b', 'b', ConcurrentHashTrieMap.noConstraint ()));
        Assert.assertEquals ((Object) 'c', bt.insert ('c', 'c', ConcurrentHashTrieMap.noConstraint ()));
        Assert.assertEquals ((Object) 'd', bt.insert ('d', 'd', ConcurrentHashTrieMap.noConstraint ()));
        Assert.assertEquals ((Object) 'e', bt.insert ('e', 'e', ConcurrentHashTrieMap.noConstraint ()));
    }

    private static void insertStrings (final ConcurrentHashTrieMap<Object, Object> bt) {
        Assert.assertEquals (null, bt.insert ("a", "a", ConcurrentHashTrieMap.noConstraint ()));
        Assert.assertEquals (null, bt.insert ("b", "b", ConcurrentHashTrieMap.noConstraint ()));
        Assert.assertEquals (null, bt.insert ("c", "c", ConcurrentHashTrieMap.noConstraint ()));
        Assert.assertEquals (null, bt.insert ("d", "d", ConcurrentHashTrieMap.noConstraint ()));
        Assert.assertEquals (null, bt.insert ("e", "e", ConcurrentHashTrieMap.noConstraint ()));

        Assert.assertEquals ("a", bt.insert ("a", "a", ConcurrentHashTrieMap.noConstraint ()));
        Assert.assertEquals ("b", bt.insert ("b", "b", ConcurrentHashTrieMap.noConstraint ()));
        Assert.assertEquals ("c", bt.insert ("c", "c", ConcurrentHashTrieMap.noConstraint ()));
        Assert.assertEquals ("d", bt.insert ("d", "d", ConcurrentHashTrieMap.noConstraint ()));
        Assert.assertEquals ("e", bt.insert ("e", "e", ConcurrentHashTrieMap.noConstraint ()));
    }

    private static void insertBytes (final ConcurrentHashTrieMap<Object, Object> bt) {
        for (byte i = 0; i < 128 && i >= 0; i++) {
            final Byte bigB = Byte.valueOf (i);
            Assert.assertEquals (null, bt.insert (bigB, bigB, ConcurrentHashTrieMap.noConstraint ()));
            Assert.assertEquals (bigB, bt.insert (bigB, bigB, ConcurrentHashTrieMap.noConstraint ()));
        }
    }

    private static void insertInts (final ConcurrentHashTrieMap<Object, Object> bt) {
        for (int i = 0; i < 128; i++) {
            final Integer bigI = Integer.valueOf (i);
            Assert.assertEquals (null, bt.insert (bigI, bigI, ConcurrentHashTrieMap.noConstraint ()));
            Assert.assertEquals (bigI, bt.insert (bigI, bigI, ConcurrentHashTrieMap.noConstraint ()));
        }
    }

    private static void removeChars (final ConcurrentHashTrieMap<Object, Object> bt) {
        Assert.assertTrue (null != bt.lookup ('a'));
        Assert.assertTrue (null != bt.lookup ('b'));
        Assert.assertTrue (null != bt.lookup ('c'));
        Assert.assertTrue (null != bt.lookup ('d'));
        Assert.assertTrue (null != bt.lookup ('e'));

        Assert.assertTrue (null != bt.delete ('a', ConcurrentHashTrieMap.noConstraint ()));
        Assert.assertTrue (null != bt.delete ('b', ConcurrentHashTrieMap.noConstraint ()));
        Assert.assertTrue (null != bt.delete ('c', ConcurrentHashTrieMap.noConstraint ()));
        Assert.assertTrue (null != bt.delete ('d', ConcurrentHashTrieMap.noConstraint ()));
        Assert.assertTrue (null != bt.delete ('e', ConcurrentHashTrieMap.noConstraint ()));

        Assert.assertFalse (null != bt.delete ('a', ConcurrentHashTrieMap.noConstraint ()));
        Assert.assertFalse (null != bt.delete ('b', ConcurrentHashTrieMap.noConstraint ()));
        Assert.assertFalse (null != bt.delete ('c', ConcurrentHashTrieMap.noConstraint ()));
        Assert.assertFalse (null != bt.delete ('d', ConcurrentHashTrieMap.noConstraint ()));
        Assert.assertFalse (null != bt.delete ('e', ConcurrentHashTrieMap.noConstraint ()));

        Assert.assertTrue (null == bt.lookup ('a'));
        Assert.assertTrue (null == bt.lookup ('b'));
        Assert.assertTrue (null == bt.lookup ('c'));
        Assert.assertTrue (null == bt.lookup ('d'));
        Assert.assertTrue (null == bt.lookup ('e'));
    }

    private static void removeStrings (final ConcurrentHashTrieMap<Object, Object> bt) {
        Assert.assertTrue (null != bt.lookup ("a"));
        Assert.assertTrue (null != bt.lookup ("b"));
        Assert.assertTrue (null != bt.lookup ("c"));
        Assert.assertTrue (null != bt.lookup ("d"));
        Assert.assertTrue (null != bt.lookup ("e"));

        Assert.assertTrue (null != bt.delete ("a", ConcurrentHashTrieMap.noConstraint ()));
        Assert.assertTrue (null != bt.delete ("b", ConcurrentHashTrieMap.noConstraint ()));
        Assert.assertTrue (null != bt.delete ("c", ConcurrentHashTrieMap.noConstraint ()));
        Assert.assertTrue (null != bt.delete ("d", ConcurrentHashTrieMap.noConstraint ()));
        Assert.assertTrue (null != bt.delete ("e", ConcurrentHashTrieMap.noConstraint ()));

        Assert.assertFalse (null != bt.delete ("a", ConcurrentHashTrieMap.noConstraint ()));
        Assert.assertFalse (null != bt.delete ("b", ConcurrentHashTrieMap.noConstraint ()));
        Assert.assertFalse (null != bt.delete ("c", ConcurrentHashTrieMap.noConstraint ()));
        Assert.assertFalse (null != bt.delete ("d", ConcurrentHashTrieMap.noConstraint ()));
        Assert.assertFalse (null != bt.delete ("e", ConcurrentHashTrieMap.noConstraint ()));

        Assert.assertTrue (null == bt.lookup ("a"));
        Assert.assertTrue (null == bt.lookup ("b"));
        Assert.assertTrue (null == bt.lookup ("c"));
        Assert.assertTrue (null == bt.lookup ("d"));
        Assert.assertTrue (null == bt.lookup ("e"));
    }

    private static void removeInts (final ConcurrentHashTrieMap<Object, Object> bt) {
        for (int i = 0; i < 128; i++) {
            final Integer bigI = Integer.valueOf (i);
            Assert.assertTrue (null != bt.lookup (bigI));
            Assert.assertTrue (null != bt.delete (bigI, ConcurrentHashTrieMap.noConstraint ()));
            Assert.assertFalse (null != bt.delete (bigI, ConcurrentHashTrieMap.noConstraint ()));
            Assert.assertTrue (null == bt.lookup (bigI));
        }
    }

    private static void removeBytes (final ConcurrentHashTrieMap<Object, Object> bt) {
        for (byte i = 0; i < 128 && i >= 0; i++) {
            final Byte bigB = Byte.valueOf (i);
            Assert.assertTrue (null != bt.lookup (bigB));
            Assert.assertTrue (null != bt.delete (bigB, ConcurrentHashTrieMap.noConstraint ()));
            Assert.assertFalse (null != bt.delete (bigB, ConcurrentHashTrieMap.noConstraint ()));
            Assert.assertTrue (null == bt.lookup (bigB));
        }
    }
}
