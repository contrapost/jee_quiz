package me.contrapost.restApi.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import me.contrapost.jee_quiz.entity.SubCategory;

import java.util.Map;

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

    public RootCategoryDTO() {
    }

    public
    RootCategoryDTO(String id, String title) {
        this.id = id;
        this.title = title;
    }
}
