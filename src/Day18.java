import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Day18 {

    @Test
    public void day18() throws IOException {
        assertEquals(64L, getInternalAndExternalSurfaceAreaOf(parseFile("day18.txt")));
        assertEquals(4474L, getInternalAndExternalSurfaceAreaOf(parseFile("day18-actual.txt")));
    }

    @Test
    public void day18part2() throws IOException {
        assertEquals(58L, getExternalSurfaceArea(parseFile("day18.txt")));
        assertEquals(2518L, getExternalSurfaceArea(parseFile("day18-actual.txt")));
    }

    private long getExternalSurfaceArea(Set<Point> points) {
        // Discover the list of points discoverable from outside using a flood fill BFS;
        // use a bounding box with room for water to flow all around the points.
        // Count all surfaces visible to newly found water cells which interface with points.

        // Start a BSF from the corner if a box surrounding the points
        long minX = points.stream().map(p -> p.x).min(Long::compare).get();
        long maxX = points.stream().map(p -> p.x).max(Long::compare).get();
        long minY = points.stream().map(p -> p.y).min(Long::compare).get();
        long maxY = points.stream().map(p -> p.y).max(Long::compare).get();
        long minZ = points.stream().map(p -> p.z).min(Long::compare).get();
        long maxZ = points.stream().map(p -> p.z).max(Long::compare).get();

        Queue<Point> queue = new ArrayDeque<>();
        Point start = new Point(minX - 1, minY - 1, minZ - 1);
        Set<Point> visited = new HashSet<>();
        visited.add(start);
        queue.add(start);

        long surfaces = 0L;
        while (!queue.isEmpty()) {
            Point p = queue.poll();

            // List all the touching cells
            List<Point> touchingCells = new ArrayList<>();
            touchingCells.add(new Point(p.x - 1, p.y, p.z));
            touchingCells.add(new Point(p.x + 1, p.y, p.z));
            touchingCells.add(new Point(p.x, p.y - 1, p.z));
            touchingCells.add(new Point(p.x, p.y + 1, p.z));
            touchingCells.add(new Point(p.x, p.y, p.z - 1));
            touchingCells.add(new Point(p.x, p.y, p.z + 1));

            // Which are in bounds
            List<Point> adjPoints = touchingCells.stream().filter(pt -> pt.x >= minX - 1 && pt.x <= maxX + 1 &&
                    pt.y >= minY - 1 && pt.y <= maxY + 1 &&
                    pt.z >= minZ - 1 && pt.z <= maxZ + 1).collect(Collectors.toList());

            // For each adj cell queue unvisited water cells.
            for (Point adj : adjPoints) {
                if (!points.contains(adj)) {
                    if (!visited.contains(adj)) {
                        queue.offer(adj);
                        visited.add(adj);
                    }
                } else {
                    // The directly connected non water cells are connected by previously unseen surfaces
                    surfaces++;
                }
            }
        }
        return surfaces;
    }

    private long getInternalAndExternalSurfaceAreaOf(Set<Point> points) {
        // Knowing the extreme dimensions would be useful
        long minX = points.stream().map(p -> p.x).min(Long::compare).get();
        long maxX = points.stream().map(p -> p.x).max(Long::compare).get();
        long minY = points.stream().map(p -> p.y).min(Long::compare).get();
        long maxY = points.stream().map(p -> p.y).max(Long::compare).get();
        long minZ = points.stream().map(p -> p.z).min(Long::compare).get();
        long maxZ = points.stream().map(p -> p.z).max(Long::compare).get();

        long sum = 0;
        // Taking scans we should be able to detect all surfaces
        // TODO think of a pattern to deduplicate these blocks?
        // z scan
        for (long x = minX - 1; x <= maxX + 1; x++) {
            for (long y = minY - 1; y <= maxY + 1; y++) {
                boolean inVolume = false;
                // At x, y sink a core through the z axies;
                for (long z = minZ - 1; z <= maxZ + 1; z++) {
                    if (points.contains(new Point(x, y, z)) != inVolume) {
                        // Each change must have been an edge
                        inVolume = !inVolume;
                        sum++;
                    }
                }
            }
        }
        // y scan
        for (long x = minX - 1; x <= maxX + 1; x++) {
            for (long z = minZ - 1; z <= maxZ + 1; z++) {
                boolean inVolume = false;
                for (long y = minY - 1; y <= maxY + 1; y++) {
                    if (points.contains(new Point(x, y, z)) != inVolume) {
                        // Each change must have been an edge
                        inVolume = !inVolume;
                        sum++;
                    }
                }
            }
        }
        // x scan
        for (long y = minY - 1; y <= maxY + 1; y++) {
            for (long z = minZ - 1; z <= maxZ + 1; z++) {
                boolean inVolume = false;
                for (long x = minX - 1; x <= maxX + 1; x++) {
                    if (points.contains(new Point(x, y, z)) != inVolume) {
                        // Each change must have been an edge
                        inVolume = !inVolume;
                        sum++;
                    }
                }
            }
        }
        return sum;
    }

    private Set<Point> parseFile(String filename) throws IOException {
        Set<Point> points = new HashSet<>();
        BufferedReader input = new BufferedReader(new FileReader(filename));
        while (input.ready()) {
            String[] line = input.readLine().split(",");
            long x = Long.parseLong(line[0]);
            long y = Long.parseLong(line[1]);
            long z = Long.parseLong(line[2]);
            points.add(new Point(x, y, z));
        }
        return points;
    }

    class Point {
        final long x, y, z;

        public Point(long x, long y, long z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Point point = (Point) o;
            return x == point.x && y == point.y && z == point.z;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y, z);
        }
    }
}
