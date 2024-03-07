package com.recipeharbor.service.impl;

import com.mongodb.client.result.DeleteResult;
import com.recipeharbor.dto.RecipeDto;
import com.recipeharbor.dto.SearchCriteriaDto;
import com.recipeharbor.entity.Recipe;
import com.recipeharbor.exception.RecipeNotFoundException;
import com.recipeharbor.mapper.RecipeMapper;
import com.recipeharbor.repository.RecipeRepository;
import com.recipeharbor.service.IRecipeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


/**
 * Author:  Ashwin Kumar
 *  Service Layer for CRUD and Search operations in Recipe Harbor Application
 */
@Service
@Slf4j
public class RecipeServiceImpl implements IRecipeService {

    private final RecipeRepository recipeRepository;
    private final RecipeMapper recipeMapper;
    private final MongoTemplate mongoTemplate;
    public RecipeServiceImpl(RecipeRepository recipeRepository,
                             RecipeMapper recipeMapper,
                             MongoTemplate mongoTemplate) {
        this.recipeRepository = recipeRepository;
        this.recipeMapper = recipeMapper;
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public RecipeDto createRecipe(RecipeDto recipeDto) {
        Recipe savedRecipe = recipeRepository.save(recipeMapper.recipeDtoToRecipeEntity(recipeDto));
        log.info("Recipe with id {} created", savedRecipe.getId());
        return recipeMapper.recipeEntityToRecipeDto(savedRecipe);
    }

    @Override
    public RecipeDto getRecipeById(String recipeId) {
        Optional<Recipe> recipeDb = recipeRepository.findById(recipeId);
        if(!recipeDb.isPresent()){
            log.error("Recipe with id {} not found", recipeId);
            throw new RecipeNotFoundException("Recipe with id '" + recipeId + "' not found");
        }
        log.info("Recipe with id {} found", recipeId);
        return recipeMapper.recipeEntityToRecipeDto(recipeDb.get());
    }

    @Override
    public RecipeDto updateRecipe(String recipeId, RecipeDto recipeDto) {

        Optional<Recipe> recipeToBeUpdated = recipeRepository.findById(recipeId);

        if (!recipeToBeUpdated.isPresent()) {
            log.error("Recipe with id {} not found", recipeId);
            throw new RecipeNotFoundException("Recipe with id '" + recipeId + "' not found");
        }

        Recipe finalRecipe = recipeMapper.recipeDtoToRecipeEntity(recipeDto);
        Recipe updatedRecipe = recipeRepository.save(updateOldValues(recipeToBeUpdated.get(), finalRecipe));
        log.info("Recipe with id {} updated", recipeId);
        return recipeMapper.recipeEntityToRecipeDto(updatedRecipe);
    }

    @Override
    public boolean deleteRecipe(String recipeId) {

        Query query = new Query(Criteria.where("id").is(recipeId));
        DeleteResult result = mongoTemplate.remove(query, Recipe.class);

        if (result.getDeletedCount() == 0) {
            log.error("Recipe with id {} not found", recipeId);
            throw new RecipeNotFoundException("Recipe with id '" + recipeId + "' not found");
        }
        log.info("Recipe with id {} deleted", recipeId);
        return true;
    }

    @Override
    public Page<RecipeDto> searchRecipe(SearchCriteriaDto searchCriteriaDto, int page, int size) {

        Criteria criteria = new Criteria();

        log.info("Searching for recipes with criteria: {}", searchCriteriaDto.toString());

        if(searchCriteriaDto.getVegetarian()!=null){
            criteria.and("vegetarian").is(searchCriteriaDto.getVegetarian());
        }
        if(searchCriteriaDto.getServings()!=null){
            criteria.and("servings").is(searchCriteriaDto.getServings());
        }
        if(searchCriteriaDto.getIncludeIngredients()!=null){
            criteria.and("ingredients").all(searchCriteriaDto.getIncludeIngredients());
        }
        if(searchCriteriaDto.getExcludeIngredients()!=null){
            criteria.and("ingredients").not().all(searchCriteriaDto.getExcludeIngredients());
        }
        if(searchCriteriaDto.getInstructionsText()!=null){
            String regex = searchCriteriaDto.getInstructionsText(); // Ensure full-text search
            criteria.and("instructions").elemMatch(Criteria.where("description").regex(regex, "i"));
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Query query = new Query(criteria).with(pageable);
        Query queryTotal = new Query(criteria);

        long count = mongoTemplate.count(queryTotal, Recipe.class);
        List<RecipeDto> recipeDtos = mongoTemplate.find(query, Recipe.class).stream().map(recipeMapper::recipeEntityToRecipeDto).toList();

        log.info("Found {} recipes matching the search criteria", recipeDtos.size());
        return PageableExecutionUtils.getPage(recipeDtos, pageable, () -> count);

    }

    private Recipe updateOldValues(Recipe recipeFromDb, Recipe finalRecipe) {
        finalRecipe.setId(recipeFromDb.getId());
        finalRecipe.setCreatedAt(recipeFromDb.getCreatedAt());
        return finalRecipe;
    }
}
