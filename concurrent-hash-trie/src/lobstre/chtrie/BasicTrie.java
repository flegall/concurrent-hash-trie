package lobstre.chtrie;

import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

public class BasicTrie {
    /**
     * Root node of the trie
     */
    private final INode root;

    /**
     * Width in bits
     */
    private final byte width;

    /**
     * Builds a {@link BasicTrie} instance
     */
    public BasicTrie () {
        this (6);
    }

    /**
     * Builds a {@link BasicTrie} instance
     * 
     * @param width
     *            the Trie width in power-of-two exponents. Values are expected
     *            between between 1 & 6, other values will be clamped.
     *            <p>
     *            The width defines the "speed" of the trie:
     *            <ul>
     *            <li>A value of 1: gives an actual width of two items per
     *            level, hence the trie is O(Log2(N))</li>
     *            <li>A value of 6: gives an actual width of 64 items per level,
     *            hence the trie is O(Log64(N)</li>
     *            </ul>
     */
    public BasicTrie (final int width) {
        this.root = new INode (new CNode ());
        if (width > 6) {
            this.width = 6;
        } else if (width < 1) {
            this.width = 1;
        } else {
            this.width = (byte) width;
        }
    }

    /**
     * Inserts or updates a key/value mapping.
     * 
     * @param key
     *            a key {@link Object}
     * @param value
     *            a value Object
     * @return the previous value associated to key
     */
    public Object insert (final Object key, final Object value) {
        while (true) {
            final Result res = iinsert (this.root, hash (key), key, value, 0, null);
            switch (res.type) {
            case FOUND:
                return res.result;
            case NOTFOUND:
                return null;
            case RESTART:
                continue;
            default:
                throw new RuntimeException ("Unexpected case: " + res.type);
            }
        }
    }

    /**
     * Looks up the value associated to a key
     * 
     * @param key
     *            a key {@link Object}
     * @return the value associated to k
     */
    public Object lookup (final Object key) {
        while (true) {
            // Getting lookup result
            final Result res = ilookup (this.root, hash (key), key, 0, null);
            switch (res.type) {
            case FOUND:
                return res.result;
            case NOTFOUND:
                return null;
            case RESTART:
                continue;
            default:
                throw new RuntimeException ("Unexpected case: " + res.type);
            }
        }
    }

    /**
     * Removes a key/value mapping
     * 
     * @param key
     *            the key Object
     * @return true if removed was performed, false otherwises
     */
    public boolean remove (final Object key) {
        while (true) {
            // Getting remove result
            final Result res = iremove (this.root, hash (key), key, 0, null);
            switch (res.type) {
            case FOUND:
                return true;
            case NOTFOUND:
                return false;
            case RESTART:
                continue;
            default:
                throw new RuntimeException ("Unexpected case: " + res.type);
            }
        }
    }

    private Result ilookup (final INode i, final int hashcode, final Object k, final int level, final INode parent) {
        final MainNode main = i.getMain ();

        // Usual case
        if (main instanceof CNode) {
            final CNode cn = (CNode) main;
            final FlagPos flagPos = flagPos (hashcode, level, cn.bitmap, this.width);

            // Asked for a hash not in trie
            if (0L == (flagPos.flag & cn.bitmap)) {
                return new Result (ResultType.NOTFOUND, null);
            }

            final BranchNode an = cn.array [flagPos.position];
            if (an instanceof INode) {
                // Looking down
                final INode sin = (INode) an;
                return ilookup (sin, hashcode, k, level + this.width, i);
            }
            if (an instanceof SNode) {
                // Found the hash locally, let's see if it matches
                final SNode sn = (SNode) an;
                if (sn.hashcode == hashcode) {
                    return new Result (ResultType.FOUND, sn.entry.value);
                } else {
                    return new Result (ResultType.NOTFOUND, null);
                }
            }
        }

        // Cleaning up trie
        if (main instanceof TNode) {
            clean (parent, level - this.width);
            return new Result (ResultType.RESTART, null);
        }
        throw new RuntimeException ("Unexpected case: " + main);
    }

    private Result iinsert (final INode i, final int hashcode, final Object k, final Object v, final int level, final INode parent) {
        final MainNode main = i.getMain ();

        // Usual case
        if (main instanceof CNode) {
            final CNode cn = (CNode) main;
            final FlagPos flagPos = flagPos (hashcode, level, cn.bitmap, this.width);

            // Asked for a hash not in trie, let's insert it
            if (0L == (flagPos.flag & cn.bitmap)) {
                final SNode snode = new SNode (hashcode, new Entry (k, v));
                final CNode ncn = cn.inserted (flagPos, snode);
                return i.casMain (main, ncn) ? new Result (ResultType.FOUND, null) : new Result (ResultType.RESTART, null);
            }

            final BranchNode an = cn.array [flagPos.position];
            if (an instanceof INode) {
                // Looking down
                final INode sin = (INode) an;
                return iinsert (sin, hashcode, k, v, level + this.width, i);
            }
            if (an instanceof SNode) {
                final SNode sn = (SNode) an;
                final SNode nsn = new SNode (hashcode, new Entry (k, v));
                // Found the hash locally, let's see if it matches
                if (sn.hashcode == hashcode) {
                    // Updates the key with the new value
                    final CNode ncn = cn.updated (flagPos.position, nsn);
                    return i.casMain (main, ncn) ? new Result (ResultType.FOUND, sn.entry.value) : new Result (ResultType.RESTART, null);
                } else {
                    // Creates a sub-level
                    final CNode scn = new CNode (sn, nsn, level + this.width, this.width);
                    final INode nin = new INode (scn);
                    final CNode ncn = cn.updated (flagPos.position, nin);
                    return i.casMain (main, ncn) ? new Result (ResultType.FOUND, null) : new Result (ResultType.RESTART, null);
                }
            }
        }

        // Cleaning up trie
        if (main instanceof TNode) {
            clean (parent, level - this.width);
            return new Result (ResultType.RESTART, null);
        }
        throw new RuntimeException ("Unexpected case: " + main);
    }

    private Result iremove (final INode i, final int hashcode, final Object k, final int level, final INode parent) {
        final MainNode main = i.getMain ();

        // Usual case
        if (main instanceof CNode) {
            final CNode cn = (CNode) main;
            final FlagPos flagPos = flagPos (hashcode, level, cn.bitmap, this.width);

            // Asked for a hash not in trie
            if (0L == (flagPos.flag & cn.bitmap)) {
                return new Result (ResultType.NOTFOUND, null);
            }

            Result res = null;
            final BranchNode an = cn.array [flagPos.position];
            if (an instanceof INode) {
                // Looking down
                final INode sin = (INode) an;
                res = iremove (sin, hashcode, k, level + this.width, i);
            }
            if (an instanceof SNode) {
                // Found the hash locally, let's see if it matches
                final SNode sn = (SNode) an;
                if (sn.hashcode == hashcode) {
                    final CNode ncn = cn.removed (flagPos);
                    final MainNode cntn = toContracted (ncn, level);
                    if (i.casMain (cn, cntn)) {
                        res = new Result (ResultType.FOUND, sn.entry);
                    } else {
                        res = new Result (ResultType.RESTART, null);
                    }
                } else {
                    res = new Result (ResultType.NOTFOUND, null);
                }
            }
            if (null == res) {
                throw new RuntimeException ("Unexpected case: " + an);
            }
            if (res.type == ResultType.NOTFOUND || res.type == ResultType.RESTART) {
                return res;
            }

            if (i.getMain () instanceof TNode) {
                cleanParent (parent, i, hashcode, level - this.width);
            }
            return res;
        }

        // Cleaning up trie
        if (main instanceof TNode) {
            clean (parent, level - this.width);
            return new Result (ResultType.RESTART, null);
        }
        throw new RuntimeException ("Unexpected case: " + main);
    }

    private void cleanParent (final INode parent, final INode i, final int hashCode, final int level) {
        while (true) {
            final MainNode m = i.getMain ();
            final MainNode pm = parent.getMain ();
            if (pm instanceof CNode) {
                final CNode pcn = (CNode) pm;
                final FlagPos flagPos = flagPos (hashCode, level, pcn.bitmap, this.width);
                if (0L == (flagPos.flag & pcn.bitmap)) {
                    return;
                }
                final BranchNode sub = pcn.array [flagPos.position];
                if (sub != i) {
                    return;
                }
                if (m instanceof TNode) {
                    final CNode ncn = pcn.updated (flagPos.position, ((TNode) m).untombed ());
                    if (parent.casMain (pcn, ncn)) {
                        return;
                    } else {
                        continue;
                    }
                }
            } else {
                return;
            }
        }
    }

    private void clean (final INode i, final int level) {
        final MainNode m = i.getMain ();
        if (m instanceof CNode) {
            i.casMain (m, toCompressed ((CNode) m, level));
        }
    }

    private MainNode toCompressed (final CNode cn, final int level) {
        final CNode ncn = cn.copied ();

        // Resurrect tombed nodes.
        for (int i = 0; i < ncn.array.length; i++) {
            final BranchNode an = ncn.array [i];
            final TNode tn = getTombNode (an);
            if (null != tn) {
                ncn.array [i] = tn.untombed ();
            }
        }

        return toContracted (ncn, level);
    }

    private MainNode toContracted (final CNode cn, final int level) {
        if (level > 0 && 1 == cn.array.length) {
            final BranchNode bn = cn.array [0];
            if (bn instanceof SNode) {
                return ((SNode) bn).tombed ();
            }
        }
        return cn;
    }

    private TNode getTombNode (final BranchNode an) {
        if (an instanceof INode) {
            final INode in = (INode) an;
            final MainNode mn = in.getMain ();
            if (mn instanceof TNode) {
                final TNode tn = (TNode) mn;
                return tn;
            }
        }
        return null;
    }

    static int hash (final Object key) {
        int h = key.hashCode ();
        // This function ensures that hashCodes that differ only by
        // constant multiples at each bit position have a bounded
        // number of collisions (approximately 8 at default load factor).
        h ^= (h >>> 20) ^ (h >>> 12);
        return h ^ (h >>> 7) ^ (h >>> 4);
    }

    /**
     * Returns a copy an {@link BranchNode} array with an updated
     * {@link BranchNode} value at a certain position.
     * 
     * @param array
     *            the source {@link BranchNode} array
     * @param position
     *            the position
     * @param n
     *            the updated {@link BranchNode} value
     * @return an updated copy of the source {@link BranchNode} array
     */
    static BranchNode[] updated (final BranchNode[] array, final int position, final BranchNode n) {
        final BranchNode[] narr = new BranchNode[array.length];
        System.arraycopy (array, 0, narr, 0, array.length);
        narr [position] = n;
        return narr;
    }

    /**
     * Returns a copy an {@link BranchNode} array with an inserted
     * {@link BranchNode} value at a certain position.
     * 
     * @param array
     *            the source {@link BranchNode} array
     * @param position
     *            the position
     * @param n
     *            the inserted {@link BranchNode} value
     * @return an updated copy of the source {@link BranchNode} array
     */
    static BranchNode[] inserted (final BranchNode[] array, final int position, final BranchNode n) {
        final BranchNode[] narr = new BranchNode[array.length + 1];
        System.arraycopy (array, 0, narr, 0, position);
        System.arraycopy (array, position, narr, position + 1, array.length - position);
        narr [position] = n;
        return narr;
    }

    /**
     * Returns a copy an {@link BranchNode} array with a removed
     * {@link BranchNode} value at a certain position.
     * 
     * @param array
     *            the source {@link BranchNode} array
     * @param position
     *            the position
     * @return an updated copy of the source {@link BranchNode} array
     */
    static BranchNode[] removed (final BranchNode[] array, final int position) {
        final BranchNode[] narr = new BranchNode[array.length - 1];
        System.arraycopy (array, 0, narr, 0, position);
        System.arraycopy (array, position + 1, narr, position, array.length - position - 1);
        return narr;
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

    /**
     * A Marker interface for what can be in an INode (CNode or SNode)
     */
    static interface MainNode {
    }

    /**
     * A Marker interface for what can be in a CNode array. (INode or SNode)
     */
    static interface BranchNode {
    }

    /**
     * A CAS-able Node which may reference either a CNode or and SNode
     */
    static class INode implements BranchNode {
        /**
         * Builds an {@link INode} instance
         * 
         * @param n
         *            a {@link MainNode}
         */
        public INode (final MainNode n) {
            INODE_UPDATER.set (this, n);
        }

        /**
         * Gets the {@link MainNode} instance this {@link INode} contains
         * 
         * @return the {@link MainNode}
         */
        public MainNode getMain () {
            return INODE_UPDATER.get (this);
        }

        /**
         * Compare and set the {@link MainNode} instance of this {@link INode}
         * 
         * @param expected
         *            the expected {@link MainNode} instance
         * @param update
         *            the updated {@link MainNode} instance
         * @return true if it sets
         */
        public boolean casMain (final MainNode expected, final MainNode update) {
            return INODE_UPDATER.compareAndSet (this, expected, update);
        }

        /**
         * Atomic Updater for the INode.main field
         */
        private static final AtomicReferenceFieldUpdater<INode, MainNode> INODE_UPDATER = AtomicReferenceFieldUpdater.newUpdater (INode.class, MainNode.class, "main");
        /**
         * The {@link MainNode} instance
         */
        @SuppressWarnings("unused")
        private volatile MainNode main;
    }

    /**
     * A Node that may contain sub-nodes.
     */
    static class CNode implements MainNode {
        /**
         * Builds a copy of this {@link CNode} instance where a sub-node
         * designated by a position has been added .
         * 
         * @param flagPos
         *            a FlagPos instance
         * @return a copy of this {@link CNode} instance with the inserted node.
         */
        public CNode inserted (final FlagPos flagPos, final SNode snode) {
            final BranchNode[] narr = BasicTrie.inserted (this.array, flagPos.position, snode);
            return new CNode (narr, flagPos.flag | this.bitmap);
        }

        /**
         * Builds a copy of this {@link CNode} instance where a sub
         * {@link BranchNode} designated by a position has been replaced by
         * another one.
         * 
         * @param position
         *            an integer position
         * @param bn
         *            a {@link BranchNode} instance
         * @return a copy of this {@link CNode} instance with the updated node.
         */
        public CNode updated (final int position, final BranchNode bn) {
            final BranchNode[] narr = BasicTrie.updated (this.array, position, bn);
            return new CNode (narr, this.bitmap);
        }

        /**
         * Builds a copy of this {@link CNode} instance where a sub-node
         * designated by flag & a position has been removed.
         * 
         * @param flagPos
         *            a {@link FlagPos} instance
         * @return a copy of this {@link CNode} instance where where a sub-node
         *         designated by flag & a position has been removed.
         */
        public CNode removed (final FlagPos flagPos) {
            final BranchNode[] narr = BasicTrie.removed (this.array, flagPos.position);
            return new CNode (narr, this.bitmap - flagPos.flag);
        }

        /**
         * Builds a copy of the current node.
         * 
         * @return a {@link CNode} copy
         */
        public CNode copied () {
            final BranchNode[] narr = new BranchNode[this.array.length];
            System.arraycopy (this.array, 0, narr, 0, this.array.length);
            return new CNode (narr, this.bitmap);
        }

        /**
         * Builds an empty {@link CNode} instance
         */
        CNode () {
            this.array = new BranchNode[] {};
            this.bitmap = 0L;
        }

        /**
         * Builds a {@link CNode} instance from a single {@link SNode} instance
         * 
         * @param sNode
         *            a {@link SNode} instance
         * @param width
         *            the width (in power-of-two exponents)
         */
        CNode (final SNode sNode, final int width) {
            final long flag = BasicTrie.flag (sNode.hashcode, 0, width);
            this.array = new BranchNode[] { sNode };
            this.bitmap = flag;
        }

        /**
         * Builds a {@link CNode} instance from two {@link SNode} objects
         * 
         * @param sn1
         *            a first {@link SNode} instance
         * @param sn2
         *            a second {@link SNode} instance
         * @param level
         *            the current level (in bit progression)
         * @param width
         *            the width (in power-of-two exponents)
         */
        CNode (final SNode sn1, final SNode sn2, final int level, final int width) {
            final long flag1 = BasicTrie.flag (sn1.hashcode, level, width);
            final long flag2 = BasicTrie.flag (sn2.hashcode, level, width);
            if (flag1 < flag2) {
                this.array = new BranchNode[] { sn1, sn2 };
            } else {
                this.array = new BranchNode[] { sn2, sn1 };
            }
            this.bitmap = flag1 | flag2;
        }

        /**
         * Builds a {@link CNode} from an array of {@link BranchNode} and its
         * computed bitmap.
         * 
         * @param array
         *            the {@link BranchNode} array
         * @param bitmap
         *            the bitmap
         */
        CNode (final BranchNode[] array, final long bitmap) {
            this.array = array;
            this.bitmap = bitmap;
        }

        /**
         * The internal {@link BranchNode} array.
         */
        public final BranchNode[] array;

        /**
         * The bitmap of the currently allocated objects.
         */
        public final long bitmap;
    }

    static abstract class BaseSNode {
        /**
         * Builds a {@link SNode} instance
         * 
         * @param hashcode
         *            its hash code value
         * @param e
         *            its {@link Entry} value
         */
        BaseSNode (final int hashcode, final Entry e) {
            this.hashcode = hashcode;
            this.entry = e;
        }

        /**
         * The object key
         */
        public final int hashcode;

        /**
         * The object value
         */
        public final Entry entry;
    }

    /**
     * A Single Node class, holds a key, a value & a tomb flag.
     */
    static class SNode extends BaseSNode implements BranchNode {
        /**
         * Builds a {@link SNode} instance
         * 
         * @param hashcode
         *            its hash code value
         * @param e
         *            its {@link Entry} value
         */
        SNode (final int hashcode, final Entry e) {
            super (hashcode, e);
        }

        /**
         * @return a copied {@link TNode} for this instance.
         */
        public TNode tombed () {
            return new TNode (this.hashcode, this.entry);
        }
    }

    /**
     * A Tombed node instance
     */
    static class TNode extends BaseSNode implements MainNode {
        /**
         * Builds a {@link TNode} instance
         * 
         * @param hashcode
         *            its hash code value
         * @param e
         *            its {@link Entry} value
         */
        TNode (final int hashcode, final Entry e) {
            super (hashcode, e);
        }

        /**
         * @return a copied {@link SNode} of this instance
         */
        public SNode untombed () {
            return new SNode (this.hashcode, this.entry);
        }
    }

    /**
     * {@link Entry} in the trie
     */
    static class Entry {
        /**
         * Builds an {@link Entry} from key-value mapping
         * 
         * @param key
         *            the key {@link Object}
         * @param value
         *            the value {@link Object}
         */
        public Entry (Object key, Object value) {
            this.key = key;
            this.value = value;
        }

        /**
         * The object key
         */
        public final Object key;

        /**
         * The object value
         */
        public final Object value;
    }

    /**
     * The result of a {@link BasicTrie#flagPos(int, int, long, int)} call.
     * Contains a single bit flag & a position
     */
    static class FlagPos {
        /**
         * Builds a {@link FlagPos} instance
         * 
         * @param flag
         *            the bit flag
         * @param position
         *            the array location.
         */
        FlagPos (final long flag, final int position) {
            this.flag = flag;
            this.position = position;
        }

        /**
         * A single bit flag that may bit compared to a {@link CNode}'s bitmap.
         */
        public final long flag;
        /**
         * Its position in the array
         */
        public final int position;
    }
}
