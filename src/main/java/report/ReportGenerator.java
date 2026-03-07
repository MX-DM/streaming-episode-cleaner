package report;

import stats.ProcessingStats;

import java.io.*;

public class ReportGenerator {

    public void generate(String path, ProcessingStats stats) throws IOException {

        BufferedWriter writer = new BufferedWriter(new FileWriter(path));

        writer.write("# Data Quality Report\n\n");

        writer.write("Total input records: " + stats.totalInput + "\n");
        writer.write("Total output records: " + stats.totalOutput + "\n");
        writer.write("Discarded entries: " + stats.discarded + "\n");
        writer.write("Corrected entries: " + stats.corrected + "\n");
        writer.write("Duplicates detected: " + stats.duplicates + "\n\n");

        writer.write("## Deduplication Strategy\n");
        writer.write("Episodes are compared using normalized series name, season number, episode number, and title. ");
        writer.write("When duplicates are found, the record with the best data quality is selected based on the following priority:\n");
        writer.write("1. Valid Air Date\n");
        writer.write("2. Known Episode Title\n");
        writer.write("3. Valid Season and Episode numbers\n");

        writer.close();
    }
}
