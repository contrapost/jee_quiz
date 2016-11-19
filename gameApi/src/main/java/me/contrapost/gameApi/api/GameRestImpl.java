package me.contrapost.gameApi.api;

import io.swagger.annotations.ApiParam;
import me.contrapost.gameApi.dto.GameDTO;

import java.util.List;

/**
 * Created by alexandershipunov on 19/11/2016.
 * Implementation of game API
 */
public class GameRestImpl implements GameRestApi {
    @Override
    public List<GameDTO> getAllActiveGames() {
        return null;
    }

    @Override
    public Long createGame(
            @ApiParam("Optional parameter specifying number of quizzes in the game. " +
                    "Default value is 5 if absent")
                    String limit) {
        return null;
    }

    @Override
    public GameDTO getGameById(@ApiParam("Unique id of the game") Long id) {
        return null;
    }

    @Override
    public GameDTO answerQuiz(@ApiParam("Unique id of the game") Long id) {
        return null;
    }

    @Override
    public GameDTO quitGame(@ApiParam("Unique id of the game")
                                        Long id,
                            @ApiParam("Answer")
                                    String answer) {
        return null;
    }
}
