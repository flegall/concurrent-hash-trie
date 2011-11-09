package lobstre.chtrie;

public class TestBasicTrie {
    public static void main (String[] args) {
        assertEquals (1L, BasicTrie.flagPos (0, 0, 0L, 6).flag);
        assertEquals (0, BasicTrie.flagPos (0, 0, 0L, 6).position);

        assertEquals (2L, BasicTrie.flagPos (1, 0, 0L, 6).flag);
        assertEquals (0, BasicTrie.flagPos (1, 0, 0L, 6).position);

        assertEquals (1L, BasicTrie.flagPos (0, 0, 1L, 6).flag);
        assertEquals (0, BasicTrie.flagPos (0, 0, 1L, 6).position);

        assertEquals (2L, BasicTrie.flagPos (1, 0, 1L, 6).flag);
        assertEquals (1, BasicTrie.flagPos (1, 0, 1L, 6).position);

        assertEquals (4L, BasicTrie.flagPos (2, 0, 1L, 6).flag);
        assertEquals (1, BasicTrie.flagPos (2, 0, 1L, 6).position);

        assertEquals (32L, BasicTrie.flagPos (5, 0, 0L, 6).flag);
        assertEquals (0, BasicTrie.flagPos (5, 0, 0L, 6).position);

        assertEquals (32L, BasicTrie.flagPos (5, 0, 2L, 6).flag);
        assertEquals (1, BasicTrie.flagPos (5, 0, 2L, 6).position);

        assertEquals (32L, BasicTrie.flagPos (5, 0, 2L, 6).flag);
        assertEquals (1, BasicTrie.flagPos (5, 0, 2L, 6).position);

        assertEquals (4L, BasicTrie.flagPos (2, 0, 2L, 6).flag);
        assertEquals (1, BasicTrie.flagPos (2, 0, 2L, 6).position);

        assertEquals (16L, BasicTrie.flagPos (4, 0, 2L, 6).flag);
        assertEquals (1, BasicTrie.flagPos (4, 0, 2L, 6).position);

        assertEquals (2L, BasicTrie.flagPos (64 + 4, 6, 2L, 6).flag);
        assertEquals (0, BasicTrie.flagPos (64 + 4, 6, 2L, 6).position);

        assertEquals (16L, BasicTrie.flagPos (256 + 4, 6, 3L, 6).flag);
        assertEquals (2, BasicTrie.flagPos (256 + 4, 6, 3L, 6).position);

        assertEquals (Long.MIN_VALUE, BasicTrie.flagPos (-1, 6, 63, 6).flag);
        assertEquals (6, BasicTrie.flagPos (-1, 6, 63, 6).position);
    }

    private static void assertEquals (long expected, long found) {
        if (expected != found) {
            new Exception ("Error : expected: " + expected + " found: " + found).printStackTrace ();
        }
    }

    private static void assertEquals (int expected, int found) {
        if (expected != found) {
            new Exception ("Error : expected: " + expected + " found: " + found).printStackTrace ();
        }
    }
}
