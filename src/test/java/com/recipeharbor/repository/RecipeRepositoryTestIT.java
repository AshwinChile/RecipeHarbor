package com.recipeharbor.repository;

import com.recipeharbor.config.AbstractContainerBaseTest;
import com.recipeharbor.config.MongoConfig;
import com.recipeharbor.config.MongoTestConfig;
import com.recipeharbor.controller.RecipeDtoTestDataBuilder;
import com.recipeharbor.dto.RecipeDto;
import com.recipeharbor.entity.Recipe;
import com.recipeharbor.entity.Steps;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataMongoTest
@Import({MongoConfig.class, MongoTestConfig.class})
@Slf4j
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class RecipeRepositoryTestIT extends AbstractContainerBaseTest {

    @Autowired private RecipeRepository recipeRepository;
    @Autowired private MongoTemplate mongoTemplate;

    private RecipeDto recipeDto;
    private Recipe recipe;

    private final String RECIPE_ID = "65e841e36ad9c545baf4c9e";

    @BeforeEach
    void setUp() {

        recipeRepository.deleteAll();
        recipeDto = RecipeDtoTestDataBuilder
                .buildRecipeDto(null,
                        "Recipe 4",
                        2,
                        List.of(RecipeDtoTestDataBuilder.buildIngredient("Oranges", 2, "units"),
                                RecipeDtoTestDataBuilder.buildIngredient("Tomatoes", 3, "cups")),
                        List.of(RecipeDtoTestDataBuilder.buildInstructions(1, "Peel the oranges and tomatoes"),
                                RecipeDtoTestDataBuilder.buildInstructions(2, "Cut the oranges and tomatoes into small pieces"),
                                RecipeDtoTestDataBuilder.buildInstructions(3, "Blend the oranges and tomatoes")),
                        true);

        recipe = Recipe.builder()
                .id(recipeDto.getId() != null ? recipeDto.getId() : null)
                .name(recipeDto.getName())
                .vegetarian(recipeDto.isVeg())
                .ingredients(recipeDto.getIngredients())
                .instructions(recipeDto.getInstructions())
                .servings(recipeDto.getServings())
                .build();
    //    recipe.setId(RECIPE_ID);

    }

    @Test
    void givenRecipe_whenSave_thenReturnSavedRecipe() {
        Recipe savedRecipe = recipeRepository.save(recipe);
        log.info("Recipe with id {} created", savedRecipe.getId());
        assertNotNull(savedRecipe);
        assertThat(savedRecipe.getId()).isNotNull();
    }

    @Test
    void givenRecipeId_whenFindById_thenReturnRecipe() {
        Recipe saved = recipeRepository.save(recipe);
        Optional<Recipe> recipeDb = recipeRepository.findById(saved.getId());
        assertTrue(recipeDb.isPresent());
        assertThat(recipeDb.get().getId()).isEqualTo(saved.getId());
        assertThat(recipeDb.stream().count()).isEqualTo(1);
        log.info("Recipe found {}", recipeDb.get());
    }

    @Test
    void givenRecipeId_whenDeleteById_thenRecipeShouldBeDeleted() {
        Recipe saved = recipeRepository.save(recipe);
        recipeRepository.deleteById(saved.getId());
        Optional<Recipe> recipeDb = recipeRepository.findById(saved.getId());
        assertTrue(recipeDb.isEmpty());
        log.info("Recipe with id {} deleted", saved.getId());
    }


    @Test
    void givenRecipe_whenUpdate_thenReturnUpdatedRecipe() {
        Recipe saved = recipeRepository.save(recipe);
        log.info("Recipe with id {} created", saved);
        saved.setServings(10);
        saved.setIngredients(List.of(RecipeDtoTestDataBuilder.buildIngredient("Oranges", 2, "units"),
                RecipeDtoTestDataBuilder.buildIngredient("Tomatoes", 3, "cups"),
                RecipeDtoTestDataBuilder.buildIngredient("Mint", 4, "grams")));

        Recipe updatedRecipe = recipeRepository.save(saved);
        assertThat(updatedRecipe.getId()).isEqualTo(saved.getId());
        assertThat(updatedRecipe.getServings()).isEqualTo(saved.getServings());
        assertThat(updatedRecipe.getIngredients()).isEqualTo(saved.getIngredients());
        log.info("Recipe with id {} updated", updatedRecipe);
    }

    @Test
    void givenQueryAndSearchCriteria_whenFindByQuery_thenReturnRecipe() {
        Criteria criteria = new Criteria();
        String searchString = "Peel the orange";
        criteria.and("instructions").elemMatch(Criteria.where("description").regex(searchString, "i"));

        Recipe saved = recipeRepository.save(recipe);
        Pageable pageable = PageRequest
                .of(0, 10, Sort.by("createdAt").descending());
        Query query = new Query(criteria).with(pageable);

        List<Recipe> recipeList = mongoTemplate.find(query, Recipe.class);

        assertThat(recipeList).isNotNull();
        assertThat(recipeList.get(0).getInstructions()
                .stream()
                .anyMatch(steps -> steps.getDescription().contains(searchString))).isTrue();
        log.info("Recipe found {}", recipeList.get(0));

    }

    @Test
    void givenQueryAndSearchCriteria_whenFindByQueryWithPage0AndSize1_thenReturnTotalCountOfRecords() {
        Criteria criteria = new Criteria();
        String searchString = "Peel the orange";
        criteria.and("instructions").elemMatch(Criteria.where("description").regex(searchString, "i"));

        Recipe savedFirst = recipeRepository.save(recipe);

        Recipe recipeTwo = Recipe.builder()
                .id(recipeDto.getId() != null ? recipeDto.getId() : null)
                .name(recipeDto.getName())
                .vegetarian(recipeDto.isVeg())
                .ingredients(recipeDto.getIngredients())
                .instructions(recipeDto.getInstructions())
                .servings(recipeDto.getServings())
                .build();
        Recipe savedSecond = recipeRepository.save(recipeTwo);

        Query countQuery = new Query(criteria);
        Pageable pageable = PageRequest
                .of(0, 1, Sort.by("createdAt").descending());
        Query query = new Query(criteria).with(pageable);

        long count = mongoTemplate.count(countQuery, Recipe.class);
        List<Recipe> recipeList = mongoTemplate.find(query, Recipe.class);

        log.info("Total count of recipes found {}", count);
        log.info("Recipes found {}", recipeList);
        assertThat(count).isEqualTo(2);
        assertThat(recipeList.size()).isEqualTo(1);

    }


}