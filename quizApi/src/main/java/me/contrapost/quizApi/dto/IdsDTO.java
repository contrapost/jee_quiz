package me.contrapost.quizApi.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * Created by Alexander Shipunov on 29.11.16.
 *
 */
@ApiModel("ids of the random quizzes")
public class IdsDTO {

    @ApiModelProperty("ids")
    public List<Long> ids;

    public IdsDTO() {}
}
