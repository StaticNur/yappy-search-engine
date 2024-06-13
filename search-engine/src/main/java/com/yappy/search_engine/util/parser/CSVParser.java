/*
package com.yappy.search_engine.util.parser;

import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import com.yappy.search_engine.model.VideoFromExcel;
import org.springframework.stereotype.Component;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class CSVParser {

    public List<VideoFromExcel> parseCSVFile(String filePath) throws IOException, CsvException {
        List<VideoFromExcel> videoEntries = new ArrayList<>();
        try (CSVReader csvReader = new CSVReaderBuilder(new FileReader(filePath))
                .withCSVParser(new CSVParserBuilder()
                        .withSeparator(',')
                        .withIgnoreQuotations(true)
                        .build())
                .build()) {
            String[] values;
            csvReader.readNext();

            while ((values = csvReader.readNext()) != null) {
                if (values.length < 2) {
                    continue;
                }
                String url = values[0];
                String description = values[1];
                videoEntries.add(new VideoFromExcel(url, description));
            }
        }
        return videoEntries;
    }
}
*/
