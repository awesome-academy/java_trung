package com.example.foodsdrinks.dto.response;

public record CsvImportResponse(boolean success, int count, String message) {

    public static CsvImportResponse success(int count) {
        return new CsvImportResponse(true, count, null);
    }

    public static CsvImportResponse error(String message) {
        return new CsvImportResponse(false, 0, message);
    }
}
