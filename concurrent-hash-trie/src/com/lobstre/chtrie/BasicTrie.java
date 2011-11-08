package com.lobstre.chtrie;

import java.util.concurrent.atomic.AtomicReference;

public class BasicTrie {
    static interface MainNode {
    }
    
    static interface ArrayNode {
    }
    
    static class RootNode {
        private final AtomicReference<INode> root = new AtomicReference<INode> ();
    }
    
    static class INode implements ArrayNode {
        public INode (final MainNode n) {
            main = new AtomicReference<MainNode> (n);
        }

        private final AtomicReference<MainNode> main;
    }
    
    static class CNode implements MainNode {
        CNode (final SNode sNode) {
        }
        private long bmp;
        private ArrayNode[] array;
    }
    
    static class SNode implements MainNode, ArrayNode {
        SNode (final Object k, final Object v, final boolean tomb) {
            this.k = k;
            this.v = v;
            this.tomb = tomb;
        }
        private final Object k;
        private final Object v;
        private final boolean tomb;
    }
    
    static class FlagPos {
        FlagPos (final long flag, final int position) {
            this.flag = flag;
            this.position = position;
        }
        private final long flag;
        private final int position;
    }
    
    public static void insert (final RootNode root, final Object k, final Object v) {
        final INode r = root.root.get ();
        if (r == null || isNullInode (r)) {
            final CNode cn = new CNode (new SNode (k, v, false));
            final INode nr = new INode (cn);
            if (!root.root.compareAndSet (r, nr)) {
                insert (root, k, v);
            }
        }
    }

    static boolean isNullInode (final INode r) {
        return r.main.get () != null;
    }
    
    static FlagPos flagPos (int hc, int level, long bitmap) {
        final long flag = (hc >> level) & ((1 << (level)) - 1);
        final int pos = (int) (bitCount (flag - 1) & bitmap);
        return new FlagPos (flag, pos);
    }

    static long bitCount (long v) {
        return Long.bitCount (v);
    }
}
