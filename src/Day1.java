import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.PriorityQueue;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Day1 {

    @Test
    public void day1() throws IOException {
        BufferedReader input = new BufferedReader(new FileReader("day1.txt"));

        Counter counter = new Counter(input);

        int best = counter.getBest();
        assertEquals(4, best + 1);
        assertEquals(24000, counter.get(best));
        assertEquals(45000, counter.getBestThree());
    }

    @Test
    public void day1Actual() throws IOException {
        BufferedReader input = new BufferedReader(new FileReader("day1-actual.txt"));

        Counter counter = new Counter(input);

        int best = counter.getBest();
        assertEquals(66186, counter.get(best));
        assertEquals(196804, counter.getBestThree());
    }

    class Counter {
        private final ArrayList<Integer> counts;
        private final PriorityQueue<Integer> bests;
        private final int best;
        private final int bestThree;

        // Takes an input stream as the constructor input.
        // Parses during construction; build an array of elf contents.
        // Tracks the best elf
        public Counter(BufferedReader input) throws IOException {
            ArrayList<Integer> counts = new ArrayList<>();
            PriorityQueue<Integer> bests = new PriorityQueue<>(Collections.reverseOrder());

            // As we parse the file, we need to track the current best
            int bestIndex = 0;
            int bestValue = 0;

            int sum = 0;
            int i = 0;
            // Parse the file line by line; accumulate a total and spill which we reach a blank line.
            while (input.ready()) {
                String line = input.readLine();
                if (line.length() > 0) {
                    sum = sum + Integer.parseInt(line);
                } else {
                    counts.add(sum);
                    bests.add(sum);
                    if (sum > bestValue) {
                        bestIndex = i;
                        bestValue = sum;
                    }
                    sum = 0;
                    i++;
                }
            }

            // TODO don't like having this tail duplication
            counts.add(sum);
            bests.add(sum);
            if (sum > bestValue) {
                bestIndex = i;
            }

            this.counts = counts;
            this.best = bestIndex;
            this.bests = bests;

            sum = 0;
            i = 0;
            while (i < 3 && bests.size() > 0) {
                sum += bests.remove();
                i++;
            }
            this.bestThree = sum;
        }

        public int getBest() {
            return best;
        }

        public int getBestThree() {
            return bestThree;
        }

        public int get(int i) {
            return counts.get(i);
        }
    }

}
