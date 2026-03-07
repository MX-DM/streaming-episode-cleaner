package processing;

import model.Episode;
import processing.dedup.EpisodeDeduplicator;
import stats.ProcessingStats;


import java.util.*;

public class EpisodeProcessor {

    private EpisodeParser parser = new EpisodeParser();
    private EpisodeDeduplicator deduplicator = new EpisodeDeduplicator();

    public List<Episode> process(List<String> lines, ProcessingStats stats) {

        List<Episode> parsed = new ArrayList<>();

        for (String line : lines) {

            Episode e = parser.parse(line, stats);

            if (e != null) {
                parsed.add(e);
            }
        }

        List<Episode> deduped = deduplicator.deduplicate(parsed, stats);

        stats.totalOutput = deduped.size();

        return deduped;
    }

}
