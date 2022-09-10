package com.otaviomiguel.softwares.mochila.core.gateway;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.util.CollectionUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class CsvReader {
    private final CSVParser csvParser;

    public CsvReader(final String path) throws IOException {
        final Reader reader = new FileReader(path);
        final BufferedReader bufferedReader = new BufferedReader(reader);
        csvParser = new CSVParser(bufferedReader, CSVFormat.DEFAULT.withDelimiter(';'));
    }

    public int[] read() {
      final List<Integer> valores = new ArrayList<>();

      for (final CSVRecord csvRecord : csvParser) {
        valores.add(Integer.parseInt(csvRecord.get(0)));
      }

      return valores.stream().mapToInt(i->i).toArray();
    }
}