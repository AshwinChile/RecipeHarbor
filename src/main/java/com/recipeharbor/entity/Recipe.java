package com.recipeharbor.entity;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "recipes")
public class Recipe extends BaseEntity {

    @Id
    private String id;
    @Indexed
    private String name;
    @Indexed
    private boolean vegetarian;
    @Indexed
    private Integer servings;
    @ElementCollection
    private List<String> ingredients;
    @ElementCollection
    private List<Steps> instructions;

}
