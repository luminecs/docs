import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class Highlight {

    public static void main(String[] args) throws Exception {
        String language = "swift";
        String path = "/home/lumine/下载/basic.md";
        String newPath = path.replace(".md", "-new.md");
        File file = new File(path);
        File newFile = new File(newPath);
        if (newFile.exists()) {
            newFile.delete();
        }
        newFile.createNewFile();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int blockCount = 0;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("```")) {
                    blockCount++;
                    if ("```".equals(line) && blockCount % 2 == 1) {
                        line += language;
                    }
                }
                Files.write(Paths.get(newPath), (line + "\n").getBytes(), StandardOpenOption.APPEND);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Process process = Runtime.getRuntime()
                .exec(String.format("pandoc %s -o %s --highlight-style=pygments", newPath,
                        newPath.replace(".md", ".docx")));
    }
}
