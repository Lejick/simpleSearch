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
            return -1;
        } else if (this.getCurrentQuality() < other.getCurrentQuality()) {
            return 1;
        }
        return 0;
    }

    public void calculate(String... words) {
        if (words.length == 1) {
            singleCalculate(words[0]);
        } else {
            relevantCalculate(words);
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
        List<String> wordsList = Arrays.asList(words);
        Set<String> set = new TreeSet<>(wordsList);
        float wordsAll = set.size();
        float wordsFound = 0;
        String[] wordsForKey = set.toArray(new String[set.size()]);
        if (cachedResults.get(createStringKey(wordsForKey)) != null) {
            currentQuality = cachedResults.get(createStringKey(wordsForKey));
            return;
        }
        for (String wordInSet : set) {
            if (wordsExistList.contains(wordInSet)) {
                wordsFound++;
            }
        }
        currentQuality = 100 * (wordsFound / wordsAll);
        cachedResults.put(createStringKey(wordsForKey), currentQuality);
    }

    protected void relevantCalculate(String... words) {
        List<String> searchWordsList = Arrays.asList(words);
        Set<String> set = new TreeSet<>(searchWordsList);
        float wordsAll = set.size();
        float wordsFound = 0;
        currentQuality = 0F;
        String[] wordsForKey = set.toArray(new String[set.size()]);
        String key = createStringKey(wordsForKey);

        if (cachedResults.get(key) != null) {
            currentQuality = cachedResults.get(key);
            return;
        }
        List<List<Integer>> wordsArray = new ArrayList<>();
        for (String searchWord : set) {
            List<Integer> positions = new ArrayList<>();
            boolean alreadyFound = false;
            for (int i = 0; i < wordsList.size(); i++) {
                if (searchWord.equalsIgnoreCase(wordsList.get(i))) {
                    if (!alreadyFound) {
                        wordsFound++;
                        alreadyFound = true;
                    }
                    positions.add(i);
                }

            }
            if (positions.size() > 0) {
                wordsArray.add(positions);
            }
        }
        float lenBtwWords = 1;
        if(wordsArray.size()>1) {
            lenBtwWords = 0;
            for (int i = 0; i < wordsArray.size() - 1; i++) {
                int minlen = Integer.MAX_VALUE;
                for (int j =  1; j < wordsArray.size(); j++) {
                    int len = minBetween(wordsArray.get(i), wordsArray.get(j));
                    if (len < minlen && i!=j) {
                        minlen = len;
                    }
                }

                lenBtwWords += (minlen);
            }
        }
        float part = 100 / wordsAll;

        currentQuality = 100 * (wordsFound / wordsAll);
        currentQuality = currentQuality - part;
        float correctionCoeff=1;
        if(wordsArray.size()>1){
            correctionCoeff = (wordsArray.size()-1) / lenBtwWords;
        }

        currentQuality = currentQuality + part * correctionCoeff;
        cachedResults.put(createStringKey(wordsForKey), currentQuality);
    }

    private int minBetween(List<Integer> list1, List<Integer> list2) {
        int min = Integer.MAX_VALUE;
        for (Integer int1 : list1) {
            for (Integer int2 : list2) {
                if (Math.abs(int1 - int2) < min) {
                    min = Math.abs(int1 - int2);
                }
            }
        }
        return min;
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

