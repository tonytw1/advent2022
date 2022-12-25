import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Day13 {

    @Test
    public void day13() throws Exception {
        assertEquals(13, sumFor(loadPairs("day13.txt")));
        assertEquals(5852, sumFor(loadPairs("day13-actual.txt")));
    }

    @Test
    public void day13part2() throws Exception {
        assertEquals(140, decode(loadPairs("day13.txt")));
        assertEquals(24190, decode(loadPairs("day13-actual.txt")));
    }

    @Test
    public void testParsingAndCompare() throws Exception {
        List<Packet[]> pairs = loadPairs("day13.txt");

        assertEquals(-1, pairs.get(0)[0].compareTo(pairs.get(0)[1]));
        assertEquals(-1, pairs.get(1)[0].compareTo(pairs.get(1)[1]));
        assertEquals(1, pairs.get(2)[0].compareTo(pairs.get(2)[1]));
        assertEquals(-1, pairs.get(3)[0].compareTo(pairs.get(3)[1]));
        assertEquals(1, pairs.get(4)[0].compareTo(pairs.get(4)[1]));
        assertEquals(-1, pairs.get(5)[0].compareTo(pairs.get(5)[1]));
        assertEquals(1, pairs.get(6)[0].compareTo(pairs.get(6)[1]));
        assertEquals(1, pairs.get(7)[0].compareTo(pairs.get(7)[1]));

        assertEquals(150, loadPairs("day13-actual.txt").size());
    }

    private int sumFor(List<Packet[]> pairs) {
        int sum = 0;
        for (int i = 0; i < pairs.size(); i++) {
            Packet[] pair = pairs.get(i);
            int i1 = pair[0].compareTo(pair[1]);
            if (i1 < 1) {
                sum = sum + (i + 1);
            }
        }
        return sum;
    }


    private int decode(List<Packet[]> pairs) {
        // Decompose the pairs into a single list; add the break packets and then sort.
        // We already have the comp

        List<Packet> all = new ArrayList<>();
        for (Packet[] pair : pairs) {
            all.add(pair[0]);
            all.add(pair[1]);
        }

        Packet divider1 = new Packet();
        Packet two = new Packet();
        two.add(new SingleValue(2));
        divider1.add(two);

        Packet six = new Packet();
        six.add(new SingleValue(6));
        Packet divider2 = new Packet();
        divider2.add(six);

        all.add(divider1);
        all.add(divider2);

        Collections.sort(all);

        int sum = 1;
        int i = 1;
        for (Packet packet : all) {
            if (packet == divider1 || packet == divider2) {
                sum = sum * i;
            }
            i++;
        }
        return sum;
    }

    private List<Packet[]> loadPairs(String filename) throws IOException {
        BufferedReader input = new BufferedReader(new FileReader(filename));
        List<Packet[]> pairs = new ArrayList<>();
        while (input.ready()) {
            // Read 2 packets and a blank line
            Packet[] pair = new Packet[2];
            pair[0] = parsePacket(input.readLine());
            pair[1] = parsePacket(input.readLine());
            pairs.add(pair);
            input.readLine();
        }
        return pairs;
    }

    private Packet parsePacket(String line) {
        // All lines are packets; so we can open a packet and skip the first open bracket
        Packet root = new Packet();
        // Because this is a nested problem keep a stack of which packet we are currently writing into
        char[] chars = line.toCharArray();
        Stack<Packet> writing = new Stack<>();
        writing.add(root);
        int i = 1;
        while (!writing.empty()) {
            char c = chars[i];
            if (c >= '0' && c <= '9') {
                // Start of digit; scan to the end
                // Not great but added after the fact when the single digit assumption failed
                int d = 1;
                char digit = c;
                StringBuilder num = new StringBuilder();
                while (digit >= '0' && digit <= '9') {
                    num.append(digit);
                    digit = chars[i + d];
                    d++;
                }
                writing.peek().add(new SingleValue(Long.parseLong(num.toString())));
                i = i + (d - 2);

            } else if (c == ',') {
                // ignore
            } else if (c == '[') {
                // Step into a new nest
                Packet nestedPacket = new Packet();
                writing.peek().add(nestedPacket);
                writing.add(nestedPacket);
            } else if (c == ']') {
                // Close the current packet
                writing.pop();
            } else {
                throw new RuntimeException("Unexpected char: " + c);
            }
            i++;
        }
        return root;
    }

    class Packet implements Packable {
        List<Packable> items = new ArrayList<>();

        public void add(Packable item) {
            items.add(item);
        }

        @Override
        public String render() {
            StringBuilder out = new StringBuilder();
            out.append("[");
            for (Packable item : items) {
                out.append(" " + item.render() + " ");
            }
            out.append("]");
            return out.toString();
        }

        @Override
        public int compareTo(Object o) {
            if (o instanceof Packet) {
                int thisSize = this.items.size();
                int theirSize = ((Packet) o).items.size();
                for (int i = 0; i < thisSize && i < theirSize; i++) {
                    Packable l = this.items.get(i);
                    Packable r = ((Packet) o).items.get(i);
                    if (l instanceof SingleValue && r instanceof Packet) {
                        Packet p = new Packet();
                        p.add(l);
                        l = p;
                    }
                    if (r instanceof SingleValue && l instanceof Packet) {
                        Packet p = new Packet();
                        p.add(r);
                        r = p;
                    }
                    int c = l.compareTo(r);
                    if (c != 0) {
                        return c;
                    }
                }
                // Reached end of list without finding a winning value; size of the list may decide it
                if (thisSize < theirSize) {
                    return -1;
                } else if (thisSize > theirSize) {
                    return 1;
                } else {
                    return 0;
                }
            }
            throw new UnsupportedOperationException();
        }
    }

    class SingleValue implements Packable {
        Long value;

        public SingleValue(long value) {
            this.value = value;
        }

        @Override
        public String render() {
            return Long.toString(value);
        }

        @Override
        public int compareTo(Object o) {
            if (o instanceof SingleValue) {
                return Long.compare(this.value, ((SingleValue) o).value);
            }
            throw new UnsupportedOperationException();
        }
    }

    interface Packable extends Comparable {    // Use an interface to get nested packages and single values to coexist.
        String render();
    }
}
