package org.example;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class Utils {

    public static void batchProcess() throws Exception {
        String property = System.getProperty("user.dir");
        File file = new File(property + "/content");
        for (File f : file.listFiles()) {
            String language = f.getName();
            for (File i : com.google.common.io.Files.fileTraverser().depthFirstPreOrder(f)) {
                if (i.isDirectory()) {
                    continue;
                }
                if (i.getAbsolutePath().endsWith(".md")
                        && !i.getAbsolutePath().endsWith("-1.md")
                        && !i.getAbsolutePath().endsWith("-new.md")) {
                    highlight(language, i, true);
                }
            }

        }
    }

    public static void highlight(String language, File file, boolean isForceUpdate) throws IOException {
        String newPath = file.getAbsolutePath().replace(".md", "-1.md");
        if (!isForceUpdate && (new File(newPath).exists())) {
            return;
        }
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

        String mdToDocxCmd = String.format("pandoc %s -s -o %s --highlight-style=pygments", newPath,
                newPath.replace(".md", ".docx"));
        System.out.println("mdToDocxCmd = " + mdToDocxCmd);
        Runtime.getRuntime().exec(mdToDocxCmd);
        String mdToHtmlCmd = String.format("pandoc %s --toc -s -o %s --highlight-style=pygments", newPath,
                newPath.replace(".md", ".html"));
        System.out.println("mdToHtmlCmd = " + mdToHtmlCmd);
        Runtime.getRuntime().exec(mdToHtmlCmd);
    }

}
