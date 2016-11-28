package me.contrapost.quizApi.dto;

import me.contrapost.jee_quiz.entity.RootCategory;
import me.contrapost.jee_quiz.entity.SpecifyingCategory;
import me.contrapost.jee_quiz.entity.SubCategory;
import me.contrapost.quizApi.dto.collection.ListDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by alexandershipunov on 30/10/2016.
 * RootCategoryConverter
 */
public class RootCategoryConverter {
    private RootCategoryConverter() {}

    public static RootCategoryDTO transform(RootCategory rootCategory) {
        Objects.requireNonNull(rootCategory);

        RootCategoryDTO dto = new RootCategoryDTO();
        dto.id = String.valueOf(rootCategory.getId());
        dto.title = rootCategory.getTitle();

        return dto;
    }

    public static RootCategoryDTO transform(RootCategory rootCategory, boolean expand) {
        Objects.requireNonNull(rootCategory);

        RootCategoryDTO dto = new RootCategoryDTO();
        dto.id = String.valueOf(rootCategory.getId());
        dto.title = rootCategory.getTitle();

        if(expand) {
            dto.subcategories = new ArrayList<>();
            rootCategory.getSubCategories().values().stream()
                    .map(SubCategoryConverter::transform)
                    .forEach(subCategoryDTO -> dto.subcategories.add(subCategoryDTO));

            dto.specifyingCategories = new ArrayList<>();

            List<SubCategory> subCategories = new ArrayList<>(rootCategory.getSubCategories().values());
            for(SubCategory s : subCategories) {
                for(SpecifyingCategory sp : s.getSpecifyingCategories().values()){
                    dto.specifyingCategories.add(SpecifyingCategoryConverter.transform(sp));
                }
            }
        }

        return dto;
    }

    public static ListDTO<RootCategoryDTO> transform(List<RootCategory> rootCategories, int offset,
                                                     int limit, boolean expand){

        List<RootCategoryDTO> dtoList = null;
        if(rootCategories != null){
            dtoList = rootCategories.stream()
                    .skip(offset) // this is a good example of how streams simplify coding
                    .limit(limit)
                    .map(rootCategory -> transform(rootCategory, expand))
                    .collect(Collectors.toList());
        }

        ListDTO<RootCategoryDTO> dto = new ListDTO<>();
        dto.list = dtoList;
        dto._links = new ListDTO.ListLinks();
        dto.rangeMin = offset;
        assert dtoList != null;
        dto.rangeMax = dto.rangeMin + dtoList.size() - 1;
        dto.totalSize = rootCategories.size();

        return dto;
    }
}
