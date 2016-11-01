package me.contrapost.restApi.dto;

import io.swagger.annotations.ApiModelProperty;
import me.contrapost.jee_quiz.entity.Quiz;
import me.contrapost.jee_quiz.entity.SubCategory;

import java.util.Map;

/**
 * Created by alexandershipunov on 30/10/2016.
 * Specifying category DTO
 */
public class
SpecifyingCategoryDTO {

    @ApiModelProperty("The id of the subcategory")
    public String id;

    @ApiModelProperty("The title of the subcategory")
    public String title;

    @ApiModelProperty("The set of specifying categories belonging to the subcategory")
    public Map<Long, Quiz> quizes;

    @ApiModelProperty("The subcategory the specifying categories belongs to")
    public SubCategory subCategory;

    public SpecifyingCategoryDTO(){}

    public SpecifyingCategoryDTO(String id, String title, Map<Long, Quiz> quizes, SubCategory subCategory) {
        this.id = id;
        this.title = title;
        this.quizes = quizes;
        this.subCategory = subCategory;
    }
}
