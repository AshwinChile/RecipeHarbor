package com.recipeharbor.service;

import com.recipeharbor.dto.RecipeDto;
import com.recipeharbor.dto.SearchCriteriaDto;
import org.springframework.data.domain.Page;

/**
 * Author:  Ashwin Kumar
 *  Service Layer Contracts for CRUD and Search operations in Recipe Harbor Application
 */
public interface IRecipeService {

    RecipeDto createRecipe(RecipeDto recipeDto);
    RecipeDto getRecipeById(String recipeId);
    RecipeDto updateRecipe(String recipeId, RecipeDto recipeDto);
    boolean deleteRecipe(String recipeId);
    Page<RecipeDto> searchRecipe(SearchCriteriaDto searchCriteriaDto, int page, int size);
}
