package processing.dedup;

import model.Episode;
import stats.ProcessingStats;

import java.util.*;

/**
 * Removes duplicate episodes based on multiple matching strategies.
 *
 * Duplicate detection is based on three possible keys:
 *
 * 1. (series, season, episode)
 * 2. (series, 0, episode, title)
 * 3. (series, season, 0, title)
 *
 * The algorithm supports transitive matching. This means that
 * if episode A matches B, and B matches C, all three will be
 * treated as part of the same duplicate group.
 *
 * Among duplicates, the best episode is selected
 */

public class EpisodeDeduplicator {

    public List<Episode> deduplicate(List<Episode> episodes, ProcessingStats stats) {

        Map<EpisodeKey, Episode> keyIndex = new HashMap<>();
        Set<Episode> keptEpisodes = new LinkedHashSet<>();

        for (Episode episode : episodes) {

            List<EpisodeKey> keys = generateKeys(episode);

            // Stores all duplicates connected by any key of the current episode
            Set<Episode> duplicates = new HashSet<>();

            for (EpisodeKey key : keys) {
                Episode existing = keyIndex.get(key);
                if (existing != null) {
                    duplicates.add(existing);
                }
            }

            if (duplicates.isEmpty()) {
                keptEpisodes.add(episode);

                for (EpisodeKey key : keys) {
                    keyIndex.put(key, episode);
                }

            } else {

                // Determine best episode among all connected ones
                Episode best = episode;

                for (Episode dup : duplicates) {
                    best = chooseBest(best, dup);
                }

                // Remove all duplicates from kept set
                for (Episode dup : duplicates) {
                    keptEpisodes.remove(dup);
                }

                keptEpisodes.add(best);

                // Collect all keys from duplicates and current episode
                Set<EpisodeKey> allKeys = new HashSet<>();

                for (Episode dup : duplicates) {
                    allKeys.addAll(generateKeys(dup));
                }

                // Add current episode keys
                allKeys.addAll(keys);

                // Update index so all keys point to the best episode
                for (EpisodeKey key : allKeys) {
                    keyIndex.put(key, best);
                }
            }
        }

        stats.duplicates = episodes.size() -  keptEpisodes.size();
        return new ArrayList<>(keptEpisodes);
    }

    private List<EpisodeKey> generateKeys(Episode ep) {

        List<EpisodeKey> keys = new ArrayList<>();

        String series = ep.getSeriesName();
        int season = ep.getSeasonNumber();
        int episode = ep.getEpisodeNumber();
        String title = ep.getEpisodeTitle();

        keys.add(new EpisodeKey(series, season, episode, null));
        keys.add(new EpisodeKey(series, 0, episode, title));
        keys.add(new EpisodeKey(series, season, 0, title));

        return keys;
    }

    /*
     * Chooses the best episode between two duplicates.
     * Priority order:
     * 1. Episode with a valid air date
     * 2. Episode with a valid title (not "untitled episode")
     * 3. Episode with valid season and episode numbers (> 0)
     *
     * If both episodes have the same quality, the first one encountered
     * is kept to preserve deterministic behavior.
     */

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
