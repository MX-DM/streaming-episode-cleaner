package io;

import java.io.*;
import java.util.*;

public class CsvReader {

    public List<String> read(String path) throws IOException {

        List<String> lines = new ArrayList<>();

        BufferedReader br = new BufferedReader(new FileReader(path));

        String line;
        br.readLine(); // Discard header
        while ((line = br.readLine()) != null) {
            lines.add(line);
        }

        br.close();

        return lines;
    }
}
