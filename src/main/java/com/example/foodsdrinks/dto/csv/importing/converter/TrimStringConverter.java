package com.example.foodsdrinks.dto.csv.importing.converter;

import com.opencsv.bean.AbstractBeanField;

public class TrimStringConverter extends AbstractBeanField<Object, String> {

    @Override
    protected Object convert(String value) {
        return value == null ? null : value.trim();
    }
}
