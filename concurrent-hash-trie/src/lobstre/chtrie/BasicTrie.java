package lobstre.chtrie;

import java.util.BitSet;
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
        FlagPos (final int flag, final int position) {
            this.flag = flag;
            this.position = position;
        }
        public final int flag;
        public final int position;
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
    
    static FlagPos flagPos (final int hc, final int level, final long bitmap, final int w) {
        final int bitsRemaining = Math.min (w, 32 - level);
        final int flag = (hc >> level) & ((1 << bitsRemaining) - 1);
        final int highestOneBit = Integer.highestOneBit (flag);
        final int pos;
        if (highestOneBit != 0) {
            pos = Long.bitCount (((long) (highestOneBit - 1)) & bitmap);
        } else {
            pos = 0;
        }
        return new FlagPos (flag, pos);
    }

    static long bitCount (final long v) {
        return Long.bitCount (v);
    }
}
