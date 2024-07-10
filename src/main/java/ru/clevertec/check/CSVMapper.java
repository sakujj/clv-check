package ru.clevertec.check;

import java.util.List;
import java.util.Map;
import java.util.SequencedMap;

public interface CSVMapper<T> {

    List<T> fromCSV(String csvHeaders, List<String> csvContent);

    SequencedMap<String, List<String>> toCSV(List<T> content);
}
