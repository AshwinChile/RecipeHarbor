package com.recipeharbor.controller;

import com.recipeharbor.dto.Ingredient;
import com.recipeharbor.dto.RecipeDto;
import com.recipeharbor.entity.Steps;

import java.util.List;

public class RecipeDtoTestDataBuilder {
    public static RecipeDto buildRecipeDto(String id, String name, int servings, List<Ingredient> ingredients, List<Steps> instructions, boolean veg) {
        return RecipeDto.builder()
                .id(id)
                .name(name)
                .servings(servings)
                .ingredients(ingredients)
                .instructions(instructions)
                .isVeg(veg)
                .build();
    }

    public static Steps buildInstructions(int stepNumber, String description) {
        return Steps.builder()
                .step_number(stepNumber)
                .description(description)
                .build();
    }

    public static Ingredient buildIngredient(String name, Integer quantity, String unit) {
        return Ingredient.builder()
                .name(name)
                .quantity(quantity)
                .unit(unit)
                .build();
    }
}
