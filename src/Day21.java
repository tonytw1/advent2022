import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Day21 {

    @Test
    public void day21() throws Exception {
        // 25 mins for part!
        // Would kind of hope that the calls form a nice 2 tree which we can just traverse in the right order
        assertEquals(152, findPart1RootValue(parseInput("day21.txt")));
        assertEquals(169525884255464L, findPart1RootValue(parseInput("day21-actual.txt")));
    }

    @Test
    public void day21part2() throws Exception {
        // Root has 2 branches coming off it; we are in one of them.
        // The other branch has a value which the head of our branch must match.
        // The other branch's value is unchanged from part 1.
        // If we are directly below root then out value must match the other root child.
        // There could be a recursive element to this.

        Node part1Root = parseInput("day21.txt");
        visit(part1Root);

        // Values of the top to branches
        System.out.println(part1Root.left.value + " v " + part1Root.right.value);

        // And a quick search to find which branch we are in...
        String outNode = "humn";
        System.out.println(contains(outNode, part1Root.left));
        System.out.println(contains(outNode, part1Root.right));
    }

    private boolean contains(String name, Node node) {
        if (node.name.equals(name)) {
            return true;
        }

        boolean inLeft = false;
        if (node.left != null ) {
            inLeft = contains(name, node.left);
        }
        boolean inRight = false;
        if (node.left != null ) {
            inRight = contains(name, node.right);
        }

        return inLeft || inRight;
     }

    private long findPart1RootValue(Node root) {
        visit(root);
        return root.value;
    }

    private void visit(Node node) {
        if (node.value != null) {
            // There is no work to do here of below
            return;
        }
        visit(node.left);
        visit(node.right);
        // Both our children must now have resolved values, so we can calculate or value
        Long left = node.left.value;
        Long right = node.right.value;
        if (node.operation.equals("+")) {
            node.value = left + right;
        } else if (node.operation.equals("-")) {
            node.value = left - right;
        } else if (node.operation.equals("*")) {
            node.value = left * right;
        } else if (node.operation.equals("/")) {
            node.value = left / right;
        } else {
            throw new UnsupportedOperationException();
        }
    }

    private Node parseInput(String fileName) throws IOException {
        // Read the file; create a node the first appearance of each monkey; we may need to update these nodes as further details appear
        Map<String, Node> nodesByName = new HashMap<>();

        BufferedReader input = new BufferedReader(new FileReader(fileName));
        while (input.ready()) {
            String line = input.readLine();
            // Line is node name followed by it's defination.
           String name = line.split(":")[0];
           String definition = line.split(":")[1].trim();

           // Create this node if it has not been previously referenced
            Node node = getOrCreate(name, nodesByName);

            if (definition.matches("\\d+")) {
               Long value = Long.parseLong(definition);
               node.value = value;
           } else {
               // Parse out the definition
               String leftName = definition.split(" ")[0];
               String operation = definition.split(" ")[1];
               String rightName = definition.split(" ")[2];

               node.left = getOrCreate(leftName, nodesByName);
               node.right = getOrCreate(rightName, nodesByName);
               node.operation = operation;
           }
        }

        // We should now be able to preform a child first traversal of the tree, setting value
        Node root = nodesByName.get("root");
        return root;
    }

    private Node getOrCreate(String name, Map<String, Node> nodesByName) {
        Node node = nodesByName.get(name);
        if (node == null) {
            node = new Node(name, null, null, null, null);
            nodesByName.put(name, node);
        }
        return node;
    }

    class Node {
        final String name;
        Long value;
        String operation;
        Node left;
        Node right;

        public Node(String name, Long value, String function, Node left, Node right) {
            this.name = name;
            this.value = value;
            this.operation = function;
            this.left = left;
            this.right = right;
        }
    }

}
