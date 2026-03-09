import io.CsvReader;
import io.CsvWriter;
import model.Episode;
import processing.EpisodeProcessor;
import report.ReportGenerator;
import stats.ProcessingStats;

import java.io.File;
import java.util.*;

/**
 * Entry point of the application.
 *
 * Pipeline:
 * 1. Read CSV input file
 * 2. Parse, clean and normalize records
 * 3. Deduplicate episodes
 * 4. Sort final catalog
 * 5. Write cleaned CSV and quality report
 */

public class Main {

    public static void main(String[] args) throws Exception {

        CsvReader reader = new CsvReader();
        CsvWriter writer = new CsvWriter();
        ReportGenerator report = new ReportGenerator();

        ProcessingStats stats = new ProcessingStats();

        EpisodeProcessor processor = new EpisodeProcessor();

        List<String> lines = reader.read();

        List<Episode> cleaned = processor.process(lines, stats);

        writer.writeEpisodes("episodes_clean.csv", cleaned);

        report.generate("report.md", stats);

        System.out.println("Processing completed.");
        System.out.println("Output file: episodes_clean.csv");
        System.out.println("Report generated: report.md");
    }
}
