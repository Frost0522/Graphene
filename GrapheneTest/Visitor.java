/**
 * Interface for implementing the visitor pattern.
 * Defines methods for visiting different types of nodes.
 */
public interface Visitor {
    /**
     * Visits a BinaryNode.
     * @param node The BinaryNode to visit.
     */
    void visit(BinaryNode node);
    // Add methods for other node types if needed
}
