import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Day2 {

    public static final char ROCK = 'A';
    public static final char PAPER = 'B';
    public static final char SCISSORS = 'C';

    public static final char LOSE = 'X';
    public static final char WIN = 'Z';

    @Test
    public void day2() throws Exception {
        int sum = getSum(new BufferedReader(new FileReader("day2.txt")));
        assertEquals(15, sum);

        sum = getSum(new BufferedReader(new FileReader("day2-actual.txt")));
        assertEquals(13682, sum);
    }

    @Test
    public void part2() throws Exception {
        assertEquals(12, getPart2Sum(new BufferedReader(new FileReader("day2.txt"))));
        assertEquals(12881, getPart2Sum(new BufferedReader(new FileReader("day2-actual.txt"))));
    }

    private int getPart2Sum(BufferedReader input) throws Exception {
        List<char[]> guide = loadGuide(input);

        Map<Character, Character> beats = new HashMap<>();
        beats.put(ROCK, SCISSORS);
        beats.put(SCISSORS, PAPER);
        beats.put(PAPER, ROCK);

        int sum = 0;
        for (char[] round : guide) {
            // Foreach round we know there move and the outcome we need; we need to detemine out move
            char them = round[0];
            char outcome = round[1];

            char you = them;
            if (outcome == LOSE) {
                you = beats.get(them);

            } else if (outcome == WIN) {
                // Iterate the winners to find something which beats them
                for (char key: beats.keySet()) {
                    if (beats.get(key) == them) {
                        you = key;
                    }
                }
            }
            sum += getOutcomeScore(them, you) + getMoveValue(you);
        }

        return sum;
    }

    private int getSum(BufferedReader input) throws IOException {
        Map<Character, Character> decryptionKey = new HashMap<>();
        decryptionKey.put('X', ROCK);
        decryptionKey.put('Y', PAPER);
        decryptionKey.put('Z', SCISSORS);

        // Read the into a list of lists; we need to decrypt the 2nd columns
        List<char[]> encrypted = loadGuide(input);

        List<char[]> guide = new ArrayList<>();
        for (char[] round : encrypted) {
            char key = round[1];
            Character character = decryptionKey.get(key);
            char[] chars = {round[0], character};
            guide.add(chars);
        }

        // Then iterate the list summing up the scores we got for following the guide
        int sum = 0;
        for (char[] round : guide) {
            char them = round[0];
            char you = round[1];
            int moveValue = getMoveValue(you);
            int outcomeScore = getOutcomeScore(them, you);
            sum += moveValue + outcomeScore;
        }
        return sum;
    }

    private Integer getMoveValue(char move) {
        Map<Character, Integer> moveValues = new HashMap<>();
        moveValues.put(ROCK, 1);
        moveValues.put(PAPER, 2);
        moveValues.put(SCISSORS, 3);
        return moveValues.get(move);
    }

    private int getOutcomeScore(char them, char move) {
        Map<Character, Character> beats = new HashMap<>();
        beats.put(ROCK, SCISSORS);
        beats.put(SCISSORS, PAPER);
        beats.put(PAPER, ROCK);

        int outcomeScore = 0;
        if (move == them) {
            // Draw
            outcomeScore = 3;
        } else if (beats.get(move) == them) {
            // Our win
            outcomeScore = 6;
        }
        return outcomeScore;
    }

    private List<char[]> loadGuide(BufferedReader input) throws IOException {
        List<char[]> guide = new ArrayList<>();
        while (input.ready()) {
            String line = input.readLine();
            guide.add(new char[]{line.charAt(0), line.charAt(2)});
        }
        return guide;
    }

}
