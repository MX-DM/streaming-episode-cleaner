package processing.dedup;

import model.Episode;
import stats.ProcessingStats;

import java.util.*;

public class EpisodeDeduplicator {

    public List<Episode> deduplicate(List<Episode> episodes, ProcessingStats stats) {

        Map<EpisodeKey, Episode> keyIndex = new HashMap<>();
        Set<Episode> keptEpisodes = new LinkedHashSet<>();

        for (Episode episode : episodes) {

            List<EpisodeKey> keys = generateKeys(episode);

            Episode duplicate = null;

            for (EpisodeKey key : keys) {
                Episode existing = keyIndex.get(key);
                if (existing != null) {
                    duplicate = existing;
                    break;
                }
            }

            if (duplicate == null) {
                keptEpisodes.add(episode);

                for (EpisodeKey key : keys) {
                    keyIndex.put(key, episode);
                }
            } else {
                Episode best = chooseBest(duplicate, episode);

                if (best != duplicate) {
                    keptEpisodes.remove(duplicate);
                    keptEpisodes.add(best);

                    for (EpisodeKey key : keys) {
                        keyIndex.put(key, best);
                    }
                }
                stats.discarded++;
            }
        }
        return new ArrayList<>(keptEpisodes);
    }

    private List<EpisodeKey> generateKeys(Episode ep) {

        List<EpisodeKey> keys = new ArrayList<>();

        String series = ep.getSeriesName();
        int season = ep.getSeasonNumber();
        int episode = ep.getEpisodeNumber();
        String title = ep.getEpisodeTitle();

        keys.add(new EpisodeKey(series, season, episode, null));
        if (ep.hasValidTitle()) {
            keys.add(new EpisodeKey(series, 0, episode, title));
            keys.add(new EpisodeKey(series, season, 0, title));
        }

        return keys;
    }

    private Episode chooseBest(Episode a, Episode b) {

        if (a.hasValidAirDate() && !b.hasValidAirDate()) return a;
        if (!a.hasValidAirDate() && b.hasValidAirDate()) return b;

        if (a.hasValidTitle() && !b.hasValidTitle()) return a;
        if (!a.hasValidTitle() && b.hasValidTitle()) return b;

        if (a.hasValidNumbers() && !b.hasValidNumbers()) return a;
        if (!a.hasValidNumbers() && b.hasValidNumbers()) return b;

        return a;
    }
}
