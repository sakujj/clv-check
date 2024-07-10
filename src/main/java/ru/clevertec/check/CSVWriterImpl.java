package ru.clevertec.check;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map.Entry;
import java.util.SequencedMap;

public class CSVWriterImpl implements CSVWriter {

    private static final String CRLF = "\r\n";

    public void writeRecordTables(String newFilePath, SequencedMap<String, List<String>> tables) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(newFilePath, StandardCharsets.UTF_8))) {

            for (Entry<String, List<String>> table : tables.entrySet()) {
                String tableHeaders = table.getKey();
                writer.write(tableHeaders);
                writer.write(CRLF);

                List<String> records = table.getValue();
                for (String record : records) {
                    writer.write(record);
                    writer.write(CRLF);
                }
                writer.write(CRLF);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
