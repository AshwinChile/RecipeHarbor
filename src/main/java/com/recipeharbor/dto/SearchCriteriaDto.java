package com.recipeharbor.dto;

import com.recipeharbor.validators.AtleastOneField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@AtleastOneField
@NoArgsConstructor
@Builder
public class SearchCriteriaDto {
    private Boolean vegetarian;
    private Integer servings;
    private List<String> includeIngredients;
    private List<String> excludeIngredients;
    private String instructionsText;
}
