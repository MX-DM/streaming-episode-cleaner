package stats;

/**
 * Collects statistics during processing.
 * Used later to generate the data quality report.
 */

public class ProcessingStats {

    public int totalInput = 0;   // total lines processed
    public int totalOutput = 0;  // final cleaned episodes

    public int discarded = 0;    // invalid records removed during parsing
    public int corrected = 0;    // records that required fixes
    public int duplicates = 0;   // duplicates removed during deduplication

}