package com.example.foodsdrinks.dto.csv.importing;

import com.example.foodsdrinks.dto.csv.CsvResponse;
import org.springframework.http.HttpStatus;

public sealed interface ImportCsvResult
        permits ImportCsvResult.Success, ImportCsvResult.RowErrors, ImportCsvResult.Rejected {

    record Success(int count)              implements ImportCsvResult {}
    record RowErrors(CsvResponse errorCsv) implements ImportCsvResult {}
    record Rejected(HttpStatus status, String message) implements ImportCsvResult {}
}
