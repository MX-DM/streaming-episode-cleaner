package io;

import java.io.*;
import java.util.*;

public class CsvReader {

    public List<String> read(String path) throws IOException {

        List<String> lines = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(path))) {

            String firstLine = br.readLine();

            // Detect a typical header like: Series,Season,Episode,Title,AirDate
            if (firstLine != null && !isHeader(firstLine)) {
                lines.add(firstLine); // it's actually data
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
