package me.contrapost.restApi.dto;

import me.contrapost.jee_quiz.entity.Quiz;
import me.contrapost.jee_quiz.entity.RootCategory;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by alexandershipunov on 30/10/2016.
 */
public class RootCategoryConverter {
    private RootCategoryConverter() {}

    public static RootCategoryDTO transform(RootCategory rootCategory) {
        Objects.requireNonNull(rootCategory);

        RootCategoryDTO dto = new RootCategoryDTO();
        dto.id = String.valueOf(rootCategory.getId());
        dto.title = rootCategory.getTitle();
        dto.subCategories = rootCategory.getSubCategories();

        return dto;
    }

    public static List<RootCategoryDTO> transform(List<RootCategory> rootCategories){
        Objects.requireNonNull(rootCategories);

        return rootCategories.stream()
                .map(RootCategoryConverter::transform)
                .collect(Collectors.toList());
    }
}
