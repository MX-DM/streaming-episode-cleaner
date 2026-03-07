package processing;

import model.Episode;
import stats.ProcessingStats;


public class EpisodeParser {

    public Episode parse(String line, ProcessingStats stats) {

        stats.totalInput++;

        String[] parts = line.split(",", -1);

        if (parts.length < 5) {
            parts = pad(parts);
        }

        String series = parts[0].trim();
        String seasonStr = parts[1].trim();
        String episodeStr = parts[2].trim();
        String title = parts[3].trim();
        String airDate = parts[4].trim();

        // Series is required
        if (series.isEmpty()) {
            stats.discarded++;
            return null;
        }

        boolean[] correctedLine = {false};

        int season = parseNumber(seasonStr, correctedLine);
        int episode = parseNumber(episodeStr, correctedLine);

        if (title.isEmpty()) {
            title = "Untitled Episode";
            correctedLine[0] = true;
        }

        if (airDate.isEmpty()) {
            airDate = "Unknown";
            correctedLine[0] = true;
        }
        else if (!isValidDate(airDate) && !airDate.equals("Unknown")) {
            airDate = "Unknown";
            correctedLine[0] = true;
        }

        // --- Discard rule ---
        if (episode == 0 && title.equals("Untitled Episode") && airDate.equals("Unknown")) {
            stats.discarded++;
            return null;
        }

        // --- Count correction (only if not discarded) ---
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
        return value.matches("\\d{4}-\\d{2}-\\d{2}");
    }

    private String[] pad(String[] arr) {

        String[] result = new String[5];

        for (int i = 0; i < 5; i++) {
            if (i < arr.length) result[i] = arr[i];
            else result[i] = "";
        }

        return result;
    }
}

