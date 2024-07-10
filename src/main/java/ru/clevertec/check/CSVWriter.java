package ru.clevertec.check;

import java.util.List;
import java.util.SequencedMap;

public interface CSVWriter {
    void writeRecordTables(String newFilePath, SequencedMap<String, List<String>> tables);
}
