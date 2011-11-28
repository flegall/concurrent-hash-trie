package lobstre.chtrie;

public class TestFlagPos {
    public static void main (String[] args) {
        TestHelper.assertEquals (1L, ConcurrentHashTrieMap.flagPos (0, 0, 0L, 6).flag);
        TestHelper.assertEquals (0, ConcurrentHashTrieMap.flagPos (0, 0, 0L, 6).position);

        TestHelper.assertEquals (2L, ConcurrentHashTrieMap.flagPos (1, 0, 0L, 6).flag);
        TestHelper.assertEquals (0, ConcurrentHashTrieMap.flagPos (1, 0, 0L, 6).position);

        TestHelper.assertEquals (1L, ConcurrentHashTrieMap.flagPos (0, 0, 1L, 6).flag);
        TestHelper.assertEquals (0, ConcurrentHashTrieMap.flagPos (0, 0, 1L, 6).position);

        TestHelper.assertEquals (2L, ConcurrentHashTrieMap.flagPos (1, 0, 1L, 6).flag);
        TestHelper.assertEquals (1, ConcurrentHashTrieMap.flagPos (1, 0, 1L, 6).position);

        TestHelper.assertEquals (4L, ConcurrentHashTrieMap.flagPos (2, 0, 1L, 6).flag);
        TestHelper.assertEquals (1, ConcurrentHashTrieMap.flagPos (2, 0, 1L, 6).position);

        TestHelper.assertEquals (32L, ConcurrentHashTrieMap.flagPos (5, 0, 0L, 6).flag);
        TestHelper.assertEquals (0, ConcurrentHashTrieMap.flagPos (5, 0, 0L, 6).position);

        TestHelper.assertEquals (32L, ConcurrentHashTrieMap.flagPos (5, 0, 2L, 6).flag);
        TestHelper.assertEquals (1, ConcurrentHashTrieMap.flagPos (5, 0, 2L, 6).position);

        TestHelper.assertEquals (32L, ConcurrentHashTrieMap.flagPos (5, 0, 2L, 6).flag);
        TestHelper.assertEquals (1, ConcurrentHashTrieMap.flagPos (5, 0, 2L, 6).position);

        TestHelper.assertEquals (4L, ConcurrentHashTrieMap.flagPos (2, 0, 2L, 6).flag);
        TestHelper.assertEquals (1, ConcurrentHashTrieMap.flagPos (2, 0, 2L, 6).position);

        TestHelper.assertEquals (16L, ConcurrentHashTrieMap.flagPos (4, 0, 2L, 6).flag);
        TestHelper.assertEquals (1, ConcurrentHashTrieMap.flagPos (4, 0, 2L, 6).position);

        TestHelper.assertEquals (2L, ConcurrentHashTrieMap.flagPos (64 + 4, 6, 2L, 6).flag);
        TestHelper.assertEquals (0, ConcurrentHashTrieMap.flagPos (64 + 4, 6, 2L, 6).position);

        TestHelper.assertEquals (16L, ConcurrentHashTrieMap.flagPos (256 + 4, 6, 3L, 6).flag);
        TestHelper.assertEquals (2, ConcurrentHashTrieMap.flagPos (256 + 4, 6, 3L, 6).position);

        TestHelper.assertEquals (Long.MIN_VALUE, ConcurrentHashTrieMap.flagPos (-1, 6, 63, 6).flag);
        TestHelper.assertEquals (6, ConcurrentHashTrieMap.flagPos (-1, 6, 63, 6).position);
    }
}
