import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Day11 {

    private final Function<Long, Long> divideByThree = i -> i / 3;

    @Test
    public void day11() throws Exception {
        assertEquals(10605, getMoneyBusiness(parseInput("day11.txt"), 20, divideByThree));
        assertEquals(76728, getMoneyBusiness(parseInput("day11-actual.txt"), 20, divideByThree));

        List<Monkey> monkeys = parseInput("day11.txt");
        assertEquals(2713310158L, getMoneyBusiness(monkeys, 10000, makeReduceByMultipleOfTestsFor(monkeys)));
        monkeys = parseInput("day11-actual.txt");
        assertEquals(21553910156L, getMoneyBusiness(monkeys, 10000, makeReduceByMultipleOfTestsFor(monkeys)));
    }

    private long getMoneyBusiness(List<Monkey> monkeys, int n, Function<Long, Long> worryReducer) {
        // For n rounds each monkey looks at it's items in turn redistributing them to other monkeys;
        // We want to count how many inspections each monkey does;
        Map<Integer, Long> inspections = new HashMap<>();
        for (int i = 0; i < n; i++) {
            int m = 0;
            for (Monkey monkey : monkeys) {
                // Foreach item this monkey has, inspect it
                while (!monkey.items.isEmpty()) {
                    long item = monkey.items.pollFirst();
                    long operatedOn = applyOperation(item, monkey.operation);
                    if (operatedOn < 0) {
                        // Guard rail for long wrap arounds
                        throw new RuntimeException();
                    }

                    // Reduce worry to manageable levels using the supplied strategy
                    long worry = worryReducer.apply(operatedOn);

                    boolean outcome = worry % monkey.test == 0;

                    // Throw to next monkey based on outcome of inspection
                    int nextMonkey = outcome ? monkey.ifTrue : monkey.ifFalse;

                    monkeys.get(nextMonkey).offer(worry);

                    inspections.put(m, inspections.getOrDefault(m, 0L) + 1);
                }
                m++;
            }
        }

        // Filter for 2 most active
        List<Long> values = new ArrayList<>(inspections.values());
        Collections.sort(values, Collections.reverseOrder());
        return values.get(0) * values.get(1);
    }

    private long applyOperation(long item, String operation) {
        String[] components = operation.split(" ");
        String op = components[3];
        long right = components[4].equals("old") ? item : Integer.parseInt(components[4]);

        if (op.equals("+")) {
            return item + right;
        } else if (op.equals("*")) {
            return item * right;
        }
        throw new UnsupportedOperationException(op);
    }

    private List<Monkey> parseInput(String filename) throws IOException {
        List<Monkey> monkeys = new ArrayList<>();
        BufferedReader input = new BufferedReader(new FileReader(filename));
        while (input.ready()) {
            String line = input.readLine();
            // Parse a single monkey
            if (line.startsWith("Monkey")) {
                // Consume the 5 lines of monkey data
                List<Long> startingItems = Arrays.stream(input.readLine().split("Starting items: ")[1].
                        split(", ")).map(Long::parseLong).collect(Collectors.toList());
                String operation = input.readLine().split("Operation: ")[1];
                long test = Long.parseLong(input.readLine().split(" ")[5]);
                int ifTrue = Integer.parseInt(input.readLine().split(" ")[9]);
                int ifFalse = Integer.parseInt(input.readLine().split(" ")[9]);
                monkeys.add(new Monkey(startingItems, operation, test, ifTrue, ifFalse));
            }
        }
        return monkeys;
    }

    private Function<Long, Long> makeReduceByMultipleOfTestsFor(List<Monkey> monkeys) {
        // We are allowed to subtract any number of the multiple of the monkey tests
        // I had to look this strategy up on the reddit
        final long testMultiplier = monkeys.stream().map( m -> m.test).reduce(1L, (a, b) -> a * b);
        return (worry -> {
            long r = worry / testMultiplier;
            return worry - (r * testMultiplier);
        }
        );
    }

    class Monkey {
        ArrayDeque<Long> items;
        String operation;
        long test;
        int ifTrue;
        int ifFalse;

        public Monkey(List<Long> startingItems, String operation, long test, int ifTrue, int ifFalse) {
            this.items = new ArrayDeque<>(startingItems);
            this.operation = operation;
            this.test = test;
            this.ifTrue = ifTrue;
            this.ifFalse = ifFalse;
        }

        public void offer(long item) {
            items.offer(item);
        }
    }
}
