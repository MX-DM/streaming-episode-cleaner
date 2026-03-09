package processing;

import model.Episode;
import processing.dedup.EpisodeDeduplicator;
import stats.ProcessingStats;


import java.util.*;

/**
 * Coordinates the full processing pipeline:
 * parsing → deduplication → sorting.
 */

public class EpisodeProcessor {

    private EpisodeParser parser = new EpisodeParser();
    private EpisodeDeduplicator deduplicator = new EpisodeDeduplicator();

    public List<Episode> process(List<String> lines, ProcessingStats stats) {

        List<Episode> parsed = new ArrayList<>();

        // Begin parsing of read lines

        for (String line : lines) {

            Episode e = parser.parse(line, stats);

            if (e != null) {
                parsed.add(e);
            }
        }

        // Deduplication of cleaned, normalized and validated list of episodes

        List<Episode> deduped = deduplicator.deduplicate(parsed, stats);

        // Sorting of final output

        sortEpisodes(deduped);

        stats.totalOutput = deduped.size();

        return deduped;
    }


    private void sortEpisodes(List<Episode> episodes) {

        episodes.sort(
                Comparator.comparing(Episode::getSeriesName)
                        .thenComparingInt(Episode::getSeasonNumber)
                        .thenComparingInt(Episode::getEpisodeNumber)
                        .thenComparing(Episode::getEpisodeTitle)
        );
    }

}
