import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Day16 {

    public static final int TIME_LIMIT = 30;
    Map<String, Integer> flowRates; // TODO better data structure for network of valves
    Map<String, Map<String, Integer>> distances;
    String best;
    int bestScore = 0;

    @Test
    public void day16() throws Exception {
        // Parse out file into an adj graph
        flowRates = new HashMap<>();
        assertEquals(1651, findBestOutcomeFor(parseGraph("day16.txt")));
        assertEquals(2077, findBestOutcomeFor(parseGraph("day16-actual.txt")));
    }

    private int findBestOutcomeFor(Map<String, List<String>> graph) {
        // Knowing the distances between all the nodes is will let us know the time cost valves in any order
        distances = measureDistancesBetweenNodes(graph);

        // We are never going to need to go more than 15 deep so a DSF through all the possible moves might work
        Set<String> valves = new HashSet<>(graph.keySet());
        // It's not worth opening valves with no flow rate so we can discard then from the search
        Set<String> valvesToOpen = valves.stream().filter(valve -> flowRates.get(valve) > 0).collect(Collectors.toSet());

        visit("AA", valvesToOpen, 0, new ArrayList<>(), new HashMap<>());
        return bestScore;
    }

    private void visit(String position, Set<String> valvesNotYetOpened, int time, List<String> opened, Map<String, Integer> log) {
        boolean isFinished = valvesNotYetOpened.isEmpty() || time > TIME_LIMIT;
        if (isFinished) {
            int score = scoreFor(log);
            if (score > bestScore) {
                best = opened.toString();
                bestScore = score;
            }
            return;
        }

        // Travel to and open a value; may be this one
        for (String next : valvesNotYetOpened) {
            // Open the valve and log when we did it
            int distToNext = distances.get(position).get(next);
            int arrivalTimeAtValve = time + distToNext;
            int timeAfterValveOpen = arrivalTimeAtValve + 1;

            Map<String, Integer> updatedLog = new HashMap<>(log);
            if (updatedLog.containsKey(next)) {
                throw new RuntimeException();

            }
            updatedLog.put(next, timeAfterValveOpen);

            Set<String> remainingValves = new HashSet<>(valvesNotYetOpened);
            remainingValves.remove(next);

            List<String> nowOpened = new ArrayList<>(opened);
            nowOpened.add(next);

            visit(next, remainingValves, timeAfterValveOpen, nowOpened, updatedLog);
        }
    }

    private int scoreFor(Map<String, Integer> log) {
        int score = 0;
        for (String opened : log.keySet()) {
            Integer minutesOpen = TIME_LIMIT - log.get(opened);
            if (minutesOpen > 0) {
                score += flowRates.get(opened) * minutesOpen;
            }
        }
        return score;
    }

    private Map<String, Map<String, Integer>> measureDistancesBetweenNodes(Map<String, List<String>> graph) {
        Map<String, Map<String, Integer>> shortest = new HashMap<>();
        for (String node : graph.keySet()) {
            shortest.put(node, shortestPath(node, graph));
        }
        return shortest;
    }

    private Map<String, Integer>  shortestPath(String node, Map<String, List<String>> graph) {
        Map<String, Integer> dists = new HashMap<>();

        // Perform a least distance crawl from each node to the other; ignore the bidirectional optimisation
        Set<String> settled = new HashSet<>();
        // The oart 1 example will pass without the comparator been correctly set up because the keys are in correct string ordering by accident!!!
        // PriorityQueue<String> unsettled = new PriorityQueue();
        PriorityQueue<String> unsettled = new PriorityQueue(Comparator.comparingLong(dists::get));

        for (String v : graph.keySet()) {
            dists.put(v, Integer.MAX_VALUE);
        }
        dists.put(node, 0);
        unsettled.add(node);

        while (!unsettled.isEmpty()) {
            String v = unsettled.poll();
            settled.add(v);
            int newDistance = dists.get(v) + 1;
            // Update distances to our immediate unsettled neighbours
            for (String n : graph.get(v)) {
                if (!settled.contains(n)) {
                    if (newDistance < dists.get(n)) {
                        dists.put(n, newDistance);
                        unsettled.offer(n);
                    }
                }
            }
        }
        return dists;
    }

    private Map<String, List<String>> parseGraph(String filename) throws Exception {
        HashMap<String, List<String>> graph = new HashMap<>();

        // Lets use a regex for the practise even though we probably shouldn't
        Pattern pattern = Pattern.compile("Valve (.*) has flow rate=(.*); tunnel(s)? lead(s)? to valve(s?) (.*)");

        BufferedReader input = new BufferedReader(new FileReader(filename));
        while (input.ready()) {
            String line = input.readLine();
            Matcher matcher = pattern.matcher(line);
            if (matcher.matches()) {
                String value = matcher.group(1);
                Integer flowRate = Integer.parseInt(matcher.group(2));
                flowRates.put(value, flowRate);
                List<String> leadsTo = Arrays.stream(matcher.group(6).split(",")).map(String::trim).collect(Collectors.toList());
                graph.put(value, leadsTo);
            } else {
                throw new RuntimeException();
            }
        }

        return graph;
    }

    @Test
    public void testGraph() throws Exception {
        flowRates = new HashMap<>();
        // We think the graph is completely bidirectional
        assertGraphIsBidirectional(parseGraph("day16.txt"));
        assertGraphIsBidirectional(parseGraph("day16-actual.txt"));
    }

    private void assertGraphIsBidirectional(Map<String, List<String>> graph) {
        // Every node should have the same number of outgoing as incoming links
        for (String node: graph.keySet()) {
            Set<String> outGoingLinks = new HashSet(graph.get(node));
            Set<String> nodesWithIncomingLinks = new HashSet<>();
            for (String adj : graph.keySet()) {
                if (graph.get(adj).contains(node)) {
                    nodesWithIncomingLinks.add(adj);
                }
            }
            assertEquals(outGoingLinks, nodesWithIncomingLinks);
        }
    }

    // Had alot of trouble with the part1 example passing but the part1 actual been too low; shortest distancea where not symetrical because the pq comparator was node string values!
    @Test
    public void testDistances() throws Exception {
        flowRates = new HashMap<>();

        Map<String, Map<String, Integer>> shortestDistances = measureDistancesBetweenNodes(parseGraph("day16.txt"));
        assertEquals(10, shortestDistances.size());
        assertEquals(5, shortestDistances.get("AA").get("HH"));
        assertEquals(5, shortestDistances.get("HH").get("AA"));
        assertEquals(1, shortestDistances.get("AA").get("DD"));
        assertEquals(2, shortestDistances.get("AA").get("CC"));
        assertEquals(0, shortestDistances.get("AA").get("AA"));

        assertThatShortestDistancesAreSymmetrical(shortestDistances);
        assertThatShortestDistancesAreSymmetrical(measureDistancesBetweenNodes(parseGraph("day16-actual.txt")));
    }

    private void assertThatShortestDistancesAreSymmetrical(Map<String, Map<String, Integer>> distances) {
        for (String start : distances.keySet()) {
            for (String destination : distances.get(start).keySet()) {
                int there = distances.get(start).get(destination);
                int back = distances.get(destination).get(start);
                assertEquals(there, back);
            }
        }
    }

}
