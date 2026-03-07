import io.CsvReader;
import io.CsvWriter;
import model.Episode;
import processing.EpisodeProcessor;
import report.ReportGenerator;
import stats.ProcessingStats;

import java.util.*;

public class Main {

    public static void main(String[] args) throws Exception {

        String input = "src/main/episodes.csv";

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
