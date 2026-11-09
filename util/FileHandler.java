package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/** Reads and writes the plain text data files (one record per line). */
public class FileHandler {
    private static String dataDir = "data";

    private FileHandler() { }

    public static void setDataDir(String dir) { dataDir = dir; }

    public static String getDataDir() { return dataDir; }

    public static List<String> readLines(String fileName) {
        return readLines(dataDir, fileName);
    }

    public static List<String> readLines(String dir, String fileName) {
        List<String> lines = new ArrayList<String>();
        Path path = Paths.get(dir, fileName);
        if (!Files.exists(path)) {
            return lines;
        }
        try (BufferedReader br = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            System.err.println("Could not read " + fileName + ": " + e.getMessage());
        }
        return lines;
    }

    public static boolean writeLines(String fileName, List<String> lines) {
        return writeLines(dataDir, fileName, lines);
    }

    public static boolean writeLines(String dir, String fileName, List<String> lines) {
        try {
            Files.createDirectories(Paths.get(dir));
            Files.write(Paths.get(dir, fileName), lines, StandardCharsets.UTF_8);
            return true;
        } catch (IOException e) {
            System.err.println("Could not save " + fileName + ": " + e.getMessage());
            return false;
        }
    }

    /** Commas separate fields in the files, so remove them (and line breaks) from user text. */
    public static String clean(String s) {
        if (s == null) {
            return "";
        }
        return s.replace(",", ";").replace("\n", " ").replace("\r", " ").trim();
    }
}
