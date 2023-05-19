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
            int blockStartBlankLineCount = 0;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("```")) {
                    blockCount++;
                    if ("```".equals(line) && blockCount % 2 == 1) {
                        line += language;
                        blockStartBlankLineCount++;
                        Files.write(Paths.get(newPath), (line + "\n").getBytes(), StandardOpenOption.APPEND);
                        continue;
                    }
                }
                if (blockStartBlankLineCount != 0) { // come in code block
                    String l = line.trim();
                    System.out.println("l = " + l);
                    if (l.length() == 0) { // blank code line
                    } else { // exist code line
                        blockStartBlankLineCount = 0;
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
