package me.contrapost.quizApi.dto;

import me.contrapost.jee_quiz.entity.SubCategory;
import me.contrapost.quizApi.dto.collection.ListDTO;

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

    public static ListDTO<SubCategoryDTO> transform(List<SubCategory> subCategories, int offset,
                                                 int limit){
        List<SubCategoryDTO> dtoList = null;
        if(subCategories != null){
            dtoList = subCategories.stream()
                    .skip(offset) // this is a good example of how streams simplify coding
                    .limit(limit)
                    .map(SubCategoryConverter::transform)
                    .collect(Collectors.toList());
        }

        ListDTO<SubCategoryDTO> dto = new ListDTO<>();
        dto.list = dtoList;
        dto._links = new ListDTO.ListLinks();
        dto.rangeMin = offset;
        assert dtoList != null;
        dto.rangeMax = dto.rangeMin + dtoList.size() - 1;
        dto.totalSize = subCategories.size();

        return dto;
    }
}
