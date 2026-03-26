package com.example.foodsdrinks.service.csv;

import com.example.foodsdrinks.config.MessageHelper;
import com.example.foodsdrinks.dto.csv.CsvResponse;
import com.example.foodsdrinks.dto.csv.importing.CategoryCsvErrorRow;
import com.example.foodsdrinks.dto.csv.importing.CategoryCsvRow;
import com.example.foodsdrinks.dto.csv.importing.CategoryKey;
import com.example.foodsdrinks.dto.csv.importing.ImportCsvResult;
import com.example.foodsdrinks.dto.csv.importing.RowValidationResult;
import com.example.foodsdrinks.entity.Category;
import com.example.foodsdrinks.mapper.CategoryCsvMapper;
import com.example.foodsdrinks.repository.CategoryRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryCsvImportService {

    private static final List<String> ERROR_COLUMNS = List.of("id", "name", "classify", "description", "error_reason");

    private final CategoryRepository categoryRepository;
    private final Validator validator;
    private final MessageHelper messageHelper;
    private final CategoryCsvMapper csvMapper;

    @Transactional
    public ImportCsvResult persist(List<CategoryCsvRow> rows) {
        CategoryLookup lookup  = loadLookup(rows);
        ImportContext  context = new ImportContext();

        List<RowValidationResult> results = validateAll(rows, context, lookup);

        long errorCount = results.stream().filter(RowValidationResult::hasError).count();
        if (errorCount > 0) {
            log.warn("CSV import aborted — {}/{} rows failed validation", errorCount, rows.size());
            return new ImportCsvResult.RowErrors(buildErrorCsv(results));
        }

        List<Category> toSave = results.stream()
                .map(r -> buildEntity(r.row(), lookup))
                .toList();
        categoryRepository.saveAll(toSave);
        log.info("CSV import successful — {} records saved", toSave.size());
        return new ImportCsvResult.Success(toSave.size());
    }

    // Private method --------------------------------------------------------------------------------------------------

    private List<RowValidationResult> validateAll(List<CategoryCsvRow> rows,
                                                  ImportContext context,
                                                  CategoryLookup lookup) {
        List<RowValidationResult> results = new ArrayList<>();
        for (CategoryCsvRow row : rows) {
            String error = validateRow(row, context, lookup);
            results.add(new RowValidationResult(row, error));
            if (error == null && !row.isUpdate()) context.track(row.toCategoryKey());
        }
        return results;
    }

    private CategoryLookup loadLookup(List<CategoryCsvRow> rows) {
        return new CategoryLookup(
                fetchExistingById(rows),
                fetchExistingByKey(rows));
    }

    private Map<Long, Category> fetchExistingById(List<CategoryCsvRow> rows) {
        Set<Long> updateIds = rows.stream()
                .filter(CategoryCsvRow::isUpdate)
                .flatMap(r -> r.getIdAsLong().stream())
                .collect(Collectors.toSet());

        if (updateIds.isEmpty()) return Map.of();

        return categoryRepository.findAllById(updateIds).stream()
                .collect(Collectors.toMap(Category::getId, c -> c));
    }

    private Map<CategoryKey, Long> fetchExistingByKey(List<CategoryCsvRow> rows) {
        Set<String> names = rows.stream()
                .map(CategoryCsvRow::getName)
                .filter(n -> n != null && !n.isBlank())
                .collect(Collectors.toSet());

        if (names.isEmpty()) return Map.of();

        return categoryRepository.findAllByNameIn(names).stream()
                .collect(Collectors.toMap(
                        c -> CategoryKey.of(c.getName(), c.getClassify()),
                        Category::getId,
                        (a, b) -> a));
    }

    private String validateRow(CategoryCsvRow row, ImportContext context, CategoryLookup lookup) {
        Set<ConstraintViolation<CategoryCsvRow>> violations = validator.validate(row);
        if (!violations.isEmpty()) {
            return violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .sorted()
                    .collect(Collectors.joining("; "));
        }
        return row.isUpdate()
                ? validateUpdate(row, lookup)
                : validateCreate(row, context, lookup);
    }

    private String validateCreate(CategoryCsvRow row, ImportContext context, CategoryLookup lookup) {
        CategoryKey key = row.toCategoryKey();
        if (context.contains(key))   return messageHelper.get("error.csv.category.row.duplicate");
        if (lookup.containsKey(key)) return messageHelper.get("error.category.already.exists");
        return null;
    }

    private String validateUpdate(CategoryCsvRow row, CategoryLookup lookup) {
        Optional<Long> parsedId = row.getIdAsLong();
        if (parsedId.isEmpty()) {
            return messageHelper.get("error.csv.category.row.id.invalid", row.getId());
        }
        long id = parsedId.get();
        if (!lookup.existsById(id)) {
            return messageHelper.get("error.csv.category.row.id.not.found", id);
        }
        Long conflictId = lookup.findConflictId(row.toCategoryKey());
        if (conflictId != null && !conflictId.equals(id)) {
            return messageHelper.get("error.category.already.exists");
        }
        return null;
    }

    private Category buildEntity(CategoryCsvRow row, CategoryLookup lookup) {
        if (!row.isUpdate()) return csvMapper.toNewCategory(row);
        Category existing = lookup.getById(row.getIdAsLong().orElseThrow());
        csvMapper.updateCategory(row, existing);
        return existing;
    }

    private CsvResponse buildErrorCsv(List<RowValidationResult> results) {
        List<CategoryCsvErrorRow> errorRows = results.stream()
                .map(r -> CategoryCsvErrorRow.from(r.row(), r.error()))
                .toList();
        byte[] content = CsvHelper.beansToCsvBytes(errorRows, CategoryCsvErrorRow.class, ERROR_COLUMNS);
        return new CsvResponse("import_errors.csv", "text/csv; charset=UTF-8", content);
    }

    private static final class ImportContext {
        private final Set<CategoryKey> seenKeys = new HashSet<>();

        boolean contains(CategoryKey key) { return seenKeys.contains(key); }
        void    track(CategoryKey key)    { seenKeys.add(key); }
    }

    private record CategoryLookup(
            Map<Long, Category> byId,
            Map<CategoryKey, Long> byKey) {

        boolean existsById(long id)          { return byId.containsKey(id); }
        Category getById(long id)            { return byId.get(id); }
        boolean containsKey(CategoryKey key) { return byKey.containsKey(key); }
        Long findConflictId(CategoryKey key) { return byKey.get(key); }
    }
}
