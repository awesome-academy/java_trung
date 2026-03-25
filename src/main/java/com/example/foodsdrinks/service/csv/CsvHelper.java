package com.example.foodsdrinks.service.csv;

import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import org.apache.commons.io.ByteOrderMark;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public final class CsvHelper {
    private CsvHelper() {}

    public static byte[] withBom(String content) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.writeBytes(ByteOrderMark.UTF_8.getBytes());
        out.writeBytes(content.getBytes(StandardCharsets.UTF_8));
        return out.toByteArray();
    }

    public static <T> byte[] beansToCsvBytes(List<T> rows, Class<T> type, List<String> columnOrder) {
        if (rows.isEmpty()) {
            // HeaderColumnNameMappingStrategy skips the header row for empty lists.
            // Write it manually using the same uppercase convention it applies to @CsvBindByName column values.
            String header = columnOrder.stream()
                    .map(String::toUpperCase)
                    .collect(Collectors.joining(",")) + "\n";
            return withBom(header);
        } else {
            StringWriter sw = new StringWriter();
            HeaderColumnNameMappingStrategy<T> strategy = new HeaderColumnNameMappingStrategy<>();
            strategy.setType(type);
            strategy.setColumnOrderOnWrite(
                    Comparator.comparingInt(col -> {
                        int i = columnOrder.indexOf(col.toLowerCase());
                        return i < 0 ? Integer.MAX_VALUE : i;
                    }));
            try {
                new StatefulBeanToCsvBuilder<T>(sw)
                        .withMappingStrategy(strategy)
                        .build()
                        .write(rows);
            } catch (CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
                throw new IllegalStateException("CSV serialization failed unexpectedly", e);
            }
            return withBom(sw.toString());
        }
    }
}
