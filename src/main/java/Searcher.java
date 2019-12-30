import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class Searcher {

    private static final String REG_SPLIT_INPUT = "[\\s]+";
    private static Map<String, FileExt> fileExtMap = new HashMap<>();

    public static void main(String[] args) {

        if (args.length == 0) {
            throw new IllegalArgumentException("No directory given to index.");
        }
        final File indexableDirectory = new File(args[0]);
        try (Scanner keyboard = new Scanner(System.in)) {
            System.out.println("Type ':quit' to exit!");
            File[] files = indexableDirectory.listFiles();
            if (files.length == 0) {
                throw new IllegalArgumentException("No files in directory");
            }
            System.out.println(files.length + " files read in directory " + indexableDirectory);

            while (true) {
                System.out.print("search > ");
                final String line = keyboard.nextLine();
                if (line.equalsIgnoreCase(":quit")) {
                    System.exit(0);
                }
                String[] words = line.split(REG_SPLIT_INPUT);
                for (File file : files) {
                    String filePath = file.getAbsolutePath();
                    FileExt fileExtObject = fileExtMap.get(filePath);
                    if (fileExtObject == null || fileExtObject.lastModified() != file.lastModified()) {
                        fileExtObject = new FileExt(file.lastModified(), filePath, file.getName());
                        fileExtMap.put(filePath, fileExtObject);
                    }
                    fileExtObject.calculate(words);
                }
                List<FileExt> resultList = fileExtMap.values().stream().sorted().limit(10).
                        filter(FileExtObject -> FileExtObject.getCurrentQuality() > 0).collect(Collectors.toList());
                if (resultList.size() == 0) {
                    System.out.println("No matches found");
                }
                resultList.forEach(System.out::println);
            }
        }
    }


}