package io;

import java.io.*;
import java.util.*;

/**
 * Reads a CSV file and returns its lines.
 * Automatically detects and skips a header if present.
 */

public class CsvReader {

    // Folder can be changed here

    private static final String INPUT_FOLDER = "src/main/resources/input";

    public List<String> read() throws IOException {

        File folder = new File(INPUT_FOLDER);

        File[] csvFiles = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".csv"));

        if (csvFiles == null || csvFiles.length == 0) {
            throw new RuntimeException("No CSV file found in " + INPUT_FOLDER);
        }

        if (csvFiles.length > 1) {
            System.out.println("Multiple CSV files found. Using: " + csvFiles[0].getName());
        }

        File inputFile = csvFiles[0];

        System.out.println("Input CSV detected: " + inputFile.getName());

        return readFile(inputFile.getAbsolutePath());
    }

    private List<String> readFile(String path) throws IOException {

        List<String> lines = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(path))) {

            String firstLine = br.readLine();

            // Detect a typical header like: Series,Season,Episode,Title,AirDate
            if (firstLine != null && !isHeader(firstLine)) {
                lines.add(firstLine);
            }

            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        }

        return lines;
    }

    private boolean isHeader(String line) {

        String[] parts = line.toLowerCase().split(",", -1);

        return parts[0].contains("series")
                && parts[1].contains("season")
                && parts[2].contains("episode");
    }
}
