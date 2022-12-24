import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class Day17 {

    private static final long FLOOR = 1L;
    private static final long WIDTH = 7L;
    private static final long NEW_ROCK_CLEAR_ROWS = 3L;
    private static final long NEW_ROCK_LEFT_EDGE_UNITS_FROM_WALL = 2L;

    Map<Long, Long> heights = new HashMap<>();

    @Test
    public void day17() throws Exception {
        assertEquals(3068, simulate("day17.txt", 2022));
        assertEquals(3157, simulate("day17-actual.txt", 2022));
    }

    //@Test
    public void day17part2() throws Exception {
        long requestedDrops = 1000000000000L;

        Set<Point> points = buildStack("day17-actual.txt", 10000);
        Long maxHeight = getMaxHeight(points);
        System.out.println("Stack height is: " + maxHeight);
        List<Integer> integers = renderToEncoded(points);
        System.out.println("Rendered height is: " + integers.size());

        System.out.println(integers);
        // Brute force look for repeating interval
        Integer interval = 2728;    // TODO not repeatable for toDrop > 5000!?
        /*
        for (int i = 10; i < 5000; i++) {
            List<Integer> a = integers.subList(0, i);
            List<Integer> b = integers.subList(i, i + i);
            if (a.equals(b)) {
                System.out.println("Stack repeats every " + i + " rows");
                interval = i;
                break;
            }
        }
        */
        assertNotNull(interval, "No repeat found");

        // Now try to determine how many rocks are in the repeating rows
        // Stable number of rocks will give a repeatable height between 2 and 3rd repeats; the first repeat is not stable
        Long rockAtStartOfSecondRepeat = heights.get(interval * 2L);
        Long rockAtStartOfThirdRepeat = heights.get(interval * 3L);
        Long rockRepeat = rockAtStartOfThirdRepeat - rockAtStartOfSecondRepeat;
        System.out.println("Stack repeats every " + rockRepeat + " rocks");

        long numOfRepeatsRequired = requestedDrops / rockRepeat;
        long remainingRocks = requestedDrops % rockRepeat;
        System.out.println(numOfRepeatsRequired + " whole repeats in the big stack with remaining " + remainingRocks);

        // How high is this many repeats?
        long heightOfRepeats = numOfRepeatsRequired * interval;
        // and the remaining rocks?
        long heightOfReaminder = 0L;
        for (long height : heights.keySet()) {
            if (heights.get(height) == remainingRocks) {
                heightOfReaminder = height;
            }
        }
        //assertEquals(1514285714288L, heightOfRepeats + heightOfReaminder);
        assertEquals(1581449275319L, heightOfRepeats + heightOfReaminder);
    }

    private long simulate(String filename, long toDrop) throws Exception {
        Set<Point> fixed = buildStack(filename, toDrop);
        render(fixed);
        return getMaxHeight(fixed);
    }

    private Set<Point> buildStack(String filename, long toDrop) throws Exception {
        Queue<Integer> wind = parseFile(filename);
        Queue<Sprite> sprites = makeSprites();

        Rock rock = null;
        boolean isStruck = false;
        Set<Point> fixed = new HashSet<>();
        long dropped = 1;
        while (!wind.isEmpty() && dropped <= toDrop) {
            if (rock == null || isStruck) {
                Sprite sprite = sprites.poll();
                sprites.offer(sprite);

                long maxHeight = getMaxHeight(fixed);

                long y = maxHeight + sprite.getHeight() + NEW_ROCK_CLEAR_ROWS;
                rock = new Rock(NEW_ROCK_LEFT_EDGE_UNITS_FROM_WALL, y, sprite);
                isStruck = false;

                Set<Point> snapshot = new HashSet<>(fixed);
                snapshot.addAll(rock.sprite.at(rock.x, rock.y));
                //render(snapshot);
            }

            // Adjust the sprites x position of possible.
            int gust = wind.poll();
            wind.offer(gust);

            long proposedX = rock.x + gust;
            // Generate the set of pixels this rock would not like to occupy
            Set<Point> wantToOccupy = rock.sprite.at(proposedX, rock.y);

            // Confirm these pixels are unoccupied
            boolean isHorizontalMoveOk = wantToOccupy.stream().allMatch( p -> p.x >= 0 && p.x <= (WIDTH - 1) && !fixed.contains(p))  ;
            if (isHorizontalMoveOk) {
                rock.x = proposedX;
            }

            // Then try to move down
            long proposedY = rock.y - 1;
            wantToOccupy = rock.sprite.at(rock.x, proposedY);
            boolean isVerticalMoveOk = wantToOccupy.stream().allMatch( p -> p.y >= FLOOR && !fixed.contains(p));
            if (isVerticalMoveOk) {
                rock.y = proposedY;

            } else {
                // This rock is in its final position; burn these rock's pixels into the fixed points
                fixed.addAll(rock.sprite.at(rock.x, rock.y));
                isStruck = true;
                Long maxHeight = getMaxHeight(fixed);
                heights.putIfAbsent(maxHeight, dropped);
                dropped++;
            }
        }
        return fixed;
    }

    private void render(Set<Point> fixed) {
        for (long y = getMaxHeight(fixed); y >= FLOOR; y --) {
            StringBuilder line = new StringBuilder();
            for(long x = 0; x < WIDTH; x++) {
                char c = '.';
                if (fixed.contains(new Point(x, y))) {
                    c = '*';
                }
                line.append(c);
            }
            System.out.println(line);
        }
    }

    private List<Integer> renderToEncoded(Set<Point> fixed) {
        List<Integer> encoded = new ArrayList<>();
        // Render the stack is a sequence of sprite encoded numbers;
        // there is probably a repeating pattern give the improbably large part 2 interactions count.
        for (long y = getMaxHeight(fixed); y >= FLOOR; y --) {
            int sum = 0;
            for(long x = 0; x < WIDTH; x++) {
                if (fixed.contains(new Point(x, y))) {
                    sum += Math.pow(x + 1, 2);
                }
            }
            encoded.add(sum);
        }
        return encoded;
    }

    private Long getMaxHeight(Set<Point> fixed) {
        return fixed.stream().map(p -> p.y).max(Long::compare).orElse(0L);
    }

    private Queue<Integer> parseFile(String filename) throws Exception {
        ArrayDeque<Integer> queue = new ArrayDeque<>();
        BufferedReader input = new BufferedReader(new FileReader(filename));
        while(input.ready()) {
            char read = (char) input.read();
            int delta = ' ';
            if (read == '>') {
                delta = 1;
            } else if (read == '<') {
                delta = -1;
            } else {
                throw new RuntimeException("Invalid char: " + read);
            }
            queue.add(delta);
        }
        return queue;
    }

    class Rock {
        long x, y;
        Sprite sprite;

        public Rock(long x, long y, Sprite sprite) {
            this.x = x;
            this.y = y;
            this.sprite = sprite;
        }
    }

    class Sprite {
        final Set<Point> pixels;

        public Sprite(Set<Point> pixels) {
            this.pixels = pixels;
        }

        public long getHeight() {
            List<Long> ys = pixels.stream().map(p -> p.y).collect(Collectors.toList());
            return ys.stream().max(Long::compare).get() - ys.stream().min(Long::compare).get() + 1;
        }
        public Set<Point> at(long x, long y) {
            return pixels.stream().map(p -> new Point(p.x + x,  y - p.y)).collect(Collectors.toSet());
        }
    }

    private Queue<Sprite> makeSprites() {
        Queue<Sprite> sprites = new ArrayDeque<>();

        Set<Point> horizontal = new HashSet<>();
        horizontal.add(new Point(0, 0));
        horizontal.add(new Point(1, 0));
        horizontal.add(new Point(2, 0));
        horizontal.add(new Point(3, 0));
        sprites.add(new Sprite(horizontal));

        Set<Point> cross = new HashSet<>();
        cross.add(new Point(1, 0));
        cross.add(new Point(0, 1));
        cross.add(new Point(1, 1));
        cross.add(new Point(2, 1));
        cross.add(new Point(1, 2));
        sprites.add(new Sprite(cross));


        Set<Point> angle = new HashSet<>();
        angle.add(new Point(2, 0));
        angle.add(new Point(2, 1));
        angle.add(new Point(2, 2));
        angle.add(new Point(1, 2));
        angle.add(new Point(0, 2));
        sprites.add(new Sprite(angle));

        Set<Point> vertical = new HashSet<>();
        vertical.add(new Point(0, 0));
        vertical.add(new Point(0, 1));
        vertical.add(new Point(0, 2));
        vertical.add(new Point(0, 3));
        sprites.add(new Sprite(vertical));

        Set<Point> square = new HashSet<>();
        square.add(new Point(0, 0));
        square.add(new Point(1, 0));
        square.add(new Point(0, 1));
        square.add(new Point(1, 1));
        sprites.add(new Sprite(square));

        return sprites;
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
