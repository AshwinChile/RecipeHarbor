package com.recipeharbor.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recipeharbor.config.AbstractContainerBaseTest;
import com.recipeharbor.config.RecipePage;
import com.recipeharbor.dto.RecipeDto;
import com.recipeharbor.dto.SearchCriteriaDto;
import com.recipeharbor.mapper.RecipeMapper;
import com.recipeharbor.repository.RecipeRepository;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Slf4j
public class RecipeControllerTestIT extends AbstractContainerBaseTest {

    private static final String CREATE_RECIPE_API_PATH = "/api/recipe";
    private static final String GET_RECIPE_API_PATH = "/api/recipe/";
    private static final String UPDATE_RECIPE_API_PATH = "/api/recipe/";
    private static final String DELETE_RECIPE_API_PATH = "/api/recipe/";
    private static final String SEARCH_RECIPE_API_PATH = "/api/recipe/search";
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private RecipeRepository recipeRepository;
    @Autowired
    private RecipeMapper recipeMapper;
    private RecipeDto recipeDto;

    @BeforeEach
    void setUp() {
        recipeRepository.deleteAll();
        //Create a test RecipeDto object

        recipeDto = RecipeDtoTestDataBuilder
                .buildRecipeDto(null,
                        "Orange and Tomato Juice",
                        2,
                        List.of(RecipeDtoTestDataBuilder.buildIngredient("Oranges", 2, "cups"),
                                RecipeDtoTestDataBuilder.buildIngredient("Tomatoes", 3, "cups")),
                        List.of(RecipeDtoTestDataBuilder.buildInstructions(1, "Peel the oranges and tomatoes"),
                                RecipeDtoTestDataBuilder.buildInstructions(2, "Cut the oranges and tomatoes into small pieces"),
                                RecipeDtoTestDataBuilder.buildInstructions(2, "Blend the oranges and tomatoes")),
                        true);
    }

    @Test
    void givenRecipeDto_whenCreateRecipe_thenReturn201Created() throws Exception {
        //given -  RecipeDto above

        //when
        ResultActions response = mockMvc.perform(post(CREATE_RECIPE_API_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(recipeDto)));

        //then
        response.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", CoreMatchers.notNullValue()));
    }


    @Test
    void givenRecipeId_whenFetchRecipe_thenReturnRecipeAnd200Status() throws Exception {
        //given
        ResultActions response = mockMvc.perform(post(CREATE_RECIPE_API_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(recipeDto)));
        RecipeDto recipeDtoSaved =
                objectMapper.readValue(response.andReturn().getResponse().getContentAsString(), RecipeDto.class);
        log.info("Recipe saved with id {}", recipeDtoSaved.getId());

        //when
        response = mockMvc.perform(get(GET_RECIPE_API_PATH + recipeDtoSaved.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(recipeDto)));

        //then
        response.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", CoreMatchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", CoreMatchers.equalTo(recipeDtoSaved.getId())));
    }

    @Test
    void givenRecipeDto_whenUpdateRecipe_thenReturn200Ok() throws Exception {
        //given
        ResultActions response = mockMvc.perform(post(CREATE_RECIPE_API_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(recipeDto)));
        RecipeDto recipeDtoSaved =
                objectMapper.readValue(response.andReturn().getResponse().getContentAsString(), RecipeDto.class);
        log.info("Recipe saved with id {}", recipeDtoSaved.getId());

        //when
        recipeDtoSaved.setName("Orange and Tomato Juice New");
        response = mockMvc.perform(put(UPDATE_RECIPE_API_PATH + recipeDtoSaved.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(recipeDtoSaved)));

        //then
        response.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", CoreMatchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", CoreMatchers.equalTo(recipeDtoSaved.getId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", CoreMatchers.equalTo("Orange and Tomato Juice New")));
    }

    @Test
    void givenRecipeId_whenDeleteRecipe_thenReturn204() throws Exception {
        //given
        ResultActions response = mockMvc.perform(post(CREATE_RECIPE_API_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(recipeDto)));
        RecipeDto recipeDtoSaved =
                objectMapper.readValue(response.andReturn().getResponse().getContentAsString(), RecipeDto.class);
        log.info("Recipe saved with id {}", recipeDtoSaved.getId());

        //when
        response = mockMvc.perform(delete(DELETE_RECIPE_API_PATH + recipeDtoSaved.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(recipeDtoSaved)));
        log.info("Recipe deleted with id {}", recipeDtoSaved.getId());

        //then
        response.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    void givenSearchCriteria_whenSearchRecipe_thenReturnRecipesAnd200Status() throws Exception {
        //given
        prepareAndSaveSearchData();

        SearchCriteriaDto searchCriteriaDto = SearchCriteriaDto.builder()
                .servings(2)
                .includeIngredients(List.of("Onions"))
                .instructionsText("Take this and do that")
                .build();

        //when
        ResultActions response = mockMvc.perform(post(SEARCH_RECIPE_API_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(searchCriteriaDto))
                .param("page", "0")
                .param("size", "10"));

        RecipePage recipeDtoPage =
                objectMapper.readValue(response
                        .andReturn()
                        .getResponse()
                        .getContentAsString(), RecipePage.class);
        log.info("Recipes found {} and total count of elements: {}", recipeDtoPage.getContent(), recipeDtoPage.getTotalElements());

        //then
        response.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content", CoreMatchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalElements", CoreMatchers.equalTo(3)));
    }

    private void prepareAndSaveSearchData() {
        RecipeDto recipeDto1 = RecipeDtoTestDataBuilder
                .buildRecipeDto(null,
                        "Recipe 1",
                        2,
                        List.of(RecipeDtoTestDataBuilder.buildIngredient("Onions", 2, "cups"),
                                RecipeDtoTestDataBuilder.buildIngredient("Tomatoes", 3, "cups")),
                        List.of(RecipeDtoTestDataBuilder.buildInstructions(1, "Take this and do that"),
                                RecipeDtoTestDataBuilder.buildInstructions(2, "Take that and do this")),
                        true);
        RecipeDto recipeDto2 = RecipeDtoTestDataBuilder
                .buildRecipeDto(null,
                        "Recipe 2",
                        2,
                        List.of(RecipeDtoTestDataBuilder.buildIngredient("Onions", 2, "cups"),
                                RecipeDtoTestDataBuilder.buildIngredient("Tomatoes", 3, "cups"),
                                RecipeDtoTestDataBuilder.buildIngredient("Carrots", 3, "cups")),
                        List.of(RecipeDtoTestDataBuilder.buildInstructions(1, "Take this and do that"),
                                RecipeDtoTestDataBuilder.buildInstructions(2, "Take that and do this")),
                        true);

        RecipeDto recipeDto3 = RecipeDtoTestDataBuilder
                .buildRecipeDto(null,
                        "Recipe 3",
                        2,
                        List.of(RecipeDtoTestDataBuilder.buildIngredient("Onions", 2, "cups"),
                                RecipeDtoTestDataBuilder.buildIngredient("Tomatoes", 3, "cups"),
                                RecipeDtoTestDataBuilder.buildIngredient("Carrots", 3, "cups"),
                                RecipeDtoTestDataBuilder.buildIngredient("Mangoes", 3, "cups")),
                        List.of(RecipeDtoTestDataBuilder.buildInstructions(1, "Mix that"),
                                RecipeDtoTestDataBuilder.buildInstructions(2, "Blend this")),
                        true);

        RecipeDto recipeDto4 = RecipeDtoTestDataBuilder
                .buildRecipeDto(null,
                        "Recipe 4",
                        2,
                        List.of(RecipeDtoTestDataBuilder.buildIngredient("Onions", 2, "cups"),
                                RecipeDtoTestDataBuilder.buildIngredient("Melons", 3, "cups")),
                        List.of(RecipeDtoTestDataBuilder.buildInstructions(1, "Take this and do that"),
                                RecipeDtoTestDataBuilder.buildInstructions(2, "Do not for about mixing")),
                        true);


        List.of(recipeDto1, recipeDto2, recipeDto3, recipeDto4).stream().map(recipeMapper::recipeDtoToRecipeEntity).forEach(recipeRepository::save);
    }

}