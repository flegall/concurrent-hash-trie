package lobstre.chtrie;

public class TestHashCollisions {
    public static void main (final String[] args) {
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
        TestHelper.assertEquals (null, bt.insert ('a', 'a', ConcurrentHashTrieMap.noConstraint ()));
        TestHelper.assertEquals (null, bt.insert ('b', 'b', ConcurrentHashTrieMap.noConstraint ()));
        TestHelper.assertEquals (null, bt.insert ('c', 'c', ConcurrentHashTrieMap.noConstraint ()));
        TestHelper.assertEquals (null, bt.insert ('d', 'd', ConcurrentHashTrieMap.noConstraint ()));
        TestHelper.assertEquals (null, bt.insert ('e', 'e', ConcurrentHashTrieMap.noConstraint ()));

        TestHelper.assertEquals ('a', bt.insert ('a', 'a', ConcurrentHashTrieMap.noConstraint ()));
        TestHelper.assertEquals ('b', bt.insert ('b', 'b', ConcurrentHashTrieMap.noConstraint ()));
        TestHelper.assertEquals ('c', bt.insert ('c', 'c', ConcurrentHashTrieMap.noConstraint ()));
        TestHelper.assertEquals ('d', bt.insert ('d', 'd', ConcurrentHashTrieMap.noConstraint ()));
        TestHelper.assertEquals ('e', bt.insert ('e', 'e', ConcurrentHashTrieMap.noConstraint ()));
    }

    private static void insertStrings (final ConcurrentHashTrieMap<Object, Object> bt) {
        TestHelper.assertEquals (null, bt.insert ("a", "a", ConcurrentHashTrieMap.noConstraint ()));
        TestHelper.assertEquals (null, bt.insert ("b", "b", ConcurrentHashTrieMap.noConstraint ()));
        TestHelper.assertEquals (null, bt.insert ("c", "c", ConcurrentHashTrieMap.noConstraint ()));
        TestHelper.assertEquals (null, bt.insert ("d", "d", ConcurrentHashTrieMap.noConstraint ()));
        TestHelper.assertEquals (null, bt.insert ("e", "e", ConcurrentHashTrieMap.noConstraint ()));

        TestHelper.assertEquals ("a", bt.insert ("a", "a", ConcurrentHashTrieMap.noConstraint ()));
        TestHelper.assertEquals ("b", bt.insert ("b", "b", ConcurrentHashTrieMap.noConstraint ()));
        TestHelper.assertEquals ("c", bt.insert ("c", "c", ConcurrentHashTrieMap.noConstraint ()));
        TestHelper.assertEquals ("d", bt.insert ("d", "d", ConcurrentHashTrieMap.noConstraint ()));
        TestHelper.assertEquals ("e", bt.insert ("e", "e", ConcurrentHashTrieMap.noConstraint ()));
    }

    private static void insertBytes (final ConcurrentHashTrieMap<Object, Object> bt) {
        for (byte i = 0; i < 128 && i >= 0; i++) {
            final Byte bigB = Byte.valueOf (i);
            TestHelper.assertEquals (null, bt.insert (bigB, bigB, ConcurrentHashTrieMap.noConstraint ()));
            TestHelper.assertEquals (bigB, bt.insert (bigB, bigB, ConcurrentHashTrieMap.noConstraint ()));
        }
    }

    private static void insertInts (final ConcurrentHashTrieMap<Object, Object> bt) {
        for (int i = 0; i < 128; i++) {
            final Integer bigI = Integer.valueOf (i);
            TestHelper.assertEquals (null, bt.insert (bigI, bigI, ConcurrentHashTrieMap.noConstraint ()));
            TestHelper.assertEquals (bigI, bt.insert (bigI, bigI, ConcurrentHashTrieMap.noConstraint ()));
        }
    }

    private static void removeChars (final ConcurrentHashTrieMap<Object, Object> bt) {
        TestHelper.assertTrue (null != bt.lookup ('a'));
        TestHelper.assertTrue (null != bt.lookup ('b'));
        TestHelper.assertTrue (null != bt.lookup ('c'));
        TestHelper.assertTrue (null != bt.lookup ('d'));
        TestHelper.assertTrue (null != bt.lookup ('e'));

        TestHelper.assertTrue (null != bt.delete ('a'));
        TestHelper.assertTrue (null != bt.delete ('b'));
        TestHelper.assertTrue (null != bt.delete ('c'));
        TestHelper.assertTrue (null != bt.delete ('d'));
        TestHelper.assertTrue (null != bt.delete ('e'));

        TestHelper.assertFalse (null != bt.delete ('a'));
        TestHelper.assertFalse (null != bt.delete ('b'));
        TestHelper.assertFalse (null != bt.delete ('c'));
        TestHelper.assertFalse (null != bt.delete ('d'));
        TestHelper.assertFalse (null != bt.delete ('e'));

        TestHelper.assertTrue (null == bt.lookup ('a'));
        TestHelper.assertTrue (null == bt.lookup ('b'));
        TestHelper.assertTrue (null == bt.lookup ('c'));
        TestHelper.assertTrue (null == bt.lookup ('d'));
        TestHelper.assertTrue (null == bt.lookup ('e'));
    }

    private static void removeStrings (final ConcurrentHashTrieMap<Object, Object> bt) {
        TestHelper.assertTrue (null != bt.lookup ("a"));
        TestHelper.assertTrue (null != bt.lookup ("b"));
        TestHelper.assertTrue (null != bt.lookup ("c"));
        TestHelper.assertTrue (null != bt.lookup ("d"));
        TestHelper.assertTrue (null != bt.lookup ("e"));

        TestHelper.assertTrue (null != bt.delete ("a"));
        TestHelper.assertTrue (null != bt.delete ("b"));
        TestHelper.assertTrue (null != bt.delete ("c"));
        TestHelper.assertTrue (null != bt.delete ("d"));
        TestHelper.assertTrue (null != bt.delete ("e"));

        TestHelper.assertFalse (null != bt.delete ("a"));
        TestHelper.assertFalse (null != bt.delete ("b"));
        TestHelper.assertFalse (null != bt.delete ("c"));
        TestHelper.assertFalse (null != bt.delete ("d"));
        TestHelper.assertFalse (null != bt.delete ("e"));

        TestHelper.assertTrue (null == bt.lookup ("a"));
        TestHelper.assertTrue (null == bt.lookup ("b"));
        TestHelper.assertTrue (null == bt.lookup ("c"));
        TestHelper.assertTrue (null == bt.lookup ("d"));
        TestHelper.assertTrue (null == bt.lookup ("e"));
    }

    private static void removeInts (final ConcurrentHashTrieMap<Object, Object> bt) {
        for (int i = 0; i < 128; i++) {
            final Integer bigI = Integer.valueOf (i);
            TestHelper.assertTrue (null != bt.lookup (bigI));
            TestHelper.assertTrue (null != bt.delete (bigI));
            TestHelper.assertFalse (null != bt.delete (bigI));
            TestHelper.assertTrue (null == bt.lookup (bigI));
        }
    }

    private static void removeBytes (final ConcurrentHashTrieMap<Object, Object> bt) {
        for (byte i = 0; i < 128 && i >= 0; i++) {
            final Byte bigB = Byte.valueOf (i);
            TestHelper.assertTrue (null != bt.lookup (bigB));
            TestHelper.assertTrue (null != bt.delete (bigB));
            TestHelper.assertFalse (null != bt.delete (bigB));
            TestHelper.assertTrue (null == bt.lookup (bigB));
        }
    }
}
