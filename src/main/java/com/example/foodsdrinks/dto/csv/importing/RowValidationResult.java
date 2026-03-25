package com.example.foodsdrinks.dto.csv.importing;

public record RowValidationResult(CategoryCsvRow row, String error) {

    public boolean hasError() {
        return error != null;
    }
}
