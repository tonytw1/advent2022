import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Day3 {

    @Test
    public void day3() throws Exception {
        assertEquals(157, getSumFor("day3.txt"));
        assertEquals(8515, getSumFor("day3-actual.txt"));
    }

    @Test
    public void day3Part2() throws Exception {
        assertEquals(70, getBadgesSumFor("day3.txt"));
        assertEquals(2434, getBadgesSumFor("day3-actual.txt"));
    }

    private int getBadgesSumFor(String filename) throws IOException {
        // Iterate the file
        // For every 3 lines set up a count map to count how many lines each char appears in.
        // After the 3rd line, check this map; there should be exactly 1 key with a value of 3.
        List<Character> badges = new ArrayList<>();
        int i = 1;
        Map<Character, Integer> counts = new HashMap<>();
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        while (reader.ready()) {
            // Read each line
            String line = reader.readLine();
            Set<Character> uniqueInLine = new HashSet<>();
            for (char c : line.toCharArray()) {
                uniqueInLine.add(c);
            }
            for (char c : uniqueInLine) {
                int s = counts.getOrDefault(c, 0);
                counts.put(c, s + 1);
            }
            i++;

            if (i > 3) {
                Character badge = null;
                for (char k : counts.keySet()) {
                    if (counts.get(k) == 3) {
                        badge = k;
                    }
                }
                badges.add(badge);
                counts = new HashMap<>();
                i = 1;
            }
        }

        // Sum up the priorities of these badges
        return badges.stream().map(this::priorityOf).mapToInt(c -> c).sum();
    }

    private int getSumFor(String filename) throws IOException {
        // Push these to a set
        // Sum up their scores
        List<Character> duplicates = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        while (reader.ready()) {
            // Read each line
            String line = reader.readLine();
            // Split into left and right compartments
            int centerIndex = line.length() / 2;
            String left = line.substring(0, centerIndex);
            String right = line.substring(centerIndex);

            // Load the left compartment into a set
            Set<Character> leftContents = new HashSet<>();
            for (char c : left.toCharArray()) {
                leftContents.add(c);
            }

            // Detect which members of the right compartment are in the set
            Set<Character> lineDuplicates = new HashSet<>();
            for (char c : right.toCharArray()) {
                if (leftContents.contains(c)) {
                    lineDuplicates.add(c);
                }
            }
            duplicates.addAll(lineDuplicates);
        }

        return duplicates.stream().map(this::priorityOf).mapToInt(c -> c).sum();
    }

    public int priorityOf(char c) {
        if (c <= 'z' && c >= 'a') {
            return (c - 'a') + 1;
        }
        return (c - 'A') + 27;
    }

}
