package com.recipeharbor.mapper;

import com.recipeharbor.dto.RecipeDto;
import com.recipeharbor.entity.Recipe;
import org.springframework.stereotype.Component;

@Component
public class RecipeMapper {

    public Recipe recipeDtoToRecipeEntity(RecipeDto recipeDto) {

        return Recipe.builder()
                .id(recipeDto.getId()!=null?recipeDto.getId():null)
                .name(recipeDto.getName())
                .vegetarian(recipeDto.isVeg())
                .ingredients(recipeDto.getIngredients())
                .instructions(recipeDto.getInstructions())
                .servings(recipeDto.getServings())
                .build();

    }

    public RecipeDto recipeEntityToRecipeDto(Recipe recipe) {

        return RecipeDto.builder()
                .id(recipe.getId())
                .name(recipe.getName())
                .isVeg(recipe.isVegetarian())
                .ingredients(recipe.getIngredients())
                .instructions(recipe.getInstructions())
                .servings(recipe.getServings())
                .build();

    }
}
