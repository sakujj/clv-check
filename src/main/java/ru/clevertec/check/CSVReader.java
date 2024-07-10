package ru.clevertec.check;

import java.util.List;
import java.util.SequencedMap;

public interface CSVReader {
    SequencedMap<String, List<String>> readRecords(String fileName);
}
