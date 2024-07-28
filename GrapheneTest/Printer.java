/**
 * Implements the Visitor interface to print the tree structure.
 * Recursively visits each node and builds a string representation of the tree.
 */
public class Printer implements Visitor {
    private StringBuilder sb = new StringBuilder();  // StringBuilder to accumulate the output
    private int indentLevel = 0;  // Current level of indentation
    
    /**
     * Visits a BinaryNode and builds the tree representation.
     * @param node The BinaryNode to visit.
     */
    @Override
    public void visit(BinaryNode node) {
        printIndentation();
        sb.append("BinaryNode\n");
        
        indentLevel++;
        if (node.getLeft() != null) {
            sb.append("Left:\n");
            node.getLeft().accept(this);
        }
        
        if (node.getRight() != null) {
            sb.append("Right:\n");
            node.getRight().accept(this);
        }
        indentLevel--;
    }

    /**
     * Prints the current level of indentation.
     */
    private void printIndentation() {
        for (int i = 0; i < indentLevel; i++) {
            sb.append("    "); // Adjust the indentation as needed
        }
    }
    
    /**
     * Gets the accumulated output as a string.
     * @return The output string.
     */
    public String getOutput() {
        return sb.toString();
    }
}
