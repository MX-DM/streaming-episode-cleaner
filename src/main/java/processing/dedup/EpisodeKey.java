package processing.dedup;

import java.util.Objects;

public class EpisodeKey {

    private final String series;
    private final int season;
    private final int episode;
    private final String title;

    public EpisodeKey(String series, int season, int episode, String title) {
        this.series = series;
        this.season = season;
        this.episode = episode;
        this.title = title;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EpisodeKey)) return false;
        EpisodeKey that = (EpisodeKey) o;
        return season == that.season &&
                episode == that.episode &&
                Objects.equals(series, that.series) &&
                Objects.equals(title, that.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(series, season, episode, title);
    }
}