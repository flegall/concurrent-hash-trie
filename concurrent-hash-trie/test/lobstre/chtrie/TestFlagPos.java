package lobstre.chtrie;

import junit.framework.Assert;

import org.junit.Test;

public class TestFlagPos {
    @Test
    public void test () {
        Assert.assertEquals (1L, ConcurrentHashTrieMap.flagPos (0, 0, 0L, 6).flag);
        Assert.assertEquals (0, ConcurrentHashTrieMap.flagPos (0, 0, 0L, 6).position);

        Assert.assertEquals (2L, ConcurrentHashTrieMap.flagPos (1, 0, 0L, 6).flag);
        Assert.assertEquals (0, ConcurrentHashTrieMap.flagPos (1, 0, 0L, 6).position);

        Assert.assertEquals (1L, ConcurrentHashTrieMap.flagPos (0, 0, 1L, 6).flag);
        Assert.assertEquals (0, ConcurrentHashTrieMap.flagPos (0, 0, 1L, 6).position);

        Assert.assertEquals (2L, ConcurrentHashTrieMap.flagPos (1, 0, 1L, 6).flag);
        Assert.assertEquals (1, ConcurrentHashTrieMap.flagPos (1, 0, 1L, 6).position);

        Assert.assertEquals (4L, ConcurrentHashTrieMap.flagPos (2, 0, 1L, 6).flag);
        Assert.assertEquals (1, ConcurrentHashTrieMap.flagPos (2, 0, 1L, 6).position);

        Assert.assertEquals (32L, ConcurrentHashTrieMap.flagPos (5, 0, 0L, 6).flag);
        Assert.assertEquals (0, ConcurrentHashTrieMap.flagPos (5, 0, 0L, 6).position);

        Assert.assertEquals (32L, ConcurrentHashTrieMap.flagPos (5, 0, 2L, 6).flag);
        Assert.assertEquals (1, ConcurrentHashTrieMap.flagPos (5, 0, 2L, 6).position);

        Assert.assertEquals (32L, ConcurrentHashTrieMap.flagPos (5, 0, 2L, 6).flag);
        Assert.assertEquals (1, ConcurrentHashTrieMap.flagPos (5, 0, 2L, 6).position);

        Assert.assertEquals (4L, ConcurrentHashTrieMap.flagPos (2, 0, 2L, 6).flag);
        Assert.assertEquals (1, ConcurrentHashTrieMap.flagPos (2, 0, 2L, 6).position);

        Assert.assertEquals (16L, ConcurrentHashTrieMap.flagPos (4, 0, 2L, 6).flag);
        Assert.assertEquals (1, ConcurrentHashTrieMap.flagPos (4, 0, 2L, 6).position);

        Assert.assertEquals (2L, ConcurrentHashTrieMap.flagPos (64 + 4, 6, 2L, 6).flag);
        Assert.assertEquals (0, ConcurrentHashTrieMap.flagPos (64 + 4, 6, 2L, 6).position);

        Assert.assertEquals (16L, ConcurrentHashTrieMap.flagPos (256 + 4, 6, 3L, 6).flag);
        Assert.assertEquals (2, ConcurrentHashTrieMap.flagPos (256 + 4, 6, 3L, 6).position);

        Assert.assertEquals (Long.MIN_VALUE, ConcurrentHashTrieMap.flagPos (-1, 6, 63, 6).flag);
        Assert.assertEquals (6, ConcurrentHashTrieMap.flagPos (-1, 6, 63, 6).position);
    }
}
