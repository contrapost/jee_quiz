package me.contrapost.gameApi.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Created by alexandershipunov on 19/11/2016.
 * Game data transfer object
 */
@ApiModel("A game")
public class GameDTO {

    @ApiModelProperty("The id of the game")
    public String id;

    @ApiModelProperty("Number of answers so far")
    public int answersCounter;

    @ApiModelProperty("Number of questions in the game")
    public int numberOfQuestions;

    @ApiModelProperty("URI to the current quiz")
    public String currentQuizURI;

    public GameDTO() {
    }

    public GameDTO(String id, int answersCounter, int numberOfQuestions, String currentQuizURI) {
        this.id = id;
        this.answersCounter = answersCounter;
        this.numberOfQuestions = numberOfQuestions;
        this.currentQuizURI = currentQuizURI;
    }
}
