import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class Day20 {

    @Test
    public void day20() throws Exception {
        // Couldn't get double linked list to work on actual file; so moved to a single linked list for less pointer updates.
        assertEquals(BigInteger.valueOf(3L), decode(mix(parseInput("day20.txt"), 1)));
        assertEquals(BigInteger.valueOf(23321L), decode(mix(parseInput("day20-actual.txt"), 1)));
    }

    @Test
    public void day20part2() throws Exception {
        List<BigInteger> input = parseInput("day20-actual.txt");

        BigInteger scaler = BigInteger.valueOf(811589153L);

        List<BigInteger> scaled = input.stream().map(i -> i.multiply(scaler)).collect(Collectors.toList());
        assertEquals(new BigInteger("1428396909280"), decode(mix(scaled, 10))); // TODO does the double linked list mixer run measurably faster?
    }

    private Node mix(List<BigInteger> input, int times) {
        Node head = makeLinkedListFrom(input);

        // Because nodes are going to be jumping around let's use some memory to remember the order of they should be called in
        Node[] toMove = new Node[input.size()];
        Node node = head;
        for (int i = 0; i < input.size(); i++) {
            toMove[i] = node;
            node = node.next;
        }

        // We need to move each number in the input which gives us the number of iterations to make

        for (int t = 0; t < times; t++) {
            for (int i = 0; i < input.size(); i++) {
                Node moving = toMove[i];

                // Find the current node and look up how many steps we want to take
                // Had to look at the reddit for this -1 https://www.reddit.com/r/adventofcode/comments/zqezkn/comment/j0za6ve/?utm_source=reddit&utm_medium=web2x&context=3
                long delta = moving.value.mod(BigInteger.valueOf(input.size() - 1)).longValue();
                // Find the node at the location by iterating to it; small n so we should be ok; can't think of a constant time model
                if (delta != 0) {
                    Node destination = moving;
                    // Wrap around to get to a previous node; less complicated update code
                    long stepsToDestination = delta > 0 ? delta : input.size() + (delta - 1);
                    for (long steps = 1; steps <= stepsToDestination; steps++) {
                        destination = destination.next;
                    }

                    Node beforeMoving = moving;
                    for (int b = 1; b <= input.size() - 1; b++) {    // Wrap all the way around to previous
                        beforeMoving = beforeMoving.next;
                    }


                    // Lift the moving node out of the chain by bridging over it
                    Node afterMoving = moving.next;
                    beforeMoving.next = afterMoving;

                    // Insert the mover back into the chain in after destination
                    Node afterDestination = destination.next;
                    destination.next = moving;
                    moving.next = afterDestination;

                    // There is no actual requirement to track the moving head
                }
            }
        }
        return head;
    }

    private BigInteger decode(Node mixed) {
        // Run up to the 0 node
        Node node = mixed;
        while (node.next != null) {
            BigInteger value = node.value;
            if (Objects.equals(value, BigInteger.ZERO)) {
                break;
            }
            node = node.next;
        }

        BigInteger sum = BigInteger.ZERO;
        int interval = 1000;
        for (int i = 0; i < (interval * 3) + 1; i++) {
            if (i % interval == 0) {
                sum = sum.add(BigInteger.valueOf(node.value.longValue()));
            }
            node = node.next;
        }
        return sum;
    }

    @Test
    public void numbersInInputsAresNotUnique() throws Exception {
        List<BigInteger> input = parseInput("day20-actual.txt");
        Set<BigInteger> asSet = new HashSet<>();
        asSet.addAll(input);

        assertNotEquals(input.size(), asSet.size());
    }

    @Test
    public void zeroMarkerIsUnique() throws Exception {
        List<BigInteger> input = parseInput("day20-actual.txt");
        Map<BigInteger, Long> counts = new HashMap<>();
        for (BigInteger i : input) {
            counts.put(i, counts.getOrDefault(i, 0L) + 1);
        }
        assertEquals(1, counts.get(BigInteger.ZERO).longValue());
    }

    @Test
    public void canMakeLoopFromInput() throws Exception {
        List<BigInteger> input = parseInput("day20-actual.txt");

        Node head = makeLinkedListFrom(input);

        // Check we can run around the loop both ways
        Node node = head;
        for (int i = 0; i < input.size() * 2; i++) {
            node = node.next;
        }

        assertEquals(BigInteger.valueOf(8359), head.value);
    }

    private Node makeLinkedListFrom(List<BigInteger> input) {
        Node last = new Node(input.get(input.size() - 1));
        Node next = last;
        // Create a singly backwards linked chain
        for (int i = input.size() - 2; i >= 0; i--) {
            BigInteger v = input.get(i);
            Node node = new Node(v);
            node.next = next;
            next = node;
        }

        Node head = next;
        // We can then link the head and tail
        last.next = head;

        return head;
    }


    private List<BigInteger> parseInput(String filename) throws IOException {
        List<BigInteger> input = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        while (reader.ready()) {
            input.add(new BigInteger(reader.readLine()));
        }
        return input;
    }

    class Node {
        Node next;
        BigInteger value;

        public Node(BigInteger value) {
            this.value = value;
        }
    }

}
