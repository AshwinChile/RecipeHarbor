package com.recipeharbor.dto;

import com.recipeharbor.validators.AtleastOneField;
import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(
            description = "Type of Recipe (Vegetarian or Non Vegetarian)", example = "True or False"
    )
    private Boolean vegetarian;

    @Schema(
            description = "Number of persons served by this recipe", example = "3"
    )
    private Integer servings;

    @Schema(
            description = "Ingredients present in the recipe ", example = "Oranges, Apples"
    )
    private List<String> includeIngredients;

    @Schema(
            description = "Ingredients that should not be present in the recipe ", example = "Mint"
    )
    private List<String> excludeIngredients;

    @Schema(
            description = "Instructions for the recipe", example = "Boil"
    )
    private String instructionsText;
}
