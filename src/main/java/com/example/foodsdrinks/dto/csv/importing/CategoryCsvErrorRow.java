package com.example.foodsdrinks.dto.csv.importing;

import com.example.foodsdrinks.dto.csv.exporting.CategoryExportRow;
import com.opencsv.bean.CsvBindByName;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CategoryCsvErrorRow extends CategoryExportRow {

    @CsvBindByName(column = "error_reason")
    private String errorReason;

    public CategoryCsvErrorRow(String id, String name, String classify, String description, String errorReason) {
        super(id, name, classify, description);
        this.errorReason = errorReason;
    }

    public static CategoryCsvErrorRow from(CategoryCsvRow row, String error) {
        return new CategoryCsvErrorRow(
                row.getId(), row.getName(), row.getClassify(), row.getDescription(), error);
    }
}
