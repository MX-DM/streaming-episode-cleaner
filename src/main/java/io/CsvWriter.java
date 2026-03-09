package io;

import model.Episode;

import java.io.*;
import java.util.*;

/**
 * Writes the cleaned episode catalog into a CSV file.
 */

public class CsvWriter {

    public void writeEpisodes(String path, List<Episode> episodes) throws IOException {

        BufferedWriter writer = new BufferedWriter(new FileWriter(path));

        writer.write("SeriesName,SeasonNumber,EpisodeNumber,EpisodeTitle,AirDate\n");

        for (Episode e : episodes) {

            writer.write(
                    e.getSeriesName() + "," +
                            e.getSeasonNumber() + "," +
                            e.getEpisodeNumber() + "," +
                            e.getEpisodeTitle() + "," +
                            e.getAirDate()
            );

            writer.newLine();
        }

        writer.close();
    }
}
