package com.recipeharbor.config;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.recipeharbor.dto.RecipeDto;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;

/*
    * This class is used to deserialize the response from the search API. This is used only when the response is a page.
 */

public class RecipePage extends PageImpl<RecipeDto> {

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public RecipePage(
            @JsonProperty("content") List<RecipeDto> content,
            @JsonProperty("number") int number,
            @JsonProperty("size") int size,
            @JsonProperty("totalElements") long totalElements,
            @JsonProperty("pageable") JsonNode pageable,
            @JsonProperty("last") boolean last,
            @JsonProperty("totalPages") int totalPages,
            @JsonProperty("sort") JsonNode sort,
            @JsonProperty("first") boolean first,
            @JsonProperty("numberOfElements") int numberOfElements,
            @JsonProperty("empty") boolean empty) {

        super(content, PageRequest.of(number, size), totalElements);
    }
}
