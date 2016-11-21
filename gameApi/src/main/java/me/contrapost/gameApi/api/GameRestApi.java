package me.contrapost.gameApi.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import me.contrapost.gameApi.dto.AnswerCheckDTO;
import me.contrapost.gameApi.dto.GameDTO;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by alexandershipunov on 19/11/2016.
 *
 */
@Api(value = "/games" , description = "Handling of creating and retrieving games")
@Path("/games")
@Produces(Formats.JSON_V1)
public interface GameRestApi {

    @ApiOperation("Get all active games")
    @GET
    List<GameDTO> getAllActiveGames();

    @ApiOperation("Create a new game")
    @POST
    @Consumes(Formats.JSON_V1)
    @ApiResponse(code = 200, message = "The id of newly created game")
    Response createGame(
            @ApiParam("Optional parameter specifying number of quizzes in the game. Default value is 5 if absent")
            @QueryParam("limit")
                    String limit);

    @ApiOperation("Get a game specified by id")
    @GET
    @Path("/{id}")
    GameDTO getGameById(
            @ApiParam("Unique id of the game")
            @PathParam("id")
                    Long id);

    @ApiOperation("Answer the current quiz")
    @POST
    @Path("/{id}")
    AnswerCheckDTO answerQuiz(
            @ApiParam("Unique id of the game")
            @PathParam("id")
                    Long id,
            @ApiParam("Answer")
            @QueryParam("answer")
                    String answer);

    @ApiOperation("Quit the game")
    @DELETE
    @Path("/{id}")
    void quitGame(
            @ApiParam("Unique id of the game")
            @PathParam("id")
                    Long id);
}
