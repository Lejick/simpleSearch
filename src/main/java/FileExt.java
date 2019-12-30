import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class FileExt implements Comparable<FileExt> {
    protected static final String REG_SPLIT_FILE = "[\\s,.;!?]+";
    protected long lastModified;
    protected String filename;
    protected Float currentQuality;
    protected Map<String, Float> cachedResults = new HashMap();
    protected List<String> wordsList;
    protected List<String> wordsExistList;

    public FileExt(long lastModified, String filePath, String filename) {
        this.lastModified = lastModified;
        this.filename = filename;
        this.wordsList = findWordsList(filePath);
        this.wordsExistList = wordsList.stream().distinct().collect(Collectors.toList());

    }

    public String toString() {
        return filename + ":" + String.format("%.1f", currentQuality) + "%";
    }

    public long lastModified() {
        return lastModified;
    }

    public Float getCurrentQuality() {
        return currentQuality;
    }

    public int compareTo(FileExt other) {
        if (this.getCurrentQuality() > other.getCurrentQuality()) {
            return 1;
        } else if (this.getCurrentQuality() < other.getCurrentQuality()) {
            return -1;
        }
        return 0;
    }

    public void calculate(String... words) {
        if (words.length == 1) {
            singleCalculate(words[0]);
        } else {
            simpleCountCalculate(words);
        }
    }

    public static List<String> findWordsList(String fileName) {
        try {
            List<String> collect =
                    Files.lines(Paths.get(fileName)).parallel()
                            .flatMap(line -> Arrays.stream(line.split(REG_SPLIT_FILE))).collect(Collectors.toList());
            return collect;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    protected void simpleCountCalculate(String... words) {
        float wordsAll = words.length;
        float wordsFound = 0;
        List<String> wordsList = Arrays.asList(words);
        for (String wordInFile : wordsExistList) {
            if (wordsList.contains(wordInFile)) {
                wordsFound++;
                wordsList.remove(wordInFile);
            }
            if (wordsFound == wordsAll) {
                break;
            }
        }
        currentQuality = wordsFound / wordsAll;
        cachedResults.put(createStringKey(words), currentQuality);
    }

    private String createStringKey(String... words) {
        StringBuilder sb = new StringBuilder();
        for (String st : words) {
            sb.append(st).append(" ");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    protected void singleCalculate(String word) {
        currentQuality = 0F;
        if (wordsExistList.contains(word)) {
            currentQuality = 100F;
        }
        cachedResults.put(word, currentQuality);
    }
}

