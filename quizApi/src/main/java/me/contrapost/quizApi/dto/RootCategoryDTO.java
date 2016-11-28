package me.contrapost.quizApi.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * Created by alexandershipunov on 30/10/2016.
 * Root category DTO
 */
@ApiModel("A root category")
public class RootCategoryDTO {

    @ApiModelProperty("The id of the root category")
    public String id;

    @ApiModelProperty("The title of the root category")
    public String title;

    @ApiModelProperty("Subcategories that belong to the category")
    public List<SubCategoryDTO> subcategories;

    @ApiModelProperty("Specifying categories that belong to the category")
    public List<SpecifyingCategoryDTO> specifyingCategories;

    public RootCategoryDTO() {
    }

    public
    RootCategoryDTO(String id, String title, List<SubCategoryDTO> subcategories,
                    List<SpecifyingCategoryDTO> specifyingCategories) {
        this.id = id;
        this.title = title;
        this.subcategories = subcategories;
        this.specifyingCategories = specifyingCategories;
    }
}
