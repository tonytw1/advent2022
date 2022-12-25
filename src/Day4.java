import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Day4 {

    @Test
    public void day4() throws Exception {
        assertEquals(2, countFullyOverlappingPairs(parsePairs("day4.txt")));
        assertEquals(657, countFullyOverlappingPairs(parsePairs("day4-actual.txt")));
    }

    @Test
    public void day4part2() throws Exception {
        assertEquals(4, countOverlappingPairs(parsePairs("day4.txt")));
        assertEquals(938, countOverlappingPairs(parsePairs("day4-actual.txt")));
    }

    private int countOverlappingPairs(List<List<int[]>> pairs) {
        // For each pair, look for those where the  ranges overlap
        int sum = 0;
        for (List<int[]> pair : pairs) {
            if (overlaps(pair.get(0), pair.get(1))) {
                sum++;
            }
        }
        return sum;
    }

    private int countFullyOverlappingPairs(List<List<int[]>> pairs) {
        // For each pair, look for those where one range cover the over
        int sum = 0;
        for (List<int[]> pair : pairs) {
            if (covers(pair.get(0), pair.get(1)) ||
                    covers(pair.get(1), pair.get(0))) {
                sum++;
            }
        }
        return sum;
    }

    private List<List<int[]>> parsePairs(String fileName) throws IOException {
        BufferedReader input = new BufferedReader(new FileReader(fileName));
        List<List<int[]>> pairs = new ArrayList<>();
        while (input.ready()) {
            String line = input.readLine();
            // Split the line and parse into ranges
            List<int[]> ranges = new ArrayList<>();
            for (String rangeString : line.split(",")) {
                List<Integer> collect = Arrays.stream(rangeString.split("-")).map(Integer::parseInt).collect(Collectors.toList());
                ranges.add(new int[]{collect.get(0), collect.get(1)});
            }
            pairs.add(ranges);
        }
        return pairs;
    }

    private boolean covers(int[] a, int[] b) {
        return (a[0] <= b[0] && a[1] >= b[1]);
    }

    private boolean overlaps(int[] a, int[] b) {
        if (a[0] < b[0]) {
            return b[0] <= a[1];
        }
        return a[0] <= b[1];
    }

}
