# Streaming Episode Cleaner

## Overview

**Streaming Episode Cleaner** is a Java application designed to clean and normalize television episode datasets stored in CSV format.

The program processes raw episode data, applies validation and normalization rules, detects duplicate records, and produces a consistent catalog of episodes.

The final output includes:

* A cleaned and deduplicated CSV file
* A data quality report summarizing processing results

This project demonstrates practical techniques for handling **imperfect datasets**, including normalization, validation, duplicate detection, and deterministic output generation.

---

# Project Structure

```
streaming-episode-cleaner
│
├─ src/main/java
│   ├─ model
│   │   └─ Episode
│   │
│   ├─ io
│   │   ├─ CsvReader
│   │   └─ CsvWriter
│   │
│   ├─ processing
│   │   ├─ EpisodeProcessor
│   │   ├─ EpisodeParser
│   │   ├─ EpisodeNormalizer
│   │   │
│   │   └─ dedup
│   │       ├─ EpisodeDeduplicator
│   │       └─ EpisodeKey
│   │
│   ├─ stats
│   │   └─ ProcessingStats
│   │
│   ├─ report
│   │   └─ ReportGenerator
│   │
│   └─ Main
│
├─ src/main/resources/input/episodes.csv
├─ episodes_clean.csv
├─ report.md
└─ README.md
```

The architecture separates responsibilities into clear components:

| Component             | Responsibility                        |
| --------------------- | ------------------------------------- |
| `CsvReader`           | Reads raw CSV input                   |
| `EpisodeParser`       | Parses, validates and cleans records  |
| `EpisodeNormalizer`   | Normalizes textual fields             |
| `EpisodeDeduplicator` | Detects and merges duplicate episodes |
| `EpisodeProcessor`    | Coordinates the processing pipeline   |
| `CsvWriter`           | Writes the cleaned dataset            |
| `ReportGenerator`     | Generates the data quality report     |

---

## Requirements

The project requires the following tools installed on your system:

* **Java 17 or newer**
* **Apache Maven 3.8+**

You can verify the installation with:

```
java -version
mvn -version
```

---

## How to Run

1. Place your input CSV file inside the following directory:

```
src/main/resources/input/
```

The program will automatically detect the **first `.csv` file** found in this folder.
The file name does **not** need to follow any specific convention.

Example:

```
src/main/resources/input/
    dataset.csv
```

2. Compile the project:

```
mvn compile
```

3. Run the application:

```
mvn exec:java
```

---

## Customizing the Input Directory

By default, the program looks for input files in:

```
src/main/resources/input/
```

This path is defined inside the `CsvReader` class:

```java
private static final String INPUT_FOLDER = "src/main/resources/input";
```

If desired, this value can be modified to point to a different directory depending on your environment or workflow.


---

# Processing Pipeline

The program processes episode data through the following stages:

1. **Read Input CSV**
2. **Parse Records**
3. **Normalize Fields**
4. **Validate Data**
5. **Deduplicate Episodes**
6. **Sort Final Output**
7. **Generate Output Files**

This pipeline ensures that inconsistent or incomplete records are handled safely before the final catalog is produced.

---

# Data Cleaning Rules

During parsing and normalization, several rules are applied to improve data consistency.

### Text Normalization

Text fields (series name and episode title) are normalized by:

* trimming leading and trailing spaces
* collapsing multiple spaces into one
* converting text to lowercase

Example:

```
"  The   Office  " → "the office"
```

Lowercasing is applied only to simplify comparisons and is not counted as a correction.

---

### Numeric Fields

Season and episode numbers are parsed as integers.

Invalid values are corrected as follows:

| Input           | Result |
| --------------- | ------ |
| empty           | 0      |
| negative number | 0      |
| invalid text    | 0      |

---

### Air Date Validation

Air dates must follow ISO format:

```
YYYY-MM-DD
```

If a date is missing or invalid, it is replaced with:

```
unknown
```

---

### Missing Episode Title

If the episode title is missing, it is replaced with:

```
untitled episode
```

---

### Discard Rule

A record is discarded if it lacks sufficient information to identify an episode.

Specifically, records are removed when:

* episode number = `0`
* title = `"untitled episode"`
* air date = `"unknown"`

Such records cannot reliably represent an episode.

---

# Deduplication Strategy

Duplicate episodes may appear in the dataset because some records have missing fields (for example, unknown season numbers or missing episode numbers).

To handle this, the system uses a **multi-key matching strategy** combined with a **transitive deduplication algorithm**.

The goal is to identify episodes that represent the same real-world entity even when some metadata is incomplete.

---

# Matching Keys

Each episode generates three possible identification keys.
These keys represent the three matching rules defined for the deduplication process.

```java
private List<EpisodeKey> generateKeys(Episode ep) {

    List<EpisodeKey> keys = new ArrayList<>();

    String series = ep.getSeriesName();
    int season = ep.getSeasonNumber();
    int episode = ep.getEpisodeNumber();
    String title = ep.getEpisodeTitle();

    keys.add(new EpisodeKey(series, season, episode, null));
    keys.add(new EpisodeKey(series, 0, episode, title));
    keys.add(new EpisodeKey(series, season, 0, title));

    return keys;
}
```

These correspond to the following matching strategies:

| Rule            | Key                           | Purpose                                             |
| --------------- | ----------------------------- | --------------------------------------------------- |
| Exact episode   | `(series, season, episode)`   | Used when both season and episode numbers are known |
| Unknown season  | `(series, 0, episode, title)` | Allows matching when season is missing              |
| Unknown episode | `(series, season, 0, title)`  | Allows matching when episode number is missing      |

Using multiple keys allows the algorithm to match records even when some information is incomplete.

---

# Deduplication Algorithm

The deduplication process iterates through the list of parsed episodes.

Two main structures are used:

| Structure      | Purpose                             |
| -------------- | ----------------------------------- |
| `keyIndex`     | Maps keys to the best known episode |
| `keptEpisodes` | Stores the final deduplicated set   |

These are defined in the algorithm as:

```java
Map<EpisodeKey, Episode> keyIndex = new HashMap<>();
Set<Episode> keptEpisodes = new LinkedHashSet<>();
```

For every episode in the dataset:

1. Generate all matching keys.
2. Check if any of those keys already exist in `keyIndex`.
3. If matches exist, collect the connected episodes as duplicates.
4. Select the best episode among them.
5. Replace the group with the selected episode.
6. Update the index so all keys point to the chosen record.

The duplicate search step is implemented as:

```java
Set<Episode> duplicates = new HashSet<>();

for (EpisodeKey key : keys) {
    Episode existing = keyIndex.get(key);
    if (existing != null) {
        duplicates.add(existing);
    }
}
```

If no duplicates are found, the episode becomes part of the final catalog:

```java
keptEpisodes.add(episode);

for (EpisodeKey key : keys) {
    keyIndex.put(key, episode);
}
```

If duplicates exist, the algorithm resolves the conflict.

---

# Selecting the Best Record

When multiple records represent the same episode, the algorithm chooses the best one based on data quality.

The selection logic is implemented in `chooseBest()`:

```java
private Episode chooseBest(Episode a, Episode b) {

    if (a.hasValidAirDate() && !b.hasValidAirDate()) return a;
    if (!a.hasValidAirDate() && b.hasValidAirDate()) return b;

    if (a.hasValidTitle() && !b.hasValidTitle()) return a;
    if (!a.hasValidTitle() && b.hasValidTitle()) return b;

    if (a.hasValidNumbers() && !b.hasValidNumbers()) return a;
    if (!a.hasValidNumbers() && b.hasValidNumbers()) return b;

    return a;
}
```

Priority rules:

1. Episode with a **valid air date**
2. Episode with a **known title**
3. Episode with **valid season and episode numbers**

If both records have equal quality, the first one encountered is kept.

---

# Transitive Deduplication Example (Step-by-Step)

Consider the following three records:

```
A: Lost,2,5,The Hatch,Unknown
B: Lost,0,5,The Hatch,2005-10-19
C: Lost,2,0,The Hatch,Unknown
```

### Step 1 — Process Episode A

Generated keys:

```
(lost,2,5)
(lost,0,5,the hatch)
(lost,2,0,the hatch)
```

State of the structures:

```
keyIndex:
(lost,2,5) -> A
(lost,0,5,the hatch) -> A
(lost,2,0,the hatch) -> A

keptEpisodes:
{A}
```

---

### Step 2 — Process Episode B

Generated keys:

```
(lost,0,5)
(lost,0,5,the hatch)
(lost,0,0,the hatch)
```

The key `(lost,0,5,the hatch)` already exists in `keyIndex`, so the algorithm detects a duplicate.

```
duplicates = {A}
```

Now the algorithm compares A and B.

Since B has a **valid air date**, it becomes the best episode.

The cluster `{A, B}` is replaced with B.

Updated state:

```
keyIndex:
(lost,2,5) -> B
(lost,0,5,the hatch) -> B
(lost,2,0,the hatch) -> B
(lost,0,5) -> B
(lost,0,0,the hatch) -> B

keptEpisodes:
{B}
```

---

### Step 3 — Process Episode C

Generated keys:

```
(lost,2,0)
(lost,0,0,the hatch)
(lost,2,0,the hatch)
```

The key `(lost,0,0,the hatch)` already exists in the index and points to B.

```
duplicates = {B}
```

Now the algorithm compares B and C.

B remains the best record because it has the most complete information.

Final state:

```
keptEpisodes:
{B}
```

Resulting record:

```
Lost,2,5,The Hatch,2005-10-19
```

---

# Why This Approach Works

This strategy provides several advantages:

• It tolerates incomplete records
• It supports **transitive duplicate detection**
• It guarantees deterministic results
• It runs in near **O(n)** time thanks to the hash index

The result is a robust deduplication process suitable for noisy real-world datasets.


# Output Files

The program generates two outputs:

### Cleaned Dataset

```
episodes_clean.csv
```

Contains the final deduplicated and sorted catalog.

### Data Quality Report

```
report.md
```

Contains processing statistics including:

* total input records
* total output records
* discarded entries
* corrected entries
* duplicates detected

---

# Design Considerations

This implementation focuses on:

* robustness against incomplete data
* deterministic output generation
* modular architecture
* clear separation of responsibilities

The deduplication algorithm supports **transitive matching**, allowing it to detect duplicate clusters even when some records are incomplete.

---

# Possible Improvements

Future improvements could include:

* configurable input/output paths
* improved date validation
* unit tests for parsing and deduplication logic

---

# Conclusion

The Streaming Episode Cleaner demonstrates practical techniques for transforming messy CSV datasets into a reliable catalog through normalization, validation, and intelligent duplicate detection.
