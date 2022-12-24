import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class Day20DoubleLinkedList {

    @Test
    public void day20() throws Exception {
        // When we move a number we need to find the node n places away.
        // Before we move, we need find the previous node
        // and link it's next to our next.
        // next nodes previous needs to link to out previous.
        // On the node which was n away,
        // We set it's next to us.
        // We set our next to it's next.
        // We set it's next's previous to us.
        assertEquals(3, decode(mix(parseInput("day20.txt"))));
        assertEquals(23321, decode(mix(parseInput("day20-actual.txt"))));
    }

    private Node mix(List<Integer> input) {
        Node head = makeDoublyLinkListFrom(input);

        // Because nodes are going to be jumping around let's use some memory to remember the order of they should be called in
        Node[] toMove = new Node[input.size()];
        Node node = head;
        for (int i = 0; i < input.size(); i++) {
            toMove[i] = node;
            node = node.next;
        }

        // We need to move each number in the input which gives us the number of iterations to make
        for (int i = 0; i < input.size(); i++) {
            Node moving = toMove[i];

            // Find the current node and look up how many steps we want to take
            int delta = moving.value % (input.size() - 1);
            // Find the node at the location by iterating to it; small n so we should be ok; can't think of a constant time model
            if (delta != 0) {
                Node destination = moving;
                for (int steps = 1; steps <= Math.abs(delta); steps++) {
                    if (delta < 0) {
                        destination = destination.previous;
                    } else {
                        destination = destination.next;
                    }
                }
                if (delta < 0) {
                    destination = destination.previous;
                }

                // Lift the moving node out of the chain by bridging over it
                Node afterDestination = destination.next;
                Node beforeMoving = moving.previous;
                Node afterMoving = moving.next;

                beforeMoving.next = afterMoving;
                afterMoving.previous = beforeMoving;

                // Insert the mover back into the chain in after destination
                destination.next = moving;
                moving.previous = destination;
                moving.next = afterDestination;

                afterDestination.previous = moving;

                // There is no actual requirement to track the moving head
            }
        }
        return head;
    }

    private int decode(Node mixed) {
        // Run up to the 0 node
        Node node = mixed;
        while (node.next != null) {
            Integer value = node.value;
            if (value == 0) {
                break;
            }
            node = node.next;
        }

        int sum = 0;
        int interval = 1000;
        for (int i = 0; i < (interval * 3) + 1; i++) {
            if (i % interval == 0) {
                sum += node.value;
            }
            node = node.next;
        }
        return sum;
    }

    @Test
    public void numbersInInputsAresNotUnique() throws Exception {
        List<Integer> input = parseInput("day20-actual.txt");
        Set<Integer> asSet = new HashSet<>();
        asSet.addAll(input);

        assertNotEquals(input.size(), asSet.size());
    }

    @Test
    public void zeroMarkerIsUnique() throws Exception {
        List<Integer> input = parseInput("day20-actual.txt");
        Map<Integer, Integer> counts = new HashMap<>();
        for (int i : input) {
            counts.put(i, counts.getOrDefault(i, 0) + 1);
        }
        assertEquals(1, counts.get(0));
    }

    @Test
    public void canMakeLoopFromInput() throws Exception {
        List<Integer> input = parseInput("day20-actual.txt");

        Node head = makeDoublyLinkListFrom(input);

        // Check we can run around the loop both ways
        Node node = head;
        for (int i = 0; i < input.size() * 2; i++) {
            node = node.next;
        }
        node = head;
        for (int i = 0; i < input.size() * 2; i++) {
            node = node.previous;
        }

        assertEquals(8359, head.value);
    }

    private Node makeDoublyLinkListFrom(List<Integer> input) {
        // Been able to look back and forward suggests a doubly linked list might be useful.
        // We can get the wrap around for free by linking the head and tail.
        //
        // Builds the input sequence of numbers into a doubly linked list with the head and tail linked into a loop.
        //
        // Returns the head node.
        Node previous = null;
        Node last = null;
        // Create a singly backwards linked chain
        for (int v : input) {
            Node node = new Node(v);
            node.previous = previous;
            previous = node;
            last = node;
        }

        // Now we can run back down the chain linking up the next links.
        Node next = null;
        Node node = last;
        while (node != null) {
            node.next = next;
            next = node;
            node = node.previous;
        }
        Node head = next;

        // We can then link the head and tail
        head.previous = last;
        last.next = head;

        return head;
    }


    private List<Integer> parseInput(String filename) throws IOException {
        List<Integer> input = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        while (reader.ready()) {
            input.add(Integer.parseInt(reader.readLine()));
        }
        return input;
    }

    class Node {
        Node previous;
        Node next;
        Integer value;

        public Node(Integer value) {
            this.value = value;
        }
    }

    private void renderForward(Node head, int steps) {
        StringBuilder out = new StringBuilder();
        Node render = head;
        for (int j = 0; j < steps; j++) {
            out.append(render.value + " ");
            render = render.next;
        }
        System.out.println(out);
    }

    private void renderBack(Node head, int steps) {
        StringBuilder out = new StringBuilder();
        Node render = head;
        for (int j = 0; j < steps; j++) {
            out.append(render.value + " ");
            render = render.previous;
        }
        System.out.println(out);
    }

}
