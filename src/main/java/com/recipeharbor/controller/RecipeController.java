package com.recipeharbor.controller;

import com.recipeharbor.dto.ErrorResponseDto;
import com.recipeharbor.dto.RecipeDto;
import com.recipeharbor.dto.SearchCriteriaDto;
import com.recipeharbor.service.IRecipeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Author:  Ashwin Kumar
 *  REST APIs Controller for CRUD and Search operations in Recipe Harbor Application
 */
@Tag(
        name = "REST APIs for CRUD and Search operations in Recipe Harbor Application",
        description = "REST API's in RecipeHarbor to Create, Read, Update and Delete Recipe details and Search for Recipe details based on different criterias."
)
@RestController
@RequestMapping(path = "/api/recipe", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class RecipeController {

    private final IRecipeService recipeService;

    public RecipeController(IRecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @Operation(
            summary = "Fetch Recipe REST API",
            description = "REST API to fetch recipe details based on the recipe id"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "HTTP Status OK"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "HTTP Status Internal Server Error",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not Found (Recipe Not Found)"
            )
    }
    )
    @GetMapping("/{recipeId}")
    public ResponseEntity<RecipeDto> getRecipeById(@PathVariable @NotNull(message = "Recipe ID cannot be null") String recipeId) {
        RecipeDto recipeDto = recipeService.getRecipeById(recipeId);
        return ResponseEntity.status(HttpStatus.OK).body(recipeDto);
    }

    @Operation(
            summary = "Create Recipe REST API",
            description = "REST API to create Recipes in Recipe Harbor Application"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "HTTP Status CREATED"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "HTTP Bad Request (Validation Errors)"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "HTTP Status Internal Server Error",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    })
    @PostMapping
    public ResponseEntity<RecipeDto> createRecipe(@Valid @RequestBody RecipeDto recipeDto) {
        RecipeDto recipeCreated = recipeService.createRecipe(recipeDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(recipeCreated);
    }


    @Operation(
            summary = "Search Recipes REST API",
            description = "REST API to search different Recipes in the Recipe Harbor Application"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "HTTP Status CREATED"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "HTTP Bad Request (Validation Errors)"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "HTTP Status Internal Server Error",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    })
    @PostMapping("/search")
    public ResponseEntity<Page<RecipeDto>> searchRecipe(@Valid @RequestBody SearchCriteriaDto searchCriteriaDto,
                                                        @RequestParam(defaultValue = "0") int page,
                                                        @RequestParam(defaultValue = "10") int size) {
        Page<RecipeDto> recipePage = recipeService.searchRecipe(searchCriteriaDto, page, size);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Page-Number", String.valueOf(recipePage.getNumber()));
        headers.add("X-Page-Size", String.valueOf(recipePage.getSize()));
        return ResponseEntity
                .status(HttpStatus.OK)
                .headers(headers)
                .body(recipePage);
    }

    @Operation(
            summary = "Update Recipes REST API",
            description = "REST API to update (Replace) Recipes in the Recipe Harbor Application based on the recipe id"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "HTTP Status CREATED"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "HTTP Bad Request (Validation Errors)"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "HTTP Not Found (Recipe Not Found)"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "HTTP Status Internal Server Error",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    })
    @PutMapping("/{recipeId}")
    public ResponseEntity<RecipeDto> updateRecipe(@PathVariable @NotNull(message = "Recipe ID cannot be null") String recipeId,
                                                  @Valid @RequestBody RecipeDto recipeDto) {
        RecipeDto recipeUpdated = recipeService.updateRecipe(recipeId, recipeDto);
        return ResponseEntity.status(HttpStatus.OK).body(recipeUpdated);
    }

    @Operation(
            summary = "Delete Recipes REST API",
            description = "REST API to delete Recipes in the Recipe Harbor Application based on the recipe id"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "HTTP Status CREATED"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "HTTP Not Found (Recipe Not Found)"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "HTTP Status Internal Server Error",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    })
    @DeleteMapping("/{recipeId}")
    public ResponseEntity<Void> deleteRecipe(@PathVariable String recipeId) {
        recipeService.deleteRecipe(recipeId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
