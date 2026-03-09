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

        writer.write("## Deduplication Strategy\n\n");
        writer.write("For a detailed explanation of the deduplication algorithm and illustrative examples, see README.md.\n\n");

        writer.write("Episodes are compared using three possible matching keys derived from the episode data:\n");
        writer.write("- (series, season, episode)\n");
        writer.write("- (series, 0, episode, title)\n");
        writer.write("- (series, season, 0, title)\n\n");

        writer.write("These keys allow matching episodes even when season or episode numbers are missing.\n");

        writer.write("The algorithm maintains an index of previously processed episodes. ");
        writer.write("When a new episode is processed, all keys are checked against the index to detect potential duplicates.\n\n");

        writer.write("If duplicates are found, all connected records are treated as a single group and the best episode is selected.\n");

        writer.write("The best episode is chosen using the following priority rules:\n");
        writer.write("1. Episode with a valid air date\n");
        writer.write("2. Episode with a known title (not \"untitled episode\")\n");
        writer.write("3. Episode with valid season and episode numbers\n\n");

        writer.write("If multiple records have equal quality, the first one encountered is kept to ensure deterministic results.\n");

        writer.close();
    }
}
