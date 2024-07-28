/**
 * Abstract base class for all nodes in the tree.
 * Each node must implement the accept method for the visitor pattern.
 */
public abstract class Node {
    /**
     * Accepts a visitor to perform operations on this node.
     * @param visitor The visitor performing the operations.
     */
    public abstract void accept(Visitor visitor);
}
