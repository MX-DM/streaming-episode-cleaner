package processing;

public class EpisodeNormalizer {

    public String normalize(String value, boolean[] correctedLine) {

        if (value == null) return "";

        String original = value;

        String normalized = value.trim()
                .replaceAll("\\s+", " ")
                .toLowerCase();

        if (!normalized.equals(original)) {
            correctedLine[0] = true;
        }

        return normalized;
    }
}
