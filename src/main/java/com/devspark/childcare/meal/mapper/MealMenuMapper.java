package com.devspark.childcare.meal.mapper;

import com.devspark.childcare.meal.MealMenu;
import com.devspark.childcare.meal.dto.MealMenuCreateRequest;
import com.devspark.childcare.meal.dto.MealMenuResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * Compile-time mapper for translating between MealMenu entities and DTOs[cite: 4].
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MealMenuMapper {

    MealMenu toEntity(MealMenuCreateRequest request);

    MealMenuResponse toResponse(MealMenu entity);

    List<MealMenuResponse> toResponseList(List<MealMenu> entities);

    /**
     * Updates an existing entity instance with data from the provided DTO.
     * Crucial for the 'Edit' functionality without creating duplicate records.
     */
    void updateEntityFromDto(MealMenuCreateRequest dto, @MappingTarget MealMenu entity);
}