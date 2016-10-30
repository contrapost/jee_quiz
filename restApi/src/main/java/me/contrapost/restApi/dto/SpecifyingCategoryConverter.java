package me.contrapost.restApi.dto;

import me.contrapost.jee_quiz.entity.SpecifyingCategory;
import me.contrapost.jee_quiz.entity.SubCategory;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by alexandershipunov on 30/10/2016.
 * Specifying category converter
 */
public class SpecifyingCategoryConverter {
    private SpecifyingCategoryConverter() {}

    public static SpecifyingCategoryDTO transform(SpecifyingCategory specifyingCategory) {
        Objects.requireNonNull(specifyingCategory);

        SpecifyingCategoryDTO dto = new SpecifyingCategoryDTO();
        dto.id = String.valueOf(specifyingCategory.getId());
        dto.title = specifyingCategory.getTitle();
        dto.quizes = specifyingCategory.getQuizes();

        return dto;
    }

    public static List<SpecifyingCategoryDTO> transform(List<SpecifyingCategory> specifyingCategories){
        Objects.requireNonNull(specifyingCategories);

        return specifyingCategories.stream()
                .map(SpecifyingCategoryConverter::transform)
                .collect(Collectors.toList());
    }
}
