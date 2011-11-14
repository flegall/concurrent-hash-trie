package lobstre.chtrie;

import java.util.concurrent.atomic.AtomicReference;

public class BasicTrie {
    /**
     * Width in bits
     */
    private final int width;

    /**
     * Root node of the trie
     */
    private final AtomicReference<INode> root;

    /**
     * Builds a {@link BasicTrie} instance
     */
    public BasicTrie () {
        this.width = 6;
        this.root = new AtomicReference<INode> ();
    }

    /**
     * Inserts or updates a key/value mapping.
     * 
     * @param k
     *            a key {@link Object}
     * @param v
     *            a value Object
     */
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

    /**
     * Looks up the value associated to a key
     * 
     * @param k
     *            a key {@link Object}
     * @return the value associated to k
     */
    public Object lookup (final Object k) {
        final INode r = this.root.get ();
        if (null == r) {
            // Empty trie
            return null;
        } else if (isNullInode (r)) {
            // Null Inode trie, fix it and retry
            this.root.compareAndSet (r, null);
            return lookup (k);
        } else {
            // Getting lookup result
            final Result res = ilookup (r, k, 0, null);
            switch (res.type) {
            case FOUND:
                return res.result;
            case NOTFOUND:
                return null;
            case RESTART:
                return lookup (k);
            default:
                throw new RuntimeException ("Unexpected case: " + res.type);
            }
        }
    }

    /**
     * Removes a key/value mapping
     * 
     * @param k
     *            the key Object
     * @return true if removed was performed, false otherwises
     */
    public boolean remove (final Object k) {
        final INode r = this.root.get ();
        if (null == r) {
            // Empty trie
            return false;
        } else if (isNullInode (r)) {
            // Null Inode trie, fix it and retry
            this.root.compareAndSet (r, null);
            return remove (k);
        } else {
            // Getting remove result
            final Result res = iremove (r, k, 0, null);
            switch (res.type) {
            case FOUND:
                return true;
            case NOTFOUND:
                return false;
            case RESTART:
                return remove (k);
            default:
                throw new RuntimeException ("Unexpected case: " + res.type);
            }
        }
    }

    private Result ilookup (final INode i, final Object k, final int level, final INode parent) {
        final MainNode main = i.main.get ();

        // Usual case
        if (main instanceof CNode) {
            final CNode cn = (CNode) main;
            final FlagPos flagPos = flagPos (k.hashCode (), level, cn.bitmap, this.width);

            // Asked for a hash not in trie
            if (0 == (flagPos.flag & cn.bitmap)) {
                return new Result (ResultType.NOTFOUND, null);
            }

            final ArrayNode an = cn.array [flagPos.position];
            if (an instanceof INode) {
                // Looking down
                final INode sin = (INode) an;
                return ilookup (sin, k, level + this.width, i);
            }
            if (an instanceof SNode && !((SNode) an).tomb) {
                // Found the hash locally, let's see if it matches
                final SNode sn = (SNode) an;
                if (sn.key.equals (k)) {
                    return new Result (ResultType.FOUND, sn.value);
                } else {
                    return new Result (ResultType.NOTFOUND, null);
                }
            }
        }

        // Cleaning up trie
        if (main instanceof SNode && ((SNode) main).tomb || main == null) {
            if (parent != null) {
                clean (parent);
            }
            return new Result (ResultType.RESTART, null);
        }
        throw new RuntimeException ("Found CNODE/SNODE.tomb!");
    }

    private boolean iinsert (final INode i, final Object k, final Object v, final int level, final INode parent) {
        final MainNode main = i.main.get ();

        // Usual case
        if (main instanceof CNode) {
            final CNode cn = (CNode) main;
            final FlagPos flagPos = flagPos (k.hashCode (), level, cn.bitmap, this.width);

            // Asked for a hash not in trie, let's insert it
            if (0 == (flagPos.flag & cn.bitmap)) {
                final SNode snode = new SNode (k, v, false);
                final CNode ncn = cn.inserted (flagPos, snode);
                return i.main.compareAndSet (main, ncn);
            }

            final ArrayNode an = cn.array [flagPos.position];
            if (an instanceof INode) {
                // Looking down
                final INode sin = (INode) an;
                return iinsert (sin, k, v, level + this.width, i);
            }
            if (an instanceof SNode && !((SNode) an).tomb) {
                final SNode sn = (SNode) an;
                final SNode nsn = new SNode (k, v, false);
                // Found the hash locally, let's see if it matches
                if (sn.key.equals (k)) {
                    // Updates the key with the new value
                    final CNode ncn = cn.updated (flagPos.position, nsn);
                    return i.main.compareAndSet (main, ncn);
                } else {
                    // Creates a sub-level
                    final CNode scn = new CNode (sn, nsn, level + this.width, this.width);
                    final INode nin = new INode (scn);
                    final ArrayNode[] narr = updated (cn.array, flagPos.position, nin);
                    final CNode ncn = new CNode (narr, cn.bitmap);
                    return i.main.compareAndSet (main, ncn);
                }
            }
        }

        // Cleaning up trie
        if (main instanceof SNode && ((SNode) main).tomb || main == null) {
            if (parent != null) {
                clean (parent);
            }
            return false;
        }
        throw new RuntimeException ("Found CNODE/SNODE.tomb!");
    }

    private Result iremove (final INode i, final Object k, final int level, final INode parent) {
        final MainNode main = i.main.get ();

        // Usual case
        if (main instanceof CNode) {
            final CNode cn = (CNode) main;
            final FlagPos flagPos = flagPos (k.hashCode (), level, cn.bitmap, this.width);

            // Asked for a hash not in trie
            if (0 == (flagPos.flag & cn.bitmap)) {
                return new Result (ResultType.NOTFOUND, null);
            }

            Result res = null;
            final ArrayNode an = cn.array [flagPos.position];
            if (an instanceof INode) {
                // Looking down
                final INode sin = (INode) an;
                res = iremove (sin, k, level + this.width, i);
            }
            if (an instanceof SNode && !((SNode) an).tomb) {
                // Found the hash locally, let's see if it matches
                final SNode sn = (SNode) an;
                if (sn.key.equals (k)) {
                    final CNode ncn = cn.removed (flagPos);
                    if (i.main.compareAndSet (cn, ncn)) {
                        res = new Result (ResultType.FOUND, sn.value);
                    } else {
                        res = new Result (ResultType.RESTART, null);
                    }
                } else {
                    res = new Result (ResultType.NOTFOUND, null);
                }
            }
            if (null == res) {
                throw new RuntimeException ("Found CNODE/SNODE.tomb!");
            }
            if (res.type == ResultType.NOTFOUND || res.type == ResultType.RESTART) {
                return res;
            }
            if (parent != null && tombCompress (i)) {
                contractParent (parent, i, k.hashCode (), level - this.width);
            }
            return res;
        }

        // Cleaning up trie
        if (main instanceof SNode && ((SNode) main).tomb || main == null) {
            if (parent != null) {
                clean (parent);
            }
            return new Result (ResultType.RESTART, null);
        }
        throw new RuntimeException ("Found CNODE/SNODE.tomb!");
    }

    private void contractParent (final INode parent, final INode i, final int hashCode, final int j) {
        // TODO
    }

    private boolean tombCompress (final INode i) {
        final MainNode m = i.main.get ();
        
        // No need to compress is not a CNode
        if (!(m instanceof CNode)) {
            return false;
        }
        
        
        return false;
    }

    private void clean (final INode parent) {
        // TODO
    }

    /**
     * Returns a copy an {@link ArrayNode} array with an updated
     * {@link ArrayNode} value at a certain position.
     * 
     * @param array
     *            the source {@link ArrayNode} array
     * @param position
     *            the position
     * @param n
     *            the updated {@link ArrayNode} value
     * @return an updated copy of the source {@link ArrayNode} array
     */
    static ArrayNode[] updated (final ArrayNode[] array, final int position, final ArrayNode n) {
        final ArrayNode[] narr = new ArrayNode[array.length];
        for (int i = 0; i < array.length; i++) {
            if (i == position) {
                narr [i] = n;
            } else {
                narr [i] = array [i];
            }
        }
        return narr;
    }

    /**
     * Returns a copy an {@link ArrayNode} array with an inserted
     * {@link ArrayNode} value at a certain position.
     * 
     * @param array
     *            the source {@link ArrayNode} array
     * @param position
     *            the position
     * @param n
     *            the inserted {@link ArrayNode} value
     * @return an updated copy of the source {@link ArrayNode} array
     */
    static ArrayNode[] inserted (final ArrayNode[] array, final int position, final ArrayNode n) {
        final ArrayNode[] narr = new ArrayNode[array.length + 1];
        for (int i = 0; i < array.length + 1; i++) {
            if (i < position) {
                narr [i] = array [i];
            } else if (i == position) {
                narr [i] = n;
            } else {
                narr [i] = array [i - 1];
            }
        }
        return narr;
    }

    /**
     * Returns a copy an {@link ArrayNode} array with a removed
     * {@link ArrayNode} value at a certain position.
     * 
     * @param array
     *            the source {@link ArrayNode} array
     * @param position
     *            the position
     * @return an updated copy of the source {@link ArrayNode} array
     */
    static ArrayNode[] removed (final ArrayNode[] array, final int position) {
        final ArrayNode[] narr = new ArrayNode[array.length - 1];
        for (int i = 0; i < array.length; i++) {
            if (i < position) {
                narr [i] = array [i];
            } else if (i > position) {
                narr [i - 1] = array [i];
            }
        }
        return narr;
    }

    /**
     * Returns <code>true</code> if the {@link INode} contains a Null reference
     * 
     * @param i
     *            an {@link INode} instance
     * @return true
     */
    static boolean isNullInode (final INode i) {
        return i.main.get () == null;
    }

    /**
     * Gets the flag value and insert position for an hashcode, level & bitmap.
     * 
     * @param hc
     *            the hashcode value
     * @param level
     *            the level (in bit progression)
     * @param bitmap
     *            the current {@link CNode}'s bitmap.
     * @param w
     *            the fan width (in bits)
     * @return a {@link FlagPos}'s instance for the specified hashcode, level &
     *         bitmap.
     */
    static FlagPos flagPos (final int hc, final int level, final long bitmap, final int w) {
        final long flag = flag (hc, level, w);
        final int pos = Long.bitCount (flag - 1 & bitmap);
        return new FlagPos (flag, pos);
    }

    /**
     * Gets the flag value for an hashcode level.
     * 
     * @param hc
     *            the hashcode value
     * @param level
     *            the level (in bit progression)
     * @param the
     *            fan width (in bits)
     * @return the flag value
     */
    static long flag (final int hc, final int level, final int w) {
        final int bitsRemaining = Math.min (w, 32 - level);
        final int subHash = hc >> level & (1 << bitsRemaining) - 1;
        final long flag = 1L << subHash;
        return flag;
    }

    /**
     * A Marker interface for what can be in an INode (CNode or SNode)
     */
    static interface MainNode {
    }

    /**
     * A Marker interface for what can be in a CNode array. (INode or SNode)
     */
    static interface ArrayNode {
    }

    /**
     * A CAS-able Node which may reference either a CNode or and SNode
     */
    static class INode implements ArrayNode {
        /**
         * Builds an {@link INode} instance
         * 
         * @param n
         *            a {@link MainNode}
         */
        public INode (final MainNode n) {
            this.main = new AtomicReference<MainNode> (n);
        }

        public final AtomicReference<MainNode> main;
    }

    static enum ResultType {
        FOUND, NOTFOUND, RESTART
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
        public CNode inserted (final FlagPos flagPos, final SNode snode) {
            final ArrayNode[] narr = BasicTrie.inserted (this.array, flagPos.position, snode);
            return new CNode (narr, flagPos.flag | this.bitmap);
        }

        public CNode updated (final int position, final SNode nsn) {
            final ArrayNode[] narr = BasicTrie.updated (this.array, position, nsn);
            return new CNode (narr, this.bitmap);
        }

        public CNode removed (final FlagPos flagPos) {
            final ArrayNode[] narr = BasicTrie.removed (this.array, flagPos.position);
            if (narr.length == 0) {
                return null;
            } else {
                return new CNode (narr, this.bitmap - flagPos.flag);
            }
        }

        public final long bitmap;

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

        CNode (final ArrayNode[] array, final long bitmap) {
            this.array = array;
            this.bitmap = bitmap;
        }

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
