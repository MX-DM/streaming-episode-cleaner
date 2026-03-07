package processing;

import model.Episode;
import stats.ProcessingStats;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class EpisodeParser {

    // Expected number of CSV fields
    private static final int EXPECTED_FIELDS = 5;
    private final EpisodeNormalizer normalizer = new EpisodeNormalizer();

    public Episode parse(String line, ProcessingStats stats) {
        stats.totalInput++;

        /*
         * The -1 argument keeps trailing empty fields.
         * Example:
         * "Breaking Bad,1,5,Title," -> last column preserved as ""
         */
        String[] parts = line.split(",", -1);

        // Ensure we always have exactly 5 fields
        if (parts.length < EXPECTED_FIELDS) {
            parts = pad(parts);
        }


        String seasonStr = parts[1].trim();
        String episodeStr = parts[2].trim();
        String airDate = parts[4].trim();

        // Track whether this line required any correction
        boolean[] correctedLine = {false};

        // Normalize text and increase corrected counter if necessary
        String series = normalizer.normalize(parts[0], correctedLine);
        String title = normalizer.normalize(parts[3], correctedLine);

        // Parse numeric fields (handles empty, negative, or invalid numbers)
        int season = parseNumber(seasonStr, correctedLine);
        int episode = parseNumber(episodeStr, correctedLine);

        // --- Validation rules ---

        // Series name is mandatory
        if (series.isEmpty()) {
            stats.discarded++;
            return null;
        }

        // Missing title -> default value
        if (title.isEmpty()) {
            title = "Untitled Episode";
            correctedLine[0] = true;
        }

        // Missing or invalid air date -> default value
        if (airDate.isEmpty()) {
            airDate = "Unknown";
            correctedLine[0] = true;
        } else if (!isValidDate(airDate) && !airDate.equals("Unknown")) {
            airDate = "Unknown";
            correctedLine[0] = true;
        }

        /*
         * Discard rule:
         * If Episode Number, Title and Air Date are all missing
         * (0 / "Untitled Episode" / "Unknown"), the record cannot
         * uniquely identify an episode and must be discarded.
         */
        if (episode == 0 && title.equals("Untitled Episode") && airDate.equals("Unknown")) {
            stats.discarded++;
            return null;
        }

        // Count corrections only for records that survive validation
        if (correctedLine[0]) {
            stats.corrected++;
        }

        return new Episode(series, season, episode, title, airDate);
    }

    private int parseNumber(String value, boolean[] corrected) {
        if (value.isEmpty()) {
            corrected[0] = true;
            return 0;
        }
        try {
            int n = Integer.parseInt(value);
            if (n < 0) {
                corrected[0] = true;
                return 0;
            }
            return n;
        } catch (Exception e) {
            corrected[0] = true;
            return 0;
        }
    }

    private boolean isValidDate(String value) {

        try {
            LocalDate.parse(value); // expects ISO format YYYY-MM-DD
            return true;

        } catch (DateTimeParseException e) {
            return false;
        }
    }

    /**
     * Pads a CSV line with missing columns so it always has 5 fields.
     */
    private String[] pad(String[] arr) {

        String[] result = new String[EXPECTED_FIELDS];

        for (int i = 0; i < EXPECTED_FIELDS; i++) {
            result[i] = (i < arr.length) ? arr[i] : "";
        }

        return result;
    }
}