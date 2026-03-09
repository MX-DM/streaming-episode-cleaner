import io.CsvReader;
import io.CsvWriter;
import model.Episode;
import processing.EpisodeProcessor;
import report.ReportGenerator;
import stats.ProcessingStats;

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

        String input = "src/main/resources/input/episodes.csv";

        CsvReader reader = new CsvReader();
        CsvWriter writer = new CsvWriter();
        ReportGenerator report = new ReportGenerator();

        ProcessingStats stats = new ProcessingStats();

        EpisodeProcessor processor = new EpisodeProcessor();

        List<String> lines = reader.read(input);

        List<Episode> cleaned = processor.process(lines, stats);

        writer.writeEpisodes("episodes_clean.csv", cleaned);

        report.generate("report.md", stats);

        System.out.println("Processing completed.");
    }
}
