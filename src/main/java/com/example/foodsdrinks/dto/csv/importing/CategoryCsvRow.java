package com.example.foodsdrinks.dto.csv.importing;

import com.example.foodsdrinks.dto.csv.importing.converter.TrimStringConverter;
import com.example.foodsdrinks.entity.enums.Classify;
import com.opencsv.bean.CsvCustomBindByName;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Optional;

@Getter
@Setter
@NoArgsConstructor
public class CategoryCsvRow {

    @CsvCustomBindByName(column = "id", converter = TrimStringConverter.class, required = false)
    private String id;

    @NotBlank(message = "{validation.category.name.required}")
    @Size(max = 100, message = "{validation.category.name.size}")
    @CsvCustomBindByName(column = "name", converter = TrimStringConverter.class, required = false)
    private String name;

    @NotBlank(message = "{validation.category.classify.required}")
    @Pattern(regexp = "^(?i)(FOOD|DRINK)$", message = "{validation.category.classify.invalid}")
    @CsvCustomBindByName(column = "classify", converter = TrimStringConverter.class, required = false)
    private String classify;

    @Size(max = 1000, message = "{validation.category.description.size}")
    @CsvCustomBindByName(column = "description", converter = TrimStringConverter.class, required = false)
    private String description;

    // ── Semantic accessors ────────────────────────────────────────────────────

    public boolean isUpdate() {
        return id != null && !id.isBlank();
    }

    public Optional<Long> getIdAsLong() {
        if (!isUpdate()) return Optional.empty();
        try {
            return Optional.of(Long.parseLong(id));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    public Optional<Classify> getEnumClassify() {
        if (classify == null || classify.isBlank()) return Optional.empty();
        try {
            return Optional.of(Classify.valueOf(classify.toUpperCase()));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    public CategoryKey toCategoryKey() {
        return getEnumClassify()
                .map(c -> CategoryKey.of(name, c))
                .orElseThrow(() -> new IllegalStateException(
                        "toCategoryKey() called before classify validation passed"));
    }

    public String getDescriptionOrNull() {
        return description != null && !description.isBlank() ? description : null;
    }
}
