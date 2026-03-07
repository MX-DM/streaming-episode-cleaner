package processing;

public class EpisodeNormalizer {

    public String normalize(String value) {

        if (value == null) return "";

        String trimmed = value.trim();

        return trimmed
                .replaceAll("\\s+", " ")
                .toLowerCase();
    }

}
