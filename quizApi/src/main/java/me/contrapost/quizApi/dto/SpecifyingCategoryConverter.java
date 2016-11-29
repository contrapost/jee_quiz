package me.contrapost.quizApi.dto;

import me.contrapost.jee_quiz.entity.SpecifyingCategory;
import me.contrapost.quizApi.dto.collection.ListDTO;

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
        dto.subCategoryId = String.valueOf(specifyingCategory.getSubCategory().getId());

        return dto;
    }

    public static ListDTO<SpecifyingCategoryDTO> transform(List<SpecifyingCategory> specifyingCategories, int offset,
                                                           int limit){
        List<SpecifyingCategoryDTO> dtoList = null;
        if(specifyingCategories != null){
            dtoList = specifyingCategories.stream()
                    .skip(offset) // this is a good example of how streams simplify coding
                    .limit(limit)
                    .map(SpecifyingCategoryConverter::transform)
                    .collect(Collectors.toList());
        }

        ListDTO<SpecifyingCategoryDTO> dto = new ListDTO<>();
        dto.list = dtoList;
        dto._links = new ListDTO.ListLinks();
        dto.rangeMin = offset;
        assert dtoList != null;
        dto.rangeMax = dto.rangeMin + dtoList.size() - 1;
        dto.totalSize = specifyingCategories.size();

        return dto;
    }
}
