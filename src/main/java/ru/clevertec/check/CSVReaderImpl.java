package ru.clevertec.check;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.SequencedMap;

public class CSVReaderImpl implements CSVReader {

    public static final String DEFAULT_CSV_SEPARATOR = ";";

    private String separator = DEFAULT_CSV_SEPARATOR;

    public String getSeparator() {
        return separator;
    }

    public void setSeparator(String separator) {
        this.separator = separator;
    }

    public SequencedMap<String, List<String>> readRecords(String fileName) {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName, StandardCharsets.UTF_8))) {

            SequencedMap<String, List<String>> tables = new LinkedHashMap<>();

            String line = null;
            while ((line = reader.readLine()) != null) {
                String strippedLine = line.strip();
                if (strippedLine.isEmpty()) {
                    continue;
                }

                String[] columns = line.split(separator, -1);
                Arrays.stream(columns)
                        .forEach(c -> {
                            if (c.isBlank()) {
                                throw new RuntimeException("CSV column can not be blank");
                            }
                        });

                String tableHeaders = line;
                tables.putIfAbsent(tableHeaders, new ArrayList<>());
                List<String> tableContent = tables.get(tableHeaders);

                final int columnsCount = columns.length;
                while ((line = reader.readLine()) != null && !line.isEmpty()) {
                    String[] record = line.split(separator, -1);
                    if (columnsCount != record.length) {
                        throw new RuntimeException(String.format("Record '%s' should have had %d columns but had %d",
                                line, columnsCount, record.length));
                    }
                    tableContent.add(line);
                }

                if (line == null) {
                    return tables;
                }
            }

            return tables;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
