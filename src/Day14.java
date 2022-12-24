import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Day14 {

    @Test
    public void day14() throws Exception {
        // Parse the file and build the rock formations in memory.
        // We only need to be able to tell if a cell is occupied so a set of occupied point is fine

        // Drop rocks one after the other; a single rock is complete when it comes to rest or breaches the lower limit
        assertEquals(24, simulate(loadFile("day14.txt")));
        assertEquals(715, simulate(loadFile("day14-actual.txt")));

        // Part 2
        assertEquals(93, simulatePart2(loadFile("day14.txt")));
        assertEquals(25248, simulatePart2(loadFile("day14-actual.txt")));
    }

    private int simulate(Set<Point> grid) {
        final long base = getBaseOf(grid);

        int r = 0;
        Point rock = new Point(500, 0);
        while (rock.y < base) {
            rock = new Point(500, 0);
            r++;

            boolean atRest = false;
            while (!atRest && rock.y < base) {
                // TODO you could probably go straight to this answer without iterating through
                //  each step in the fall by maintaining the heights of each column
                List<Point> possiblePlacements = new ArrayList<>();
                possiblePlacements.add(new Point(rock.x, rock.y + 1));
                possiblePlacements.add(new Point(rock.x - 1, rock.y + 1));
                possiblePlacements.add(new Point(rock.x + 1, rock.y + 1));

                // Take for first available placement
                atRest = true;
                for (Point possiblePlacement : possiblePlacements) {
                    if (!grid.contains(possiblePlacement)) {
                        rock = possiblePlacement;
                        atRest = false;
                        break;
                    }
                }
            }
            grid.add(rock);
        }
        return r - 1;
    }

    private int simulatePart2(Set<Point> grid) {
        final long base = getBaseOf(grid);
        final long floor = base + 2;

        int r = 0;
        while (!grid.contains(new Point(500, 0))) {
            Point rock = new Point(500, -10);
            r++;

            boolean atRest = false;
            while (!atRest) {
                List<Point> possiblePlacements = new ArrayList<>();
                possiblePlacements.add(new Point(rock.x, rock.y + 1));
                possiblePlacements.add(new Point(rock.x - 1, rock.y + 1));
                possiblePlacements.add(new Point(rock.x + 1, rock.y + 1));

                // Take for first available placement
                atRest = true;
                for (Point possiblePlacement : possiblePlacements) {
                    boolean isFree = !grid.contains(possiblePlacement) && possiblePlacement.y < floor;
                    if (isFree) {
                        rock = possiblePlacement;
                        atRest = false;
                        break;
                    }
                }
            }
            grid.add(rock);
        }
        return r;
    }

    private long getBaseOf(Set<Point> grid) {
        return grid.stream().map(point -> point.y).max(Long::compare).orElse(0L);
    }

    private Set<Point> loadFile(String filename) throws Exception {
        Set<Point> grid = new HashSet<>();
        BufferedReader input = new BufferedReader(new FileReader(filename));
        while (input.ready()) {
            // Each trace is on a single line of separate point pairs
            String line = input.readLine();
            String[] pairs = line.split(" -> ");

            List<Point> wayPoints = Arrays.stream(pairs).map(pair -> {
                        List<Long> coords = Arrays.stream(pair.split(",")).map(Long::parseLong).collect(Collectors.toList());
                        return new Point(coords.get(0), coords.get(1));
                    }
            ).collect(Collectors.toList());

            // Foreach waypoint, trace out the points to the next
            for (int i = 0; i < wayPoints.size() - 1; i++) {
                Point from = wayPoints.get(i);
                Point to = wayPoints.get(i + 1);
                Point cursor = new Point(from.x, from.y);
                grid.add(cursor);
                while (!cursor.equals(to)) {
                    // Move the cursor
                    if (to.x > from.x) {
                        cursor = new Point(cursor.x + 1, cursor.y);
                    } else if (to.x < from.x) {
                        cursor = new Point(cursor.x - 1, cursor.y);
                    } else if (to.y > from.y) {
                        cursor = new Point(cursor.x, cursor.y + 1);
                    } else if (to.y < from.y) {
                        cursor = new Point(cursor.x, cursor.y - 1);
                    }
                    grid.add(cursor);
                }
            }
        }

        return grid;
    }

    private void renderGrid(Set<Point> grid) {
        long base = getBaseOf(grid);
        for (long y = 0; y <= base; y++) {
            StringBuilder line = new StringBuilder();
            for (long x = 420; x < 520; x++) {
                char c = '.';
                if (grid.contains(new Point(x, y))) {
                    c = '#';
                }
                line.append(c);
            }
            System.out.println(line);
        }
    }

    class Point {
        final long x;
        final long y;

        public Point(long x, long y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Point point = (Point) o;
            return x == point.x && y == point.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }

        @Override
        public String toString() {
            return "Point{" +
                    "x=" + x +
                    ", y=" + y +
                    '}';
        }
    }
}
