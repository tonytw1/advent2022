import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Day9 {

    @Test
    public void day9() throws Exception {
        assertEquals(13, countTailVisits(loadMovesFile("day9.txt"), 2));
        assertEquals(5779, countTailVisits(loadMovesFile("day9-actual.txt"), 2));

        assertEquals(1, countTailVisits(loadMovesFile("day9.txt"), 10));
        assertEquals(36, countTailVisits(loadMovesFile("day9-part2.txt"), 10));

        assertEquals(2331, countTailVisits(loadMovesFile("day9-actual.txt"), 10));
    }

    @Test
    public void shouldFollow() {
        assertEquals(3, follow(4, 2, 2, 2)[0]);
        assertEquals(2, follow(4, 2, 2, 2)[1]);

        assertEquals(2, follow(2, 2, 2, 4)[0]);
        assertEquals(3, follow(2, 2, 2, 4)[1]);

        assertEquals(3, follow(3, 4, 2, 2)[0]);
        assertEquals(3, follow(3, 4, 2, 2)[1]);

        assertEquals(3, follow(4, 3, 2, 2)[0]);
        assertEquals(3, follow(4, 3, 2, 2)[1]);

        assertEquals(-3, follow(-4, 2, -2, 2)[0]);
        assertEquals(2, follow(-4, 2, -2, 2)[1]);

        assertEquals(2, follow(2, -2, 2, -4)[0]);
        assertEquals(-3, follow(2, -2, 2, -4)[1]);

        assertEquals(2, follow(2, -1, 1, 1)[0]);
        assertEquals(0, follow(2, -1, 1, 1)[1]);

        assertEquals(0, follow(-1, 1, 1, 0)[0]);
        assertEquals(1, follow(-1, 1, 1, 0)[1]);

        // TODO dx = 2, dy =2 is possible
    }

    private int countTailVisits(List<Character> moves, int n) {
        // Step through each move, moving the head
        // then letting the tail react.
        // We'll need to track the positions of the head and tail.

        // Setup our knows
        int[][] knots = new int[n][2];
        for (int i = 0; i < n; i++) {
            knots[i] = new int[]{1, 1};
        }
        // And we want to record all the locations the tail visited; potentially unbounded coordinates so use a set
        Set<String> tailVisits = new HashSet<>();

        for (char move : moves) {
            // Move the head know
            int hx = knots[0][0];
            int hy = knots[0][1];
            if (move == 'R') {
                hx++;
            } else if (move == 'L') {
                hx--;
            } else if (move == 'U') {
                hy++;
            } else if (move == 'D') {
                hy--;
            }
            knots[0][0] = hx;
            knots[0][1] = hy;

            // Tail knots react in turn
            for (int i = 1; i < n; i++) {
                hx = knots[i - 1][0];
                hy = knots[i - 1][1];
                int tx = knots[i][0];
                int ty = knots[i][1];
                // How far away is the tail
                int[] tailLocation = follow(hx, hy, tx, ty);
                knots[i] = tailLocation;
            }

            // Record the last tails location
            int[] lastKnot = knots[n - 1];
            int tx = lastKnot[0];
            int ty = lastKnot[1];
            tailVisits.add(tx + "," + ty);
        }

        return tailVisits.size();
    }

    private int[] follow(int hx, int hy, int tx, int ty) {
        int dx = hx - tx;
        int dy = hy - ty;

        // Straight moves
        if (dx == 2 && dy == 0) {
            tx++;
        } else if (dx == -2 && dy == 0) {
            tx--;
        } else if (dy == 2 && dx == 0) {
            ty++;
        } else if (dy == -2 && dx == 0) {
            ty--;

        } else if (dy == 2 && dx == 2) {
            tx++;
            ty++;

        } else if (dy == -2 && dx == 2) {
            tx++;
            ty--;

        } else if (dx == -2 && dy == 2) {
            tx--;
            ty++;

        } else if (dx == -2 && dy == -2) {
            tx--;
            ty--;

        } else if (dx == 2) {
            tx++;
            ty = hy;

        } else if (dx == -2) {
            tx--;
            ty = hy;

        } else if (dy == 2) {
            ty++;
            tx = hx;

        } else if (dy == -2) {
            ty--;
            tx = hx;
        }

        return new int[]{tx, ty};
    }

    private List<Character> loadMovesFile(String filename) throws IOException {
        // Parse the file and expand it into a sequence of single step moves
        List<Character> moves = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        while (reader.ready()) {
            String line = reader.readLine();
            char dir = line.charAt(0);
            int num = Integer.parseInt(line.split(" ")[1]);
            for (int i = 0; i < num; i++) {
                moves.add(dir);
            }
        }
        return moves;
    }

}
