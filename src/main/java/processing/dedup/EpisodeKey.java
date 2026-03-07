package processing.dedup;

import java.util.Objects;

public class EpisodeKey {

    String series;
    int season;
    int episode;
    String title;

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

        EpisodeKey key = (EpisodeKey) o;

        return season == key.season &&
                episode == key.episode &&
                Objects.equals(series, key.series) &&
                Objects.equals(title, key.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(series, season, episode, title);
    }
}