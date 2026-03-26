package com.example.foodsdrinks.service.csv;

import com.example.foodsdrinks.config.MessageHelper;
import com.example.foodsdrinks.dto.csv.exporting.CategoryExportRow;
import com.example.foodsdrinks.dto.csv.CsvResponse;
import com.example.foodsdrinks.dto.csv.importing.ImportCsvResult;
import com.example.foodsdrinks.exception.AppException;
import com.example.foodsdrinks.exception.ErrorCode;
import com.example.foodsdrinks.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Orchestrates the CSV import flow and handles export/template.
 * Import: delegates I/O to CategoryCsvParserService, DB work to CategoryCsvImportService.
 * All exceptions are caught here and converted to ImportCsvResult.Rejected —
 * callers receive a typed result, never a thrown exception.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryCsvService {

    private static final List<String> EXPORT_COLUMNS =
            List.of("id", "name", "classify", "description");

    private final CategoryRepository categoryRepository;
    private final CategoryCsvParserService parserService;
    private final CategoryCsvImportService importService;
    private final MessageHelper messageHelper;

    // ── Import ────────────────────────────────────────────────────────────────

    public ImportCsvResult importCsv(MultipartFile file) {
        try {
            var rows = parserService.parse(file);
            return importService.persist(rows);
        } catch (AppException ex) {
            log.warn("CSV import rejected — {}", ex.getMessage());
            return new ImportCsvResult.Rejected(
                    ex.getErrorCode().getStatus(),
                    messageHelper.get(ex.getErrorCode().getMessageKey(), ex.getArgs()));
        } catch (Exception ex) {
            log.error("Unexpected error during CSV import", ex);
            return new ImportCsvResult.Rejected(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    messageHelper.get(ErrorCode.INTERNAL_ERROR.getMessageKey()));
        }
    }

    // ── Export ────────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public CsvResponse exportCsv() {
        List<CategoryExportRow> rows = categoryRepository.findAll().stream()
                .map(c -> new CategoryExportRow(
                        String.valueOf(c.getId()),
                        c.getName(),
                        c.getClassify().name(),
                        c.getDescription() != null ? c.getDescription() : ""))
                .toList();
        return new CsvResponse("categories.csv", "text/csv; charset=UTF-8",
                CsvHelper.beansToCsvBytes(rows, CategoryExportRow.class, EXPORT_COLUMNS));
    }

    public CsvResponse downloadTemplate() {
        List<CategoryExportRow> samples = List.of(
                new CategoryExportRow("", "Coffee Black", "DRINK", "Coffee without milk or sugar"),
                new CategoryExportRow("", "Meat Ball",     "FOOD",  "Meat Ball with tomato sauce")
        );
        return new CsvResponse("categories_template.csv", "text/csv; charset=UTF-8",
                CsvHelper.beansToCsvBytes(samples, CategoryExportRow.class, EXPORT_COLUMNS));
    }
}
