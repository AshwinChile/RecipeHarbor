package com.recipeharbor.dto;

import com.recipeharbor.entity.Steps;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Schema(
        name = "Recipe DTO",
        description = "Schema to hold Recipe information"
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecipeDto {

    @Schema(
            description = "Autogenerated Recipe ID", example = "65e841e36ad9c545baf4c9ef"
    )
    private String id;

    @Schema(
            description = "Name of the Recipe", example = "Pesto Pasta"
    )
    @NotEmpty(message = "Name of the recipe is required")
    @Size(min = 5, max = 30, message = "The length name should be between 5 and 30")
    private String name;

    @Schema(
            description = "Type of Recipe (Vegetarian or Non Vegetarian)", example = "True or False"
    )
    @NotNull(message = "Veg/Non-veg information is required")
    private boolean isVeg;

    @Schema(
            description = "Number of persons served by this recipe", example = "3"
    )
    @Min(value = 1, message = "Number of servings must be a positive integer")
    @Max(value = 20, message = "Number of servings cannot exceed 20")
    private Integer servings;

    @Schema(
            description = "The Ingredients of the dish", example = "{\"name\": \"Potatoes\", \"quantity\": 1, \"unit\": \"unit\"}"
    )
    @NotEmpty(message = "Ingredients of the recipe are required")
    @Size(min = 1, message = "At least one ingredient is required")
    private List<Ingredient> ingredients;

    @Schema(
            description = "Instructions on how to use this recipe", example = "Boil the pasta, add the pesto sauce and mix well. Serve hot."
    )
    @NotEmpty(message = "Instructions for the recipe are required")
    @Size(min = 1, message = "At least one instruction is required")
    private List<Steps> instructions;

}
