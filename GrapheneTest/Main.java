import java.io.IOException;

/**
 * Main class to run the program.
 * Reads the tree structure from a file, builds the tree, and prints it.
 */
public class Main {
    public static void main(String[] args) {
        String filePath = "testmarkdown.txt";  // Path to the input file
        Parser builder = new Parser();  // Create a TreeBuilder instance
        Printer printer = new Printer();  // Create a Printer instance
        
        try {
            Node root = builder.buildTreeFromFile(filePath);  // Build the tree from the file
            if (root != null) {
                root.accept(printer);  // Print the tree structure
                System.out.println(printer.getOutput());
            } else {
                System.out.println("Failed to build the tree.");
            }
        } catch (IOException e) {
            e.printStackTrace();  // Print stack trace for any IO exceptions
        }
    }
}
