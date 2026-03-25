package com.example.foodsdrinks.dto.csv.importing;

import com.example.foodsdrinks.entity.enums.Classify;

public record CategoryKey(String name, Classify classify) {

    public static CategoryKey of(String name, Classify classify) {
        return new CategoryKey(name, classify);
    }
}
