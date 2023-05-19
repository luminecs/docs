package org.example;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class Utils {

    public static void highlight() throws IOException {
        String language = "swift";
        String path = "/home/lumine/dev/repo/github/luminecs/docs/swift/guide/The-Basics-Documentation.md";
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
            boolean isInCodeBlock = false;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("```")) {
                    blockCount++;
                    if ("```".equals(line) && blockCount % 2 == 1) { // come in code block
                        line += language;
                        isInCodeBlock = true;
                        Files.write(Paths.get(newPath), (line + "\n").getBytes(), StandardOpenOption.APPEND);
                        continue;
                    }
                    if ("```".equals(line) && blockCount % 2 == 0) { // quit code block
                        isInCodeBlock = false;
                    }
                }
                if (isInCodeBlock) { // come in code block
                    String l = line.trim();
                    if (l.length() == 0) { // delete blank code line
                    } else { // exist code line
                        Files.write(Paths.get(newPath), (line + "\n").getBytes(), StandardOpenOption.APPEND);
                    }
                } else {
                    Files.write(Paths.get(newPath), (line + "\n").getBytes(), StandardOpenOption.APPEND);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        String cmd = String.format("pandoc %s -o %s --highlight-style=pygments", newPath,
                newPath.replace(".md", ".docx"));
        Runtime.getRuntime().exec(cmd);
    }

}
