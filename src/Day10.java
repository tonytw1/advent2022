import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Day10 {

    @Test
    public void day10() throws Exception {
        // Step through the input
        // After reading each line we will know the state for the next n cycles.
        // We can just push these onto an indexed list
        assertEquals(13140, evaluateX("day10.txt"));
        assertEquals(14360, evaluateX("day10-actual.txt"));
    }

    private int evaluateX(String filename) throws IOException {
        List<Integer> states = getStates(filename);
        int sum  = 0;
        for (int i = 0; i <= 220; i++) {
            int cycle = i + 1;
            if (((cycle + 20) % 40) == 0) {
                int score = states.get(i) * cycle;
                sum += score;
            }
        }
        return sum;
    }

    @Test
    public void day10part2() throws Exception {
        int screenWidth = 40;
        int screenHeight = 6;
        int numPixels = screenHeight * screenWidth;
        char[][] screen = new char[screenHeight][screenWidth];
        List<Integer> states = getStates("day10-actual.txt");
        for (int i = 0; i < numPixels; i++) {
            Integer state = states.get(i);
            int y = i / screenWidth;
            int x = i % screenWidth;
            boolean isPixelLite = (state >= x -1 && state <= x + 1);
            screen[y][x] = isPixelLite ? '*' : ' ';
        }
        renderScreen(screen);
    }

    private void renderScreen(char[][] screen) {
        for (char[] row : screen) {
            StringBuilder line = new StringBuilder();
            for (char c: row) {
                line.append(c);
            }
            System.out.println(line);
        }
    }

    private List<Integer> getStates(String filename) throws IOException {
        BufferedReader input = new BufferedReader(new FileReader(filename));
        List<Integer> states = new ArrayList<>();
        int x = 1;
        while(input.ready()) {
            String line = input.readLine();
            if (line.equals("noop")) {
                states.add(x);
            } else if (line.startsWith("addx ")) {
                int add = Integer.parseInt(line.split(" ")[1]);
                states.add(x);
                states.add(x);
                x = x + add;
            }
        }
        return states;
    }

}
