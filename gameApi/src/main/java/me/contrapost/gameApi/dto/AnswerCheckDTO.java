package me.contrapost.gameApi.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Created by alexandershipunov on 21/11/2016.
 *
 */
@ApiModel("An answer check")
public class AnswerCheckDTO {

    @ApiModelProperty("The boolean result reflects if the answer was correct")
    public boolean isCorrect;

    public AnswerCheckDTO() {
    }

    public AnswerCheckDTO(boolean isCorrect) {
        this.isCorrect = isCorrect;
    }
}
