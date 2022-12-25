import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Day21 {

    @Test
    public void day21() throws Exception {
        // 25 mins for part1; very happy
        assertEquals(152, findPart1RootValue(parseInput("day21.txt")));
        assertEquals(169525884255464L, findPart1RootValue(parseInput("day21-actual.txt")));
    }

    @Test
    public void day21part2() throws Exception {
        assertEquals(301L, findPart2Value(parseInput("day21.txt"), "humn").longValue());
        assertEquals(3247317268284L, findPart2Value(parseInput("day21-actual.txt"), "humn").longValue());
    }

    private BigInteger findPart2Value(Node root, String ourNodeName) {
        // Put the tree into the same state as solved part1
        visit(root);

        // We can find the chain of nodes from root down to our problem node.
        Queue<Node> queue = new ArrayDeque<>();
        Node node = root;
        while (!node.name.equals(ourNodeName)) {
            queue.offer(node);
            if (contains(ourNodeName, node.left)) {
                node = node.left;
            } else if (contains(ourNodeName, node.right)) {
                node = node.right;
            } else {
                throw new RuntimeException();
            }
        }

        BigInteger answer = null;
        // We can walk down this queue amending the nodes to push the new equal requirement down to our node.
        // Had to work this on paper first!

        while (!queue.isEmpty()) {
            node = queue.poll();

            // This child does not lead to the problem node and it's value is fixed and uneffected by part2
            Node fixedChild = contains(ourNodeName, node.left) ? node.right : node.left;    // could avoid this repeated lookup by peeking in the queue but messy boundary checks
            Node nextNode = fixedChild == node.left ? node.right : node.left;

            if (node == root) {
                // Special case; next node's value must equal the opposite arm
                // Root has 2 branches coming off it; we are in one of them.
                // The other branch has a value which the head of our branch must match.
                // The other branch's value is unchanged from part 1; therefore our value must match the other arm
                nextNode.value = fixedChild.value;

            } else {
                // In deep nodes, the child which does not lead to the problem node has a fixed value.
                // That fixed value and the current node's operation give is the value of the new value of the node leading
                // to the problem node. We can stop once we've reached the parent of the problem node and calculated it's value.
                // Find the child with the fixed value

                // Apply the inverse operation to adjust the node fixed child to the new condition
                BigInteger inverse;
                if (node.operation.equals("+")) {
                    inverse = operate(node.value, fixedChild.value, "-");
                } else if (node.operation.equals("-")) {
                    // This case was only found by cross checking node.value to node.left node.operation node.right at the end of each turn
                    if (fixedChild == node.left) {
                        inverse = operate(fixedChild.value, node.value, "-");
                    } else {
                        inverse = operate(fixedChild.value, node.value, "+");
                    }

                } else if (node.operation.equals("*")) {
                    inverse = operate(node.value, fixedChild.value, "/");
                } else if (node.operation.equals("/")) {
                    inverse = operate(node.value, fixedChild.value, "*");
                } else {
                    throw new UnsupportedOperationException();
                }
                nextNode.value = inverse;
                answer = nextNode.value;
            }
        }
        return answer;
    }

    private boolean contains(String name, Node node) {
        if (node.name.equals(name)) {
            return true;
        }

        boolean inLeft = false;
        if (node.left != null) {
            inLeft = contains(name, node.left);
        }
        boolean inRight = false;
        if (node.left != null) {
            inRight = contains(name, node.right);
        }

        return inLeft || inRight;
    }

    private long findPart1RootValue(Node root) {
        // Would kind of hope that the calls form a nice 2 tree which we can just traverse in the right order
        visit(root);
        // Yep it is!
        return root.value.longValue();
    }

    private void visit(Node node) {
        if (node.value != null) {
            // There is no work to do here of below
            return;
        }
        visit(node.left);
        visit(node.right);
        // Both our children must now have resolved values, so we can calculate or value
        node.value = operate(node.left.value, node.right.value, node.operation);
    }

    private BigInteger operate(BigInteger left, BigInteger right, String operation) {
        if (operation.equals("+")) {
            return left.add(right);
        } else if (operation.equals("-")) {
            return left.subtract(right);
        } else if (operation.equals("*")) {
            return left.multiply(right);
        } else if (operation.equals("/")) {
            return left.divide(right);
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
                node.value = new BigInteger(definition);
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
        BigInteger value;
        String operation;
        Node left;
        Node right;

        public Node(String name, BigInteger value, String function, Node left, Node right) {
            this.name = name;
            this.value = value;
            this.operation = function;
            this.left = left;
            this.right = right;
        }
    }

}
