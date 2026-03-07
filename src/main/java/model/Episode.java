package model;

public class Episode {

    private String seriesName;
    private int seasonNumber;
    private int episodeNumber;
    private String episodeTitle;
    private String airDate;

    public Episode(String seriesName, int seasonNumber, int episodeNumber, String episodeTitle, String airDate) {
        this.seriesName = seriesName;
        this.seasonNumber = seasonNumber;
        this.episodeNumber = episodeNumber;
        this.episodeTitle = episodeTitle;
        this.airDate = airDate;
    }

    @Override
    public String toString() {
        return
                seriesName + ',' + ' ' +
                seasonNumber + ',' + ' ' +
                episodeNumber + ',' + ' ' +
                episodeTitle + ',' + ' ' +
                airDate
                ;
    }

    public String getSeriesName() { return seriesName; }
    public int getSeasonNumber() { return seasonNumber; }
    public int getEpisodeNumber() { return episodeNumber; }
    public String getEpisodeTitle() { return episodeTitle; }
    public String getAirDate() { return airDate; }

}
