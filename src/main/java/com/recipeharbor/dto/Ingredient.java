package com.recipeharbor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.TextIndexed;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Ingredient {

    private String name;
    private Integer quantity;
    private String unit;
}
