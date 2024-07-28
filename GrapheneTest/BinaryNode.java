/**
 * Represents a binary node in the tree with left and right children.
 * Extends Node and implements the accept method for the visitor pattern.
 */
public class BinaryNode extends Node {
    private Node left;   // Left child of the node
    private Node right;  // Right child of the node
    
    /**
     * Constructs a BinaryNode with the specified left and right children.
     * @param left The left child of this node.
     * @param right The right child of this node.
     */
    public BinaryNode(Node left, Node right) {
        this.left = left;
        this.right = right;
    }
    
    /**
     * Gets the left child of this node.
     * @return The left child node.
     */
    public Node getLeft() {
        return left;
    }
    
    /**
     * Gets the right child of this node.
     * @return The right child node.
     */
    public Node getRight() {
        return right;
    }
    
    /**
     * Sets the left child of this node.
     * @param left The new left child node.
     */
    public void setLeft(Node left) {
        this.left = left;
    }
    
    /**
     * Sets the right child of this node.
     * @param right The new right child node.
     */
    public void setRight(Node right) {
        this.right = right;
    }

    /**
     * Accepts a visitor to perform operations on this node.
     * @param visitor The visitor performing the operations.
     */
    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
