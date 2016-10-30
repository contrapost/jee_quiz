package me.contrapost.restApi.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import me.contrapost.jee_quiz.entity.RootCategory;
import me.contrapost.jee_quiz.entity.SpecifyingCategory;

import java.util.Map;

/**
 * Created by alexandershipunov on 30/10/2016.
 * Subcategory DTO
 */
@ApiModel("A subcategory")
public class SubCategoryDTO {

    @ApiModelProperty("The id of the subcategory")
    public String id;

    @ApiModelProperty("The title of the subcategory")
    public String title;

    @ApiModelProperty("The set of specifying categories belonging to the subcategory")
    public Map<Long, SpecifyingCategory> specifyingCategories;

    @ApiModelProperty("The root category the subcategory belongs to")
    public RootCategory rootCategory;

    public SubCategoryDTO(){}

    public SubCategoryDTO(String id, String title, Map<Long, SpecifyingCategory> specifyingCategories, RootCategory rootCategory) {
        this.id = id;
        this.title = title;
        this.specifyingCategories = specifyingCategories;
        this.rootCategory = rootCategory;
    }
}
