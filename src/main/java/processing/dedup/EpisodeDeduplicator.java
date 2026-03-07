package processing.dedup;

import model.Episode;
import processing.EpisodeNormalizer;
import stats.ProcessingStats;

import java.util.*;

public class EpisodeDeduplicator {

    private EpisodeNormalizer normalizer = new EpisodeNormalizer();

    public List<Episode> deduplicate(List<Episode> episodes, ProcessingStats stats) {

        Map<EpisodeKey, Episode> best = new HashMap<>();

        for (Episode e : episodes) {

            EpisodeKey key = buildKey(e);

            if (!best.containsKey(key)) {

                best.put(key, e);

            } else {

                Episode existing = best.get(key);

                Episode better = chooseBetter(existing, e);

                if (better != existing) {
                    stats.duplicates++;
                }

                best.put(key, better);
            }
        }

        return new ArrayList<>(best.values());
    }

    private EpisodeKey buildKey(Episode e) {

        String series = e.getSeriesName();
        String title = e.getEpisodeTitle();

        return new EpisodeKey(series, e.getSeasonNumber(), e.getEpisodeNumber(), title);
    }

    private Episode chooseBetter(Episode a, Episode b) {

        int scoreA = score(a);
        int scoreB = score(b);

        if (scoreB > scoreA) return b;

        return a;
    }

    private int score(Episode e) {

        int score = 0;

        if (!e.getAirDate().equals("Unknown")) score += 4;
        if (!e.getEpisodeTitle().equals("Untitled Episode")) score += 2;
        if (e.getSeasonNumber() > 0 && e.getEpisodeNumber() > 0) score += 1;

        return score;
    }

}
