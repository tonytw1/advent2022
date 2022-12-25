import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Day8 {

    @Test
    public void day8() throws Exception {
        // Read the input into a array
        assertEquals(21, findVisible(loadGrid("day8.txt", 5)));
        assertEquals(1787, findVisible(loadGrid("day8-actual.txt", 99)));
    }

    @Test
    public void day8part2() throws Exception {
        assertEquals(8, findBestScenicScore(loadGrid("day8.txt", 5)));
        assertEquals(440640, findBestScenicScore(loadGrid("day8-actual.txt", 99)));
    }

    private int findBestScenicScore(int[][] grid) {
        int best = 0;
        int n = grid.length;
        for (int y = 0; y < n; y++) {
            for (int x = 0; x < n; x++) {
                // For each point in the grid get the sight lines from that point and score them
                List<List<Integer>> sightLines = getSightLinesFrom(grid, y, x);
                int sum = 1;
                for (List<Integer> sightLine : sightLines) {
                    int i = scoreSightLine(grid[y][x], sightLine.subList(1, sightLine.size()));
                    sum = sum * i;
                }
                if (sum > best) {
                    best = sum;
                }
            }
        }
        return best;
    }

    private List<List<Integer>> getSightLinesFrom(int[][] grid, int y, int x) {
        int n = grid.length;
        List<List<Integer>> sightLines = new ArrayList<>();
        for (int d = 0; d < 4; d++) {   // Direction
            int ya = y;
            int xa = x;
            List<Integer> sightLine = new ArrayList<>();
            while (ya >= 0 && ya < n && xa >= 0 && xa < n) {
                sightLine.add(grid[ya][xa]);
                if (d == 0) {
                    xa ++;
                } else if (d == 1) {
                    ya ++;
                } else if (d == 2) {
                    xa --;
                } else {
                    ya --;
                }
            }
            sightLines.add(sightLine);
        }
        return sightLines;
    }

    private int scoreSightLine(int h, List<Integer> sightLine) {
        int score = 0;
        for (Integer tree: sightLine) {
            score++;
            if (tree >= h) {
                return score;
            }
        }
        return score;
    }

    private int findVisible(int[][] grid) {
        int n = grid.length;

        // We are probably going to want a registry of visible trees
        Set<String> visible = new HashSet<>();

        // Scan the grid from each direction

        // Left to right
        for (int y = 1; y < (n - 1); y++) {
            int h = grid[y][0];
            for (int x = 1; x < (n - 1); x++) {
                int t = grid[y][x];
                if (t > h) {
                    markAsVisible(visible, y, x);
                    h = t;
                }
            }
        }

        // Top to bottom
        for (int x = 1; x < (n - 1); x++) {
            int h = grid[0][x];
            for (int y = 1; y < (n - 1); y++) {
                int t = grid[y][x];
                if (t > h) {
                    markAsVisible(visible, y, x);
                    h = t;
                }
            }
        }

        // Right to left
        for (int y = 1; y < (n - 1); y++) {
            int h = grid[y][n - 1];
            for (int x = n - 2; x > 0; x--) {
                int t = grid[y][x];
                if (t > h) {
                    markAsVisible(visible, y, x);
                    h = t;
                }
            }
        }

        // Bottom to top
        for (int x = 1; x < (n - 1); x++) {
            int h = grid[n - 1][x];
            for (int y = n - 2; y > 0; y--) {
                int t = grid[y][x];
                if (t > h) {
                    markAsVisible(visible, y, x);
                    h = t;
                }
            }
        }
        return visible.size() + (2 * n) + (2 * (n - 2));
    }

    private boolean markAsVisible(Set<String> visible, int y, int x) {
        return visible.add(y + "," + x);
    }

    private int[][] loadGrid(String fileName, int n) throws IOException {
        int[][] grid = new int[n][n];
        BufferedReader input = new BufferedReader(new FileReader(fileName));
        for (int y = 0; y < n; y++) {
            String line = input.readLine();
            for (int x = 0; x < n; x++) {
                grid[y][x] = Integer.parseInt(Character.toString(line.charAt(x)));
            }
        }
        return grid;
    }

}
