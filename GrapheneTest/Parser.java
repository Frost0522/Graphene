import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * Builds a binary tree from a text file.
 * Parses the file and constructs BinaryNode objects.
 */
public class Parser {

    /**
     * Builds a binary tree from the specified file.
     * @param filePath The path to the file containing the tree structure.
     * @return The root node of the constructed binary tree.
     * @throws IOException If there is an error reading the file.
     */
    public Node buildTreeFromFile(String filePath) throws IOException {
        Map<String, BinaryNode> nodeMap = new HashMap<>();  // Map to store node references
        Stack<BinaryNode> stack = new Stack<>();  // Stack to maintain the current path in the tree
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            
            while ((line = reader.readLine()) != null) {
                int indentLevel = getIndentLevel(line);  // Determine the level of indentation
                String nodeName = line.trim();  // Node name
                
                BinaryNode currentNode = new BinaryNode(null, null);  // Create a new BinaryNode
                nodeMap.put(nodeName, currentNode);  // Store the node in the map
                
                if (indentLevel == 0) {
                    stack.push(currentNode);  // Push root-level nodes to the stack
                } else {
                    while (stack.size() > indentLevel) {
                        stack.pop();  // Pop the stack to maintain the correct level
                    }
                    
                    BinaryNode parent = stack.peek();  // Get the parent node
                    if (parent.getLeft() == null) {
                        parent.setLeft(currentNode);  // Set left child if empty
                    } else {
                        parent.setRight(currentNode);  // Set right child if left is occupied
                    }
                    
                    stack.push(currentNode);  // Push the current node to the stack
                }
            }
        }
        
        return stack.isEmpty() ? null : stack.firstElement();  // Return the root node
    }

    /**
     * Gets the level of indentation in a line.
     * @param line The line to check.
     * @return The level of indentation.
     */
    private int getIndentLevel(String line) {
        int indentLevel = 0;
        while (line.startsWith("    ")) {
            indentLevel++;
            line = line.substring(4);
        }
        return indentLevel;
    }
}
