import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Day6 {

    @Test
    public void day6() throws Exception {
        assertEquals(7, getStartOfMessageOffset("mjqjpqmgbljsphdztnvjfqwrcgsmlb", 4));
        assertEquals(5, getStartOfMessageOffset("bvwbjplbgvbhsrlpgdmjqwftvncz", 4));
        assertEquals(6, getStartOfMessageOffset("nppdvjthqldpwncqszvftbrmjlhg", 4));
        assertEquals(10, getStartOfMessageOffset("nznrnfrfntjfmvfwmzdfjlvtqnbhcprsg", 4));
        assertEquals(11, getStartOfMessageOffset("zcfzfwzzqfrljwzlrfnpqdbhtmscgvjw", 4));
        assertEquals(1093, getStartOfMessageOffset(fromFile("day6-actual.txt"), 4));

        assertEquals(19, getStartOfMessageOffset("mjqjpqmgbljsphdztnvjfqwrcgsmlb", 14));
        assertEquals(3534, getStartOfMessageOffset(fromFile("day6-actual.txt"), 14));
    }

    private Integer getStartOfMessageOffset(String line, int n) {
        for (int i = 0; i < line.length() - n; i++) {
            Set<Character> seen = new HashSet<>();
            for (int j = 0; j < n; j++) {
                seen.add(line.charAt(i + j));
            }
            if (seen.size() == n) {
                return i + n;
            }
        }
        return null;
    }

    private String fromFile(String filename) throws IOException {
        BufferedReader lines = new BufferedReader(new FileReader(filename));
        return lines.readLine();
    }

}
