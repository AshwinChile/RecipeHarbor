package com.recipeharbor.service.impl;

import com.mongodb.client.result.DeleteResult;
import com.recipeharbor.dto.RecipeDto;
import com.recipeharbor.dto.SearchCriteriaDto;
import com.recipeharbor.entity.Recipe;
import com.recipeharbor.entity.Steps;
import com.recipeharbor.exception.RecipeNotFoundException;
import com.recipeharbor.mapper.RecipeMapper;
import com.recipeharbor.repository.RecipeRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecipeServiceTest {

    private final String RECIPE_ID = "65e841e36ad9c545baf4c9e";
    @Mock
    private RecipeRepository recipeRepository;
    @Mock
    private RecipeMapper recipeMapper;
    @Mock
    private MongoTemplate mongoTemplate;
    @InjectMocks
    private RecipeServiceImpl recipeService;
    private RecipeDto recipeDto;
    private Recipe recipe;

    @BeforeEach
    void setUp() {

        recipeDto = RecipeDto.builder()
                .name("Orange and Tomato Juice")
                .ingredients(List.of("Oranges", "Tomatoes"))
                .isVeg(true)
                .servings(4)
                .instructions(List.of(Steps.builder()
                                .step_number(1)
                                .description("Peel the oranges and tomatoes")
                                .build(),
                        Steps.builder()
                                .step_number(2)
                                .description("Cut the oranges and tomatoes into small pieces")
                                .build(),
                        Steps.builder()
                                .step_number(3)
                                .description("Blend the oranges and tomatoes")
                                .build()))
                .build();

        recipe = Recipe.builder()
                .id(recipeDto.getId() != null ? recipeDto.getId() : null)
                .name(recipeDto.getName())
                .vegetarian(recipeDto.isVeg())
                .ingredients(recipeDto.getIngredients())
                .instructions(recipeDto.getInstructions())
                .servings(recipeDto.getServings())
                .build();
        recipe.setId(RECIPE_ID);

    }

    @Test
    void givenRecipeDTO_whenCreateRecipeInvoked_thenVerifyRecipeCreated() {

        //given
        when(recipeRepository.save(any(Recipe.class))).thenReturn(recipe);
        when(recipeMapper.recipeDtoToRecipeEntity(any(RecipeDto.class))).thenReturn(recipe);
        when(recipeMapper.recipeEntityToRecipeDto(any(Recipe.class))).thenReturn(recipeDto);

        //when
        RecipeDto savedRecipe = recipeService.createRecipe(recipeDto);

        //then
        assertThat(savedRecipe).isNotNull();
        verify(recipeRepository, times(1)).save(any(Recipe.class));

    }

    @Test
    void givenRecipeId_whenGetRecipeInvoked_thenReturnRecipeFound() {

        //given
        when(recipeRepository.findById(RECIPE_ID)).thenReturn(Optional.of(recipe));
        recipeDto.setId(recipe.getId());
        when(recipeMapper.recipeEntityToRecipeDto(any(Recipe.class))).thenReturn(recipeDto);

        //when
        RecipeDto fetchedRecipe = recipeService.getRecipeById(RECIPE_ID);

        //then
        assertThat(fetchedRecipe).isNotNull();
        assertThat(fetchedRecipe.getId()).isEqualTo(RECIPE_ID);
    }

    @Test
    void givenRecipeId_whenGetRecipeInvoked_thenReturnRecipeNotFound() {

        //given
        when(recipeRepository.findById(RECIPE_ID)).thenReturn(Optional.empty());
        //when
        Assertions.assertThrows(RecipeNotFoundException.class, () -> {
            recipeService.getRecipeById(RECIPE_ID);
        });
        //then
        verify(recipeMapper, never()).recipeEntityToRecipeDto(any(Recipe.class));
    }

    @Test
    void givenRecipeDTOAndRecipeId_whenUpdatingRecipe_thenReturnUpdatedRecipe() {

        //given
        when(recipeRepository.findById(RECIPE_ID)).thenReturn(Optional.of(recipe));

        //update the recipe
        Recipe updatedRecipe = Recipe.builder()
                .id(RECIPE_ID)
                .name(recipeDto.getName())
                .vegetarian(false)
                .ingredients(recipeDto.getIngredients())
                .instructions(recipeDto.getInstructions())
                .servings(3)
                .build();
        when(recipeMapper.recipeDtoToRecipeEntity(any(RecipeDto.class))).thenReturn(updatedRecipe);
        when(recipeRepository.save(any(Recipe.class))).thenReturn(updatedRecipe);

        RecipeDto updatedRecipeDto = RecipeDto.builder()
                .id(updatedRecipe.getId())
                .name(updatedRecipe.getName())
                .isVeg(updatedRecipe.isVegetarian())
                .ingredients(updatedRecipe.getIngredients())
                .instructions(updatedRecipe.getInstructions())
                .servings(updatedRecipe.getServings())
                .build();

        when(recipeMapper.recipeEntityToRecipeDto(any(Recipe.class))).thenReturn(updatedRecipeDto);

        //when
        RecipeDto latestRecipe = recipeService.updateRecipe(RECIPE_ID, recipeDto);

        //then
        assertThat(latestRecipe).isNotNull();
        assertThat(latestRecipe.getServings()).isEqualTo(3);
        assertThat(latestRecipe.isVeg()).isFalse();
    }

    @Test
    void givenRecipeDTOAndRecipeId_whenUpdatingRecipe_thenThrowsRecipeNotFoundError() {

        //given
        when(recipeRepository.findById(RECIPE_ID)).thenReturn(Optional.empty());
        //when
        Assertions.assertThrows(RecipeNotFoundException.class, () -> {
            recipeService.updateRecipe(RECIPE_ID, recipeDto);
        });
        //then
        verify(recipeMapper, never()).recipeDtoToRecipeEntity(any(RecipeDto.class));
        verify(recipeRepository, never()).save(any(Recipe.class));
    }

    @Test
    void givenRecipeId_whenDeletingRecipe_thenRecipeDeletedWithVoidReturn() {

        //given
        Query query = new Query(Criteria.where("id").is(RECIPE_ID));
        DeleteResult result = DeleteResult.acknowledged(1);
        when(mongoTemplate.remove(query, Recipe.class)).thenReturn(result);
        //when
        boolean isDeleted = recipeService.deleteRecipe(RECIPE_ID);
        //then
        assertThat(isDeleted).isTrue();
    }

    @Test
    void givenRecipeId_whenDeletingRecipe_thenThrowsRecipeNotFoundError() {

        //given
    //    Query query = new Query(Criteria.where("id").is(RECIPE_ID));
        DeleteResult result = DeleteResult.acknowledged(0);
        when(mongoTemplate.remove(any(Query.class), eq(Recipe.class))).thenReturn(result);
        //when
        Assertions.assertThrows(RecipeNotFoundException.class, () -> {
            recipeService.deleteRecipe(RECIPE_ID);
        });
    }

    @Test
    void givenSearchCriteriaAndPageAndSize_whenSearchingRecipesWithDefaultPageSize_thenReturnMatchingRecipes() {

        Recipe recipe2 = Recipe.builder()
                .id("75e841e36ad9c545baf4c9e")
                .name("Another recipe of oranges")
                .vegetarian(true)
                .ingredients(List.of("Oranges", "Tomatoes", "Mangoes"))
                .instructions(List.of(Steps.builder()
                                .step_number(1)
                                .description("Peel the oranges and tomatoes")
                                .build(),
                        Steps.builder()
                                .step_number(2)
                                .description("Cut the oranges and tomatoes into small pieces")
                                .build(),
                        Steps.builder()
                                .step_number(3)
                                .description("Blend the oranges and tomatoes")
                                .build(),
                        Steps.builder()
                                .step_number(4)
                                .description("Add mangoes and blend")
                                .build()))
                .servings(4)
                .build();

        RecipeDto recipeDto2 = RecipeDto.builder()
                .id(recipe2.getId())
                .name(recipe2.getName())
                .ingredients(recipe2.getIngredients())
                .isVeg(recipe2.isVegetarian())
                .servings(recipe2.getServings())
                .instructions(recipe2.getInstructions())
                .build();

        //given
        SearchCriteriaDto searchCriteriaDto = SearchCriteriaDto.builder()
                .vegetarian(true)
                .servings(4)
                .includeIngredients(List.of("Oranges"))
                .build();
        when(mongoTemplate.count(any(Query.class), eq(Recipe.class))).thenReturn(2L);
        when(mongoTemplate.find(any(Query.class), eq(Recipe.class))).thenReturn(List.of(recipe, recipe2));
        when(recipeMapper.recipeEntityToRecipeDto(recipe)).thenReturn(recipeDto);
        when(recipeMapper.recipeEntityToRecipeDto(recipe2)).thenReturn(recipeDto2);



        //when
        Page<RecipeDto> recipeDtos = recipeService.searchRecipe(searchCriteriaDto, 0, 10);

        //then
        assertThat(recipeDtos).isNotNull();
        assertThat(recipeDtos.getTotalElements()).isEqualTo(2);
        assertThat(recipeDtos.getContent().get(0).isVeg()).isTrue();
        assertThat(recipeDtos.getContent().get(1).getServings()).isEqualTo(4);
    }

    @Test
    void givenSearchCriteriaWithInstructions_whenSearchingRecipesWithDefaultPageSize_thenReturnMatchingRecipes() {

        Recipe recipe2 = Recipe.builder()
                .id("75e841e36ad9c545baf4c9e")
                .name("Another recipe of oranges")
                .vegetarian(true)
                .ingredients(List.of("Oranges", "Tomatoes", "Mangoes"))
                .instructions(List.of(Steps.builder()
                                .step_number(1)
                                .description("Peel the oranges and tomatoes")
                                .build(),
                        Steps.builder()
                                .step_number(2)
                                .description("Cut the oranges and tomatoes into small pieces")
                                .build(),
                        Steps.builder()
                                .step_number(3)
                                .description("Blend the oranges and tomatoes")
                                .build(),
                        Steps.builder()
                                .step_number(4)
                                .description("Add mangoes and blend")
                                .build()))
                .servings(4)
                .build();

        RecipeDto recipeDto2 = RecipeDto.builder()
                .id(recipe2.getId())
                .name(recipe2.getName())
                .ingredients(recipe2.getIngredients())
                .isVeg(recipe2.isVegetarian())
                .servings(recipe2.getServings())
                .instructions(recipe2.getInstructions())
                .build();

        //given
        SearchCriteriaDto searchCriteriaDto = SearchCriteriaDto.builder()
                .instructionsText("mango")
                .build();
        when(mongoTemplate.count(any(Query.class), eq(Recipe.class))).thenReturn(1L);
        when(mongoTemplate.find(any(Query.class), eq(Recipe.class))).thenReturn(List.of(recipe2));
        when(recipeMapper.recipeEntityToRecipeDto(any(Recipe.class))).thenReturn(recipeDto2);

        //when
        Page<RecipeDto> recipeDtos = recipeService.searchRecipe(searchCriteriaDto, 0, 10);

        //then
        assertThat(recipeDtos).isNotNull();
        assertThat(recipeDtos.getTotalElements()).isEqualTo(1);
        assertThat(recipeDtos.getContent().get(0)
                .getInstructions()
                .stream()
                .anyMatch(step -> step.getDescription().contains("mango"))).isTrue();
    }

    @Test
    void givenSearchCriteriaWithInstructionsAndPageSize_whenSearchingRecipesWithGivenPageAndSize_thenReturnMatchingRecipes() {

        Recipe recipe2 = Recipe.builder()
                .id("75e841e36ad9c545baf4c9e")
                .name("Another recipe of oranges")
                .vegetarian(true)
                .ingredients(List.of("Oranges", "Tomatoes", "Mangoes"))
                .instructions(List.of(Steps.builder()
                                .step_number(1)
                                .description("Peel the oranges and tomatoes")
                                .build(),
                        Steps.builder()
                                .step_number(2)
                                .description("Cut the oranges and tomatoes into small pieces")
                                .build(),
                        Steps.builder()
                                .step_number(3)
                                .description("Blend the oranges and tomatoes")
                                .build(),
                        Steps.builder()
                                .step_number(4)
                                .description("Add mangoes and blend")
                                .build()))
                .servings(4)
                .build();

        RecipeDto recipeDto2 = RecipeDto.builder()
                .id(recipe2.getId())
                .name(recipe2.getName())
                .ingredients(recipe2.getIngredients())
                .isVeg(recipe2.isVegetarian())
                .servings(recipe2.getServings())
                .instructions(recipe2.getInstructions())
                .build();

        //given
        int page = 0, size = 1;
        SearchCriteriaDto searchCriteriaDto = SearchCriteriaDto.builder()
                .vegetarian(true)
                .servings(4)
                .includeIngredients(List.of("Oranges"))
                .build();
        when(mongoTemplate.count(any(Query.class), eq(Recipe.class))).thenReturn(2L);
        when(mongoTemplate.find(any(Query.class), eq(Recipe.class))).thenReturn(List.of(recipe));
        when(recipeMapper.recipeEntityToRecipeDto(recipe)).thenReturn(recipeDto);



        //when
        Page<RecipeDto> recipeDtos = recipeService.searchRecipe(searchCriteriaDto, page, size);

        //then
        assertThat(recipeDtos).isNotNull();
        assertThat(recipeDtos.getTotalElements()).isEqualTo(2);
        assertThat(recipeDtos.getNumberOfElements()).isEqualTo(1);

    }
}