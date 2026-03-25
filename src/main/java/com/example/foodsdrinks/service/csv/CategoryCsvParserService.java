package com.example.foodsdrinks.service.csv;

import com.example.foodsdrinks.dto.csv.importing.CategoryCsvRow;
import com.example.foodsdrinks.exception.AppException;
import com.example.foodsdrinks.exception.ErrorCode;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.input.BOMInputStream;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class CategoryCsvParserService {

    private static final long MAX_FILE_BYTES = 5 * 1024 * 1024L; // 5 MB

    private static final Set<String> VALID_CONTENT_TYPE_PREFIXES = Set.of(
            "text/csv", "text/plain", "application/vnd.ms-excel",
            "application/csv", "application/octet-stream"
    );

    public List<CategoryCsvRow> parse(MultipartFile file) {
        if (file.isEmpty())                  throw new AppException(ErrorCode.CSV_EMPTY);
        if (file.getSize() > MAX_FILE_BYTES) throw new AppException(ErrorCode.CSV_FILE_TOO_LARGE);
        validateContentType(file);

        List<CategoryCsvRow> rows = parseCsvFile(file);
        if (rows.isEmpty()) {
            log.info("CSV import: file contained no data rows");
            throw new AppException(ErrorCode.CSV_EMPTY);
        }
        return rows;
    }

    // ── Content-type ──────────────────────────────────────────────────────────

    private void validateContentType(MultipartFile file) {
        String ct = file.getContentType();
        if (ct == null || VALID_CONTENT_TYPE_PREFIXES.stream().noneMatch(ct.toLowerCase()::startsWith)) {
            log.warn("Rejected CSV upload — content-type: {}", ct);
            throw new AppException(ErrorCode.CSV_INVALID_FORMAT);
        }
    }

    // ── Parsing ───────────────────────────────────────────────────────────────

    private List<CategoryCsvRow> parseCsvFile(MultipartFile file) {
        try (BOMInputStream bomStream = BOMInputStream.builder()
                        .setInputStream(file.getInputStream()).get();
             Reader reader = new InputStreamReader(bomStream, StandardCharsets.UTF_8)) {

            return new CsvToBeanBuilder<CategoryCsvRow>(reader)
                    .withType(CategoryCsvRow.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .withIgnoreEmptyLine(true)
                    .build()
                    .parse();

        } catch (IOException e) {
            log.error("Failed to read CSV file: {}", e.getMessage(), e);
            throw new AppException(ErrorCode.CSV_INVALID_FORMAT);
        } catch (RuntimeException e) {
            log.error("Failed to parse CSV file: {}", e.getMessage(), e);
            throw new AppException(ErrorCode.CSV_INVALID_FORMAT);
        }
    }
}
