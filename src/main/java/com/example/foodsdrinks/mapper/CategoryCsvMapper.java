package com.example.foodsdrinks.mapper;

import com.example.foodsdrinks.dto.csv.importing.CategoryCsvRow;
import com.example.foodsdrinks.entity.Category;
import com.example.foodsdrinks.entity.enums.Classify;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface CategoryCsvMapper {

    @Mapping(target = "id",          ignore = true)
    @Mapping(target = "products",    ignore = true)
    @Mapping(target = "classify",    source = "classify",    qualifiedByName = "toClassify")
    @Mapping(target = "description", source = "description", qualifiedByName = "blankToNull")
    Category toNewCategory(CategoryCsvRow row);

    @Mapping(target = "id",          ignore = true)
    @Mapping(target = "products",    ignore = true)
    @Mapping(target = "createdAt",   ignore = true)
    @Mapping(target = "updatedAt",   ignore = true)
    @Mapping(target = "classify",    source = "classify",    qualifiedByName = "toClassify")
    @Mapping(target = "description", source = "description", qualifiedByName = "blankToNull")
    void updateCategory(CategoryCsvRow row, @MappingTarget Category category);

    @Named("toClassify")
    default Classify toClassify(String classify) {
        return Classify.valueOf(classify.toUpperCase());
    }

    @Named("blankToNull")
    default String blankToNull(String value) {
        return value != null && !value.isBlank() ? value : null;
    }
}
