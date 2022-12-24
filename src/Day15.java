import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Day15 {

    @Test
    public void day15() throws Exception {
        // parse the input; collect list of sensors
        assertEquals(26, sweep(parseFile("day15.txt"), 10));
        assertEquals(5299855, sweep(parseFile("day15-actual.txt"), 2000000));
    }

    @Test
    public void day15part2() throws Exception {
        assertEquals(56000011, bruteForcePart2(parseFile("day15.txt"), 20));
        assertEquals(56000011, overlapsPart2(parseFile("day15.txt"), 20));
        assertEquals(13615843289729L, overlapsPart2(parseFile("day15-actual.txt"), 4000000));
    }

    private long overlapsPart2(List<Sensor> sensors, long max) {
        long sum = 0;
        for (long y = 0; y <= max; y++) {
            // For this row, evaluate each sensor's horizontal range at this row
            List<long[]> ranges = new ArrayList<>();
            for (Sensor sensor: sensors) {
                long deltaY = Math.abs(y - sensor.y);
                long xRearch = sensor.range - deltaY;   // How much of this sensor's range is available to reach along the x axis
                long right = sensor.x + xRearch;
                if (xRearch >= 0 && right >= 0) {
                    ranges.add(new long[]{sensor.x - xRearch, right});
                }
            }

            // Sort by left most first
            Collections.sort(ranges, Comparator.comparingLong(o -> o[0]));

            // Merge all the ranges into the best continuous extension from the left most range we can find.
            long left = ranges.get(0)[0];
            long right = ranges.get(0)[1];
            for (long[] range: ranges) {
                if (range[0] <= right + 1) {
                     if (range[1] > right) {
                         right = range[1];
                     }
                }
            }

            boolean isCompletelyCovered = left <= 0 && right >= max;
            if (!isCompletelyCovered) {
                return ((right + 1) * 4000000) + y;
            }
        }
        throw new RuntimeException();
    }

    private long bruteForcePart2(List<Sensor> sensors, long max) {
        for (long y = 0; y <= max; y++) {
            for (long x = 0; x <= max; x++) {
                boolean inRange = false;
                for (Sensor sensor : sensors) {
                    long deltaX = Math.abs(x - sensor.x);
                    long deltaY = Math.abs(y - sensor.y);
                    long delta = deltaY + deltaX;
                    if (delta <= sensor.range) {
                        inRange = true;
                    }
                }
                if (!inRange) {
                    long freq = (x * 4000000) + y;
                    return freq;
                }
            }
        }
        throw new RuntimeException();
    }

    private long sweep(List<Sensor> sensors, long y) {
        // Find the x range
        long left = sensors.stream().map(s -> {return s.x - s.range;}).min(Long::compare).get();
        long right = sensors.stream().map(s -> {return s.x + s.range;}).max(Long::compare).get();
        long sum = 0;
        for (long x = left; x <= right; x++) {
            boolean inRange = isInRange(sensors, y, x);
            if (inRange) {
                sum++;
            }
        }
        return sum;
    }

    private boolean isInRange(List<Sensor> sensors, long y, long x) {
        // Are any sensors in range?
        boolean inRange = false;
        for (Sensor sensor: sensors) {
            long delta = Math.abs(y - sensor.y) + Math.abs(x - sensor.x);
            boolean isClosestBeaconToThisSensor = x == sensor.closedBeaconX && y == sensor.getClosedBeaconY;
            if (delta <= sensor.range && !isClosestBeaconToThisSensor) {
                inRange = true;
                break;
            }
        }
        return inRange;
    }

    private List<Sensor> parseFile(String filename) throws Exception {
        List<Sensor> sensors = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        while(reader.ready()) {
            String numbers = reader.readLine().replaceAll("[a-z|A-z|=|,|:]", "").replaceAll("\\s+", " ").trim();
            String[] split = numbers.split(" ");

            long x = Long.parseLong(split[0]);
            long y = Long.parseLong(split[1]);
            long cbx = Long.parseLong(split[2]);
            long cby = Long.parseLong(split[3]);

            sensors.add(new Sensor(x, y, cbx, cby));
        }
        return sensors;

    }

    class Sensor {
        final long x, y;
        final long closedBeaconX, getClosedBeaconY;
        final long range;

        public Sensor(long x, long y, long closedBeaconX, long getClosedBeaconY) {
            this.x = x;
            this.y = y;
            this.closedBeaconX = closedBeaconX;
            this.getClosedBeaconY = getClosedBeaconY;

            // Calculate our distance to closest beacon; this is our effective max range
            this.range = Math.abs(closedBeaconX - x) + Math.abs(getClosedBeaconY -y);
        }
    }
}
