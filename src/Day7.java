import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Day7 {

    @Test
    public void day7() throws Exception {
        assertEquals(95437, getSumOfFoldersSizesUnder("day7.txt"));
        assertEquals(1783610, getSumOfFoldersSizesUnder("day7-actual.txt"));
    }

    @Test
    public void day7Part2() throws Exception {
        assertEquals(24933642, dirToDelete("day7.txt"));
        assertEquals(4370655, dirToDelete("day7-actual.txt"));

    }

    private long dirToDelete(String fileName) throws IOException {
        long totalSpace = 70000000;
        long spaceNeeded = 30000000;

        Map<String, Long> dirSizes = getDirSizes(fileName);
        long spaceFree = totalSpace - dirSizes.get("/,");
        long spaceToFree = spaceNeeded - spaceFree;

        // Get the sizes of all folders;
        // Filter to all those which are larger than the required space.
        // Output the smallest of these.

        // Sort map by values ascending
        List<Long> sizes = new ArrayList<>(dirSizes.values());  // TODO need to know how to this on Map Entries
        Collections.sort(sizes);
        for (long size: sizes) {
            if (size >= spaceToFree) {
                return size;
            }
        }
        return -1L;
    }

    private long getSumOfFoldersSizesUnder(String fileName) throws IOException {
        Map<String, Long> dirSizes = getDirSizes(fileName);
        // Filter for all folder less than 100000 and sum there sizes.
        long sum = 0;
        for (Long dirSize : dirSizes.values()) {
            if (dirSize <= 100000) {
                sum += dirSize;
            }
        }
        return sum;
    }

    private Map<String, Long> getDirSizes(String fileName) throws IOException {
        Map<String, Long> dirSizes = new HashMap<>();

        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        Stack<String> currentFolder = new Stack<>();
        while (reader.ready()) {
            String line = reader.readLine();
            if (line.equals("$ cd ..")) {
                currentFolder.pop();

            } else if (line.startsWith("$ cd ")) {
                String folder = line.split(" ")[2];
                currentFolder.push(folder);

            } else {
                if (!line.startsWith("dir ") && !line.equals("$ ls")) {
                    String[] fileAttributes = line.split(" ");
                    long fileSize = Long.parseLong(fileAttributes[0]);

                    // Add this file size to all in scope folders
                    Iterator<String> iterator = currentFolder.iterator();
                    StringBuilder folderName = new StringBuilder();
                    while(iterator.hasNext()) {
                        folderName.append(iterator.next() + ",");
                        String key = folderName.toString();
                        long count = dirSizes.getOrDefault(key, 0L);
                        dirSizes.put(key, count + fileSize);
                    }
                }
            }
        }
        return dirSizes;
    }

}
