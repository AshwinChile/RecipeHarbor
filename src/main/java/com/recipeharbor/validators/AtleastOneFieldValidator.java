package com.recipeharbor.validators;

import com.recipeharbor.dto.SearchCriteriaDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * This class implements the validation logic to make sure that at least one field is specified from the SearchCriteria object.
 */
public class AtleastOneFieldValidator implements ConstraintValidator<AtleastOneField, SearchCriteriaDto> {
    /**
     * Initializes the validator in preparation for
     * {@link #isValid(Object, ConstraintValidatorContext)} calls.
     * The constraint annotation for a given constraint declaration
     * is passed.
     * <p>
     * This method is guaranteed to be called before any use of this instance for
     * validation.
     * <p>
     * The default implementation is a no-op.
     *
     * @param constraintAnnotation annotation instance for a given constraint declaration
     */
    @Override
    public void initialize(AtleastOneField constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    /**
     * Implements the validation logic to make sure that at least one field is specified from the SearchCriteria object.
     * The state of {@code value} must not be altered.
     * <p>
     * This method can be accessed concurrently, thread-safety must be ensured
     * by the implementation.
     *
     * @param searchCriteriaDto   object to validate
     * @param context context in which the constraint is evaluated
     * @return {@code false} if {@code value} does not pass the constraint
     */
    @Override
    public boolean isValid(SearchCriteriaDto searchCriteriaDto, ConstraintValidatorContext context) {
        return searchCriteriaDto.getVegetarian() != null
                || searchCriteriaDto.getServings() != null
                || searchCriteriaDto.getIncludeIngredients()!= null
                || searchCriteriaDto.getExcludeIngredients()!= null
                || searchCriteriaDto.getInstructionsText() != null;
    }
}
