import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Day5 {

    @Test
    public void day5() throws Exception {
        // Parse the file.
        // Reverse the lines
        // Push the contains onto indexed stacks
        // Assume we are given the number of stacks
        // Pop off the top items
        assertEquals("CMZ", process("day5.txt", 3));
        assertEquals("HBTMTBSDC", process("day5-actual.txt", 9));

        assertEquals("MCD", processWith9001("day5.txt", 3));
        assertEquals("PQTJRSHWS", processWith9001("day5-actual.txt", 9));
    }

    private String processWith9001(String filename, int n) throws Exception {
        List<Stack<Character>> stacks = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            stacks.add(new Stack<>());
        }

        // Fill the stacks
        for (Character[] line : getStacks(filename, n)) {
            for (int i = 0; i < n; i++) {
                if (line[i] != null) {
                    stacks.get(i).add(line[i]);
                }
            }
        }

        // Execute the moves
        List<List<Integer>> moves = getMoves(filename);
        moveUsing9001(stacks, moves);

        // Read out the heads
        StringBuilder out = new StringBuilder();
        for (Stack stack : stacks) {
            if (!stack.isEmpty()) {
                out.append(stack.peek());
            }
        }
        return out.toString();
    }

    private String process(String filename, int n) throws Exception {
        List<Stack<Character>> stacks = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            stacks.add(new Stack<>());
        }

        // Fill the stacks
        for (Character[] line : getStacks(filename, n)) {
            for (int i = 0; i < n; i++) {
                if (line[i] != null) {
                    stacks.get(i).add(line[i]);
                }
            }
        }

        // Execute the moves
        List<List<Integer>> moves = getMoves(filename);
        moveUsing9000(stacks, moves);

        // Read out the heads
        StringBuilder out = new StringBuilder();
        for (Stack stack : stacks) {
            if (!stack.isEmpty()) {
                out.append(stack.peek());
            }
        }
        return out.toString();
    }

    private void moveUsing9000(List<Stack<Character>> stacks, List<List<Integer>> moves) {  // TODO use a lambda for these
        for (List<Integer> move : moves) {
            int num = move.get(0);
            int from = move.get(1) - 1;
            int to = move.get(2) - 1;
            for (int i = 0; i < num; i++) {
                Character c = stacks.get(from).pop();
                stacks.get(to).push(c);
            }
        }
    }

    private void moveUsing9001(List<Stack<Character>> stacks, List<List<Integer>> moves) {
        for (List<Integer> move : moves) {
            int num = move.get(0);
            int from = move.get(1) - 1;
            int to = move.get(2) - 1;

            List<Character> taken = new ArrayList<>();
            for (int i = 0; i < num; i++) {
                Character c = stacks.get(from).pop();
                taken.add(c);
            }
            Collections.reverse(taken);
            for (Character c : taken) {
                stacks.get(to).push(c);
            }
        }
    }

    private List<List<Integer>> getMoves(String filename) throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        List<String> lines = reader.lines().collect(Collectors.toList());
        List<String> moveLines = lines.stream().filter(l -> l.contains("move")).collect(Collectors.toList());

        List<List<Integer>> moves = new ArrayList<>();
        for (String moveLine : moveLines) {
            String numbers = moveLine.replace("move", "").replace("from ", "").replace("to ", "").trim();
            moves.add(Arrays.stream(numbers.split(" ")).sequential().map(Integer::parseInt).collect(Collectors.toList()));
        }
        return moves;
    }

    private List<Character[]> getStacks(String filename, int n) throws FileNotFoundException {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        List<String> lines = reader.lines().collect(Collectors.toList());

        List<String> stacks = lines.stream().filter(l -> !l.contains("move") && l.length() > 0).collect(Collectors.toList());
        Collections.reverse(stacks);
        List<String> stackLines = stacks.subList(1, stacks.size()); // Strip header

        // Parse stack lines
        List<Character[]> layers = new ArrayList<>();
        for (String stackLine : stackLines) {
            Character[] layer = new Character[n];
            int i = 0;
            for (int d = 1; d < stackLine.length(); d += 4) {
                char c = stackLine.charAt(d);
                if (c != ' ') {
                    layer[i] = c;
                }
                i++;
            }
            layers.add(layer);
        }

        return layers;
    }

}
