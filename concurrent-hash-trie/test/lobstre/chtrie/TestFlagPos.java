package lobstre.chtrie;

public class TestFlagPos {
    public static void main (String[] args) {
        TestHelper.assertEquals (1L, BasicTrie.flagPos (0, 0, 0L, 6).flag);
        TestHelper.assertEquals (0, BasicTrie.flagPos (0, 0, 0L, 6).position);

        TestHelper.assertEquals (2L, BasicTrie.flagPos (1, 0, 0L, 6).flag);
        TestHelper.assertEquals (0, BasicTrie.flagPos (1, 0, 0L, 6).position);

        TestHelper.assertEquals (1L, BasicTrie.flagPos (0, 0, 1L, 6).flag);
        TestHelper.assertEquals (0, BasicTrie.flagPos (0, 0, 1L, 6).position);

        TestHelper.assertEquals (2L, BasicTrie.flagPos (1, 0, 1L, 6).flag);
        TestHelper.assertEquals (1, BasicTrie.flagPos (1, 0, 1L, 6).position);

        TestHelper.assertEquals (4L, BasicTrie.flagPos (2, 0, 1L, 6).flag);
        TestHelper.assertEquals (1, BasicTrie.flagPos (2, 0, 1L, 6).position);

        TestHelper.assertEquals (32L, BasicTrie.flagPos (5, 0, 0L, 6).flag);
        TestHelper.assertEquals (0, BasicTrie.flagPos (5, 0, 0L, 6).position);

        TestHelper.assertEquals (32L, BasicTrie.flagPos (5, 0, 2L, 6).flag);
        TestHelper.assertEquals (1, BasicTrie.flagPos (5, 0, 2L, 6).position);

        TestHelper.assertEquals (32L, BasicTrie.flagPos (5, 0, 2L, 6).flag);
        TestHelper.assertEquals (1, BasicTrie.flagPos (5, 0, 2L, 6).position);

        TestHelper.assertEquals (4L, BasicTrie.flagPos (2, 0, 2L, 6).flag);
        TestHelper.assertEquals (1, BasicTrie.flagPos (2, 0, 2L, 6).position);

        TestHelper.assertEquals (16L, BasicTrie.flagPos (4, 0, 2L, 6).flag);
        TestHelper.assertEquals (1, BasicTrie.flagPos (4, 0, 2L, 6).position);

        TestHelper.assertEquals (2L, BasicTrie.flagPos (64 + 4, 6, 2L, 6).flag);
        TestHelper.assertEquals (0, BasicTrie.flagPos (64 + 4, 6, 2L, 6).position);

        TestHelper.assertEquals (16L, BasicTrie.flagPos (256 + 4, 6, 3L, 6).flag);
        TestHelper.assertEquals (2, BasicTrie.flagPos (256 + 4, 6, 3L, 6).position);

        TestHelper.assertEquals (Long.MIN_VALUE, BasicTrie.flagPos (-1, 6, 63, 6).flag);
        TestHelper.assertEquals (6, BasicTrie.flagPos (-1, 6, 63, 6).position);
    }
}
