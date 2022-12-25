import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Day12 {

    @Test
    public void day12() throws Exception {
        // Parse the file into a grid
        // then transform it into a graph of valid steps
        // then so shortests paths
        char[][] day12Grid = loadGrid("day12.txt");
        Map<Integer, List<Integer>> graph = makeGraphFrom(day12Grid);
        assertTrue(isConnected(graph, getElement(day12Grid, 'E'), getElement(day12Grid, 'E')));
        assertEquals(31, getShortestPathsFor(graph, getElement(day12Grid, 'E')).get(getElement(day12Grid, 'S')));

        char[][] day12ActualGrid = loadGrid("day12-actual.txt");
        assertTrue(isConnected(makeGraphFrom(day12ActualGrid), getElement(day12ActualGrid, 'S'), getElement(day12ActualGrid, 'E')));
        Map<Integer, Integer> day12ActualDistances = getShortestPathsFor(makeGraphFrom(day12ActualGrid), getElement(day12ActualGrid, 'E'));
        assertEquals(420, day12ActualDistances.get(getElement(day12ActualGrid, 'S')));
    }

    @Test
    public void day12part2() throws Exception {
        assertEquals(29, getBestDownTo(loadGrid("day12.txt")));
        assertEquals(414, getBestDownTo(loadGrid("day12-actual.txt")));
    }

    private int getBestDownTo(char[][] grid) {
        Map<Integer, Integer> distances = getShortestPathsFor(makeGraphFrom(grid), getElement(grid, 'E'));

        int best = Integer.MAX_VALUE;
        // Foreach elevation a in the grid look for a short path from the E
        for (int y = 0; y < grid.length; y++) {
            for (int x = 0; x < grid[0].length; x++) {
                char c = grid[y][x];
                if (c == 'a') {
                    int i = getIndexFor(y, x, grid);
                    if (distances.get(i) < best) {
                        best = distances.get(i);
                    }
                }
            }
        }
        return best;
    }

    private Map<Integer, Integer> getShortestPathsFor(Map<Integer, List<Integer>> graph, int start) {
        // Do a shortest path run
        // Prefill the distances maps
        Map<Integer, Integer> distanceTo = new HashMap<>();
        for (int i : graph.keySet()) {
            distanceTo.put(i, Integer.MAX_VALUE);
        }
        distanceTo.put(start, 0);

        // Priority queue of closes nodes
        Comparator<Integer> byDist = new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return Integer.compare(distanceTo.get(o1), distanceTo.get(o2));
            }
        };

        // Mark visited
        Set<Integer> settled = new HashSet<>();
        PriorityQueue<Integer> unsettledNodes = new PriorityQueue<>(byDist);
        unsettledNodes.add(start);

        while (!unsettledNodes.isEmpty()) {
            int current = unsettledNodes.poll();

            int distanceToCurrent = distanceTo.get(current);
            // Measure distance to our neighbours
            for (Integer neighbour : graph.get(current)) {
                if (!settled.contains(neighbour)) {
                    int neighbourDistanceFromCurrent = distanceToCurrent + 1;
                    if (distanceTo.get(neighbour) == Integer.MAX_VALUE) {
                        distanceTo.put(neighbour, neighbourDistanceFromCurrent);
                        unsettledNodes.offer(neighbour);
                    } else {
                        if (neighbourDistanceFromCurrent < distanceTo.get(neighbour)) {
                            distanceTo.put(neighbour, neighbourDistanceFromCurrent);
                            unsettledNodes.offer(neighbour);
                        }
                    }
                }
            }
            settled.add(current);
        }
        return distanceTo;
    }

    private boolean isConnected(Map<Integer, List<Integer>> graph, int start, int end) {
        // Check if a path exists from start to end with a BFS
        Set<Integer> visited = new HashSet<>();
        Queue<Integer> todo = new ArrayDeque<>();
        todo.offer(start);

        while (!todo.isEmpty()) {
            int current = todo.poll();
            if (current == end) {
                return true;
            }
            // Foreach adjacent node which has no been visited queue it
            for (Integer neighhbour : graph.get(current)) {
                if (!visited.contains(neighhbour) && !todo.contains(neighhbour))
                    todo.offer(neighhbour);
            }
            visited.add(current);
        }
        return false;
    }

    private int getElement(char[][] grid, char e) {
        int start = 0;
        for (int y = 0; y < grid.length; y++) {
            for (int x = 0; x < grid[0].length; x++) {
                if (grid[y][x] == e) {
                    start = getIndexFor(y, x, grid);
                }
            }
        }
        return start;
    }

    private Map<Integer, List<Integer>> makeGraphFrom(char[][] grid) {
        Map<Integer, List<Integer>> graph = new HashMap<>();
        int height = grid.length;
        int width = grid[0].length;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int key = getIndexFor(y, x, grid);

                List<int[]> neighbours = new ArrayList<>();
                if (y > 0) {
                    neighbours.add(new int[]{y - 1, x});
                }
                if (y < height - 1) {
                    neighbours.add(new int[]{y + 1, x});
                }
                if (x > 0) {
                    neighbours.add(new int[]{y, x - 1});
                }
                if (x < width - 1) {
                    neighbours.add(new int[]{y, x + 1});
                }

                char c = heightOf(y, x, grid);
                List<Integer> reachableNeighbours = new ArrayList<>();
                for (int[] neighbour : neighbours) {
                    char d = heightOf(neighbour[0], neighbour[1], grid);
                    int step = c - d;
                    if (step <= 1) {
                        reachableNeighbours.add(getIndexFor(neighbour[0], neighbour[1], grid));
                    }
                }
                graph.put(key, reachableNeighbours);
            }
        }
        return graph;
    }

    private char heightOf(int y, int x, char[][] grid) {
        char c = grid[y][x];
        if (c == 'S') {
            c = 'a';
        }
        if (c == 'E') {
            c = 'z';
        }
        return c;
    }

    private int getIndexFor(int y, int x, char[][] grid) {
        return (y * grid[0].length) + x;
    }

    private char[][] loadGrid(String filename) throws Exception {
        List<char[]> list = new ArrayList<>();
        BufferedReader input = new BufferedReader(new FileReader(filename));
        while (input.ready()) {
            String line = input.readLine();
            list.add(line.toCharArray());
        }
        char[][] grid = new char[list.size()][list.get(0).length];
        for (int i = 0; i < list.size(); i++) {
            grid[i] = list.get(i);
        }
        return grid;
    }
}
