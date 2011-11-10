package lobstre.chtrie;

import java.util.concurrent.atomic.AtomicReference;

public class BasicTrie {
    private final int width;
    private final AtomicReference<INode> root;

    public BasicTrie () {
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

    public Object lookup (final Object k) {
        final INode r = this.root.get ();
        if (null == r) {
            return null;
        } else if (isNullInode (r)) {
            this.root.compareAndSet (r, null);
            return lookup (k);
        } else {
            Result res = ilookup (r, k, 0, null);
            switch (res.type) {
            case FOUND:
                return res.result;
            case NOTFOUND:
                return null;
            case RESTART:
                return lookup (k);
            default:
                throw new RuntimeException ("Unexpected case: "+res.type);
            }
        }
    }

    private Result ilookup (INode i, Object k, int level, INode parent) {
        final MainNode main = i.main.get ();
        if (main instanceof CNode) {
            final CNode cn = (CNode) main;
            final FlagPos flagPos = flagPos (k.hashCode (), level, cn.bitmap, this.width);
            if (0 == (flagPos.flag & cn.bitmap)) {
                return new Result (ResultType.NOTFOUND, null);    
            }
            final ArrayNode an = cn.array [flagPos.position];
            if (an instanceof INode) {
                final INode sin = (INode) an;
                return ilookup (sin, k, level + width, i);
            }
            if (an instanceof SNode && !((SNode) an).tomb) {
                final SNode sn = (SNode) an;
                if (sn.key.equals (k)) {
                    return new Result (ResultType.FOUND, sn.value);
                } else {
                    return new Result (ResultType.NOTFOUND, null);    
                }
            }
        }
        if ((main instanceof SNode && ((SNode)main).tomb) || main == null) {
            if (parent != null) {
                clean (parent);
            }
            return new Result (ResultType.RESTART, null);
        }
        throw new RuntimeException ("Found CNODE/SNODE.tomb!");
    }

    private boolean iinsert (final INode i, final Object k, final Object v, final int level, final INode parent) {
        final MainNode main = i.main.get ();

        if (main instanceof CNode) {
            final CNode cn = (CNode) main;
            final FlagPos flagPos = flagPos (k.hashCode (), level, cn.bitmap, this.width);
            if (0 == (flagPos.flag & cn.bitmap)) {
                final SNode snode = new SNode (k, v, false);
                final CNode ncn = cn.inserted (flagPos, snode);
                return i.main.compareAndSet (main, ncn);
            }
            final ArrayNode an = cn.array [flagPos.position];
            if (an instanceof INode) {
                final INode sin = (INode) an;
                return iinsert (sin, k, v, level + this.width, i);
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
        if ((main instanceof SNode && ((SNode)main).tomb) || main == null) {
            if (parent != null) {
                clean (parent);
            }
            return false;
        }
        throw new RuntimeException ("Found CNODE/SNODE.tomb!");
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
        final long flag = flag (hc, level, w);
        final int pos = Long.bitCount (flag - 1 & bitmap);
        return new FlagPos (flag, pos);
    }

    static long flag (final int hc, final int level, final int w) {
        final int bitsRemaining = Math.min (w, 32 - level);
        final int subHash = hc >> level & (1 << bitsRemaining) - 1;
        final long flag = 1L << subHash;
        return flag;
    }

    static interface MainNode {
    }

    static interface ArrayNode {
    }

    static class INode implements ArrayNode {
        public INode (final MainNode n) {
            this.main = new AtomicReference<MainNode> (n);
        }

        public final AtomicReference<MainNode> main;
    }

    static enum ResultType {
        FOUND,
        NOTFOUND,
        RESTART
    }

    static class Result {
        public Result (final ResultType type, final Object result) {
            this.type = type;
            this.result = result;
        }

        public final Object result;
        public final ResultType type;
    }

    static class CNode implements MainNode {
        CNode (final SNode sNode, final int width) {
            final long flag = BasicTrie.flag (sNode.key.hashCode (), 0, width);
            this.array = new ArrayNode[] { sNode };
            this.bitmap = flag;
        }

        CNode (final SNode sn1, final SNode sn2, final int level, final int width) {
            final long flag1 = BasicTrie.flag (sn1.key.hashCode (), level, width);
            final long flag2 = BasicTrie.flag (sn2.key.hashCode (), level, width);
            if (flag1 < flag2) {
                this.array = new ArrayNode[] { sn1, sn2 };
            } else {
                this.array = new ArrayNode[] { sn2, sn1 };
            }
            this.bitmap = flag1 | flag2;
        }

        public CNode inserted (final FlagPos flagPos, final SNode snode) {
            final ArrayNode[] narr = BasicTrie.inserted (this.array, flagPos.position, snode);
            return new CNode (narr, flagPos.flag | this.bitmap);
        }

        public CNode updated (final int position, final SNode nsn) {
            final ArrayNode[] narr = BasicTrie.updated (this.array, position, nsn);
            return new CNode (narr, this.bitmap);
        }

        CNode (final ArrayNode[] array, final long bitmap) {
            this.array = array;
            this.bitmap = bitmap;
        }

        public final long bitmap;
        public final ArrayNode[] array;
    }

    static class SNode implements MainNode, ArrayNode {
        SNode (final Object k, final Object v, final boolean tomb) {
            this.key = k;
            this.value = v;
            this.tomb = tomb;
        }

        public final Object key;
        public final Object value;
        public final boolean tomb;
    }

    static class FlagPos {
        FlagPos (final long flag, final int position) {
            this.flag = flag;
            this.position = position;
        }

        public final long flag;
        public final int position;
    }
}
