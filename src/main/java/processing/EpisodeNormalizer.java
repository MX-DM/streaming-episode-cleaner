package processing;

public class EpisodeNormalizer {

    public static String normalize(String input, boolean[]  correctedLine) {
        if (input == null) return "";

        String cleaned = input.trim().replaceAll("\\s+", " ");

        // Only count trailing spaces and extra spaces as a correction
        if (!cleaned.equals(input)) {
            correctedLine[0] = true;
        }

        // Return lowercased to ease comparison and for consistent results in cleaned csv. (Doesn't count as correction)
        return cleaned.toLowerCase();
    }
}
