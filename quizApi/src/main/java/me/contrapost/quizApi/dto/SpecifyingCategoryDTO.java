package me.contrapost.quizApi.dto;

import io.swagger.annotations.ApiModelProperty;

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

    @ApiModelProperty("The subcategory the specifying categories belongs to")
    public String subCategoryId;

    public SpecifyingCategoryDTO(){}

    public SpecifyingCategoryDTO(String id, String title, String subCategoryId) {
        this.id = id;
        this.title = title;
        this.subCategoryId = subCategoryId;
    }
}
