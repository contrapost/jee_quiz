package me.contrapost.quizApi.dto;

import me.contrapost.jee_quiz.entity.SubCategory;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by alexandershipunov on 30/10/2016.
 * Subcategory converter
 */
public class SubCategoryConverter {
    private SubCategoryConverter() {}

    public static SubCategoryDTO transform(SubCategory subCategory) {
        Objects.requireNonNull(subCategory);

        SubCategoryDTO dto = new SubCategoryDTO();
        dto.id = String.valueOf(subCategory.getId());
        dto.title = subCategory.getTitle();
        dto.rootCategoryId = String.valueOf(subCategory.getRootCategory().getId());

        return dto;
    }

    public static List<SubCategoryDTO> transform(List<SubCategory> subCategories){
        Objects.requireNonNull(subCategories);

        return subCategories.stream()
                .map(SubCategoryConverter::transform)
                .collect(Collectors.toList());
    }
}
