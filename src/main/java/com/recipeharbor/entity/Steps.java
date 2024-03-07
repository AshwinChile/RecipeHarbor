package com.recipeharbor.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.TextIndexed;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Steps {

    private Integer step_number;

    @TextIndexed
    private String description;

}
