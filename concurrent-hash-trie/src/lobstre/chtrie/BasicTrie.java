package lobstre.chtrie;

import java.util.concurrent.atomic.AtomicReference;

public class BasicTrie {
    private final int width;
    private final AtomicReference<INode> root;

    public BasicTrie() {
        this.width = 6;
        this.root = new AtomicReference<INode> ();
    }

    public void insert (final Object k, final Object v) {
        final INode r = this.root.get ();
        if (r == null || isNullInode (r)) {
            // Insertion on an empty trie.
            final CNode cn = new CNode (new SNode (k, v, false), this.width);
            final INode nr = new INode (cn);
            if (!this.root.compareAndSet (r, nr)) {
                insert (k, v);
            }
            // Else tries inserting in a populated trie
        } else if (!iinsert (r, k, v, 0, null)) {
            insert (k, v);
        }
    }

    private boolean iinsert (final INode i, final Object k, final Object v, final int level, final INode parent) {
        final MainNode main = i.main.get ();

        if (main instanceof CNode) {
            final CNode cn = (CNode) main;
            final FlagPos flagPos = flagPos (k.hashCode (), level, cn.bitmap, this.width);
            if (0 == (flagPos.flag & cn.bitmap)) {
                final SNode snode = new SNode (k, v, false);
                final ArrayNode[] narr = inserted (cn.array, flagPos.position, snode);
                final CNode ncn = new CNode (narr, flagPos.flag | cn.bitmap);
                return i.main.compareAndSet (main, ncn);
            }
            final ArrayNode an = cn.array [flagPos.position];
            if (an instanceof INode) {
                final INode in = (INode) an;
                return iinsert (in, k, v, level + this.width, i);
            }
            if (an instanceof SNode && !((SNode) an).tomb) {
                final SNode sn = (SNode) an;
                final SNode nsn = new SNode (k, v, false);
                if (sn.key.equals (k)) {
                    final CNode ncn = cn.updated (flagPos.position, nsn);
                    return i.main.compareAndSet (main, ncn);
                } else {
                    final CNode scn = new CNode (sn, nsn, level + this.width, this.width);
                    final INode nin = new INode (scn);
                    final ArrayNode[] narr = updated (cn.array, flagPos.position, nin);
                    final CNode ncn = new CNode (narr, cn.bitmap);
                    return i.main.compareAndSet (main, ncn);
                }
            }
        }
        if (main instanceof SNode || main == null) {
            if (parent != null) {
                clean (parent);
            }
            return false;
        }
        return false;
    }

    private void clean (final INode parent) {
        // TODO
    }

    private static ArrayNode[] updated (final ArrayNode[] array, final int position, final ArrayNode snode) {
        final ArrayNode[] narr = new ArrayNode[array.length];
        for (int i = 0; i < array.length; i++) {
            if (i == position) {
                narr [i] = snode;
            } else {
                narr [i] = array [i];
            }
        }
        return narr;
    }

    private static ArrayNode[] inserted (final ArrayNode[] array, final int position, final ArrayNode snode) {
        final ArrayNode[] narr = new ArrayNode[array.length + 1];
        for (int i = 0; i < array.length + 1; i++) {
            if (i < position) {
                narr [i] = array [i];
            } else if (i == position) {
                narr [i] = snode;
            } else {
                narr [i] = array [i - 1];
            }
        }
        return narr;
    }

    static boolean isNullInode (final INode r) {
        return r.main.get () == null;
    }

    static FlagPos flagPos (final int hc, final int level, final long bitmap, final int w) {
        final int bitsRemaining = Math.min (w, 32 - level);
        final int subHash = hc >> level & (1 << bitsRemaining) - 1;
        final long flag = 1L << subHash;
        final int pos = Long.bitCount (flag - 1 & bitmap);
        return new FlagPos (flag, pos);
    }

    static interface MainNode {
    }

    static interface ArrayNode {
    }

    static class INode implements ArrayNode {
        public INode(final MainNode n) {
            this.main = new AtomicReference<MainNode> (n);
        }

        public final AtomicReference<MainNode> main;
    }

    static class CNode implements MainNode {
        CNode(final SNode sNode, final int width) {
            final FlagPos flagPos = BasicTrie.flagPos (sNode.key.hashCode (), 0, 0L, width);
            this.array = new ArrayNode[] { sNode };
            this.bitmap = flagPos.flag;
        }

        CNode(final SNode sn1, final SNode sn2, final int level, final int width) {
            final FlagPos fp1 = BasicTrie.flagPos (sn1.key.hashCode (), level, 0L, width);
            final FlagPos fp2 = BasicTrie.flagPos (sn2.key.hashCode (), level, 0L, width);
            if (fp1.flag < fp2.flag) {
                this.array = new ArrayNode[] { sn1, sn2 };
            } else {
                this.array = new ArrayNode[] { sn2, sn1 };
            }
            this.bitmap = fp1.flag | fp2.flag;
        }

        public CNode updated (final int position, final SNode nsn) {
            final ArrayNode[] narr = BasicTrie.updated (this.array, position, nsn);
            return new CNode (narr, this.bitmap);
        }

        CNode(final ArrayNode[] array, final long bitmap) {
            this.array = array;
            this.bitmap = bitmap;
        }

        public final long bitmap;
        public final ArrayNode[] array;
    }

    static class SNode implements MainNode, ArrayNode {
        SNode(final Object k, final Object v, final boolean tomb) {
            this.key = k;
            this.value = v;
            this.tomb = tomb;
        }

        public final Object key;
        public final Object value;
        public final boolean tomb;
    }

    static class FlagPos {
        FlagPos(final long flag, final int position) {
            this.flag = flag;
            this.position = position;
        }

        public final long flag;
        public final int position;
    }
}
