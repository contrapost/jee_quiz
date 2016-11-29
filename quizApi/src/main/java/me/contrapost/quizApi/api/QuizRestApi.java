package me.contrapost.quizApi.api;

import io.swagger.annotations.*;
import io.swagger.jaxrs.PATCH;
import me.contrapost.quizApi.dto.*;
import me.contrapost.quizApi.dto.collection.ListDTO;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by alexandershipunov on 30/10/2016.
 * Quiz API interface
 */
@Api(value = "/quiz" , description = "Handling of creating and retrieving root, sub- and specifying " +
        "categories and quizzes")
@Path("/quiz")
@Produces(Formats.JSON_V1)
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public interface QuizRestApi {

    //region Dealing with root category

    @ApiOperation("Get all root categories")
    @GET
    @Path("/categories")
    ListDTO<RootCategoryDTO> getAllRootCategories(@ApiParam("Offset in the list of news")
                                               @QueryParam("offset")
                                               @DefaultValue("0")
                                                       Integer offset,
                                               @ApiParam("Limit of news in a single retrieved page")
                                               @QueryParam("limit")
                                               @DefaultValue("10")
                                                       Integer limit,
                                               @ApiParam("Specifying to list only categories with quizzes")
                                               @QueryParam("withQuizzes")
                                               String withQuizzes,
                                               @ApiParam("Whether to retrieve or not votes and comments for the given news")
                                               @QueryParam("expand")
                                               @DefaultValue("false")
                                               Boolean expand);

    @ApiOperation("Get a single root category specified by id")
    @GET
    @Path("categories/{id}")
    RootCategoryDTO getRootCategoryById(@ApiParam("Whether to retrieve or not votes and comments for the given news")
                                        @QueryParam("expand")
                                        @DefaultValue("false")
                                                Boolean expand,
                                        @ApiParam(Params.ROOT_ID_PARAM)
                                        @PathParam("id")
                                                Long id);

    @ApiOperation("Create a new root category")
    @POST
    @Path("/categories")
    @Consumes(Formats.JSON_V1)
    @ApiResponse(code = 200, message = "The id of newly created root category")
    Long createRootCategory(
            @ApiParam("Title of a new root category. Should not specify id.")
                    RootCategoryDTO dto);

    @ApiOperation("Update a title of a root category")
    @PUT
    @Path("/categories/id/{id}/title")
    @Consumes(MediaType.TEXT_PLAIN)
    void updateRootCategoryTitle(
            @ApiParam(Params.ROOT_ID_PARAM)
            @PathParam("id")
                    Long id,
            //
            @ApiParam("The new title which will replace the old one")
                    String title
    );

    @ApiOperation("Modify the root category using JSON Merge Patch")
    @Path("/categories/id/{id}")
    @PATCH
    @Consumes(Formats.JSON_MERGE_V1)
    void mergePatchRootCategory(@ApiParam("The unique id of the root category")
                                @PathParam("id")
                                        Long id,
                                @ApiParam("The partial patch")
                                        String jsonPatch);

    @ApiOperation("Delete a root category with the given id")
    @DELETE
    @Path("/categories/id/{id}")
    void deleteRootCategory(
            @ApiParam(Params.ROOT_ID_PARAM)
            @PathParam("id")
                    Long id);

    // endregion

    //region Dealing with subcategory

    @ApiOperation("Get all subcategories")
    @GET
    @Path("/subcategories")
    ListDTO<SubCategoryDTO> getAllSubCategories(@ApiParam("Offset in the list of news")
                                                @QueryParam("offset")
                                                @DefaultValue("0")
                                                        Integer offset,
                                                @ApiParam("Limit of news in a single retrieved page")
                                                @QueryParam("limit")
                                                @DefaultValue("10")
                                                        Integer limit);

    @ApiOperation("Get a single subcategory specified by id")
    @GET
    @Path("/subcategories/{id}")
    SubCategoryDTO getSubCategoryById(
            @ApiParam(Params.SUB_ID_PARAM)
            @PathParam("id")
                    Long id);

    @ApiOperation("Create a new subcategory")
    @POST
    @Path("/subcategories")
    @Consumes(Formats.JSON_V1)
    @ApiResponse(code = 200, message = "The id of newly created subcategory")
    Long createSubCategory(
            @ApiParam("Title of a new subcategory. Should not specify id.")
                    SubCategoryDTO dto);

    @ApiOperation("Update a title of a subcategory")
    @PUT
    @Path("/subcategories/id/{id}/title")
    @Consumes(MediaType.TEXT_PLAIN)
    void updateSubCategoryTitle(
            @ApiParam(Params.SUB_ID_PARAM)
            @PathParam("id")
                    Long id,
            //
            @ApiParam("The new title which will replace the old one")
                    String title
    );

    @ApiOperation("Modify the subcategory using JSON Merge Patch")
    @Path("/subcategories/id/{id}")
    @PATCH
    @Consumes(Formats.JSON_MERGE_V1)
    void mergePatchSubCategory(@ApiParam("The unique id of the subcategory")
                               @PathParam("id")
                                       Long id,
                               @ApiParam("The partial patch")
                                       String jsonPatch);

    @ApiOperation("Delete a subcategory with the given id")
    @DELETE
    @Path("/subcategories/id/{id}")
    void deleteSubCategory(
            @ApiParam(Params.SUB_ID_PARAM)
            @PathParam("id")
                    Long id);

    //endregion

    //region Dealing with specifying categories

    @ApiOperation("Get all specifying categories")
    @GET
    @Path("/specifying-categories")
    ListDTO<SpecifyingCategoryDTO> getAllSpecifyingCategories(@ApiParam("Offset in the list of news")
                                                              @QueryParam("offset")
                                                              @DefaultValue("0")
                                                                      Integer offset,
                                                              @ApiParam("Limit of news in a single retrieved page")
                                                              @QueryParam("limit")
                                                              @DefaultValue("10")
                                                                      Integer limit,
                                                              @ApiParam("Specifying to list only categories with quizzes")
                                                              @QueryParam("withQuizzes")
                                                                      String withQuizzes);

    @ApiOperation("Get a single specifying category specified by id")
    @GET
    @Path("/specifying-categories/{id}")
    SpecifyingCategoryDTO getSpecifyingCategoryById(
            @ApiParam(Params.SPEC_ID_PARAM)
            @PathParam("id")
                    Long id);

    @ApiOperation("Create a new specifying category")
    @POST
    @Path("/specifying-categories")
    @Consumes(Formats.JSON_V1)
    @ApiResponse(code = 200, message = "The id of newly created specifying category")
    Long createSpecifyingCategory(
            @ApiParam("Title of a new subcategory. Should not specify id.")
                    SpecifyingCategoryDTO dto);

    @ApiOperation("Update a title of a specifying category")
    @PUT
    @Path("/specifying-categories/id/{id}/title")
    @Consumes(MediaType.TEXT_PLAIN)
    void updateSpecifyingCategoryTitle(
            @ApiParam(Params.SPEC_ID_PARAM)
            @PathParam("id")
                    Long id,
            //
            @ApiParam("The new title which will replace the old one")
                    String title
    );

    @ApiOperation("Modify the specifying category using JSON Merge Patch")
    @Path("/specifying-categories/id/{id}")
    @PATCH
    @Consumes(Formats.JSON_MERGE_V1)
    void mergePatchSpecifyingCategory(@ApiParam("The unique id of the specifying category")
                               @PathParam("id")
                                       Long id,
                               @ApiParam("The partial patch")
                                       String jsonPatch);

    @ApiOperation("Delete a specifying category with the given id")
    @DELETE
    @Path("/specifying-categories/id/{id}")
    void deleteSpecifyingCategory(
            @ApiParam(Params.SPEC_ID_PARAM)
            @PathParam("id")
                    Long id);

    //endregion

    //region Dealing with quizzes
    @ApiOperation("Get all quizzes")
    @GET
    @Path("/quizzes")
    @Produces(Formats.HAL_V1)
    ListDTO<QuizDTO> getAllQuizzes(@ApiParam("Offset in the list of news")
                                   @QueryParam("offset")
                                   @DefaultValue("0")
                                           Integer offset,
                                   @ApiParam("Limit of news in a single retrieved page")
                                   @QueryParam("limit")
                                   @DefaultValue("10")
                                           Integer limit);

    @ApiOperation("Get a single quiz specified by id")
    @GET
    @Path("/quizzes/{id}")
    QuizDTO getQuizById(
            @ApiParam(Params.QUIZ_ID_PARAM)
            @PathParam("id")
                    Long id);

    @ApiOperation("Check if the answer is correct")
    @GET
    @Path("/answer-check")
    boolean checkAnswer(
            @ApiParam(Params.QUIZ_ID_PARAM)
            @QueryParam("id")
                    Long id,
            @ApiParam("Answer to check")
            @QueryParam("answer")
                    String answer);

    @ApiOperation("Create a new quiz")
    @POST
    @Path("/quizzes")
    @Consumes(Formats.JSON_V1)
    @ApiResponse(code = 200, message = "The id of newly created specifying category")
    Long createQuiz(
            @ApiParam("Question, set of answers as a Map<String, boolean> and " +
                    "id of specifying category the quiz belongs to. Should not specify id.")
                    QuizWithCorrectAnswerDTO dto);

    @ApiOperation("Update the quiz question")
    @PUT
    @Path("/quizzes/id/{id}/question")
    @Consumes(MediaType.TEXT_PLAIN)
    void updateQuestionQuiz(
            @ApiParam(Params.QUIZ_ID_PARAM)
            @PathParam("id")
                    Long id,
            //
            @ApiParam("The new question which will replace the old one")
                    String question
    );

    @ApiOperation("Modify the quiz using JSON Merge Patch")
    @Path("/quizzes/id/{id}")
    @PATCH
    @Consumes(Formats.JSON_MERGE_V1)
    void mergePatchQuiz(@ApiParam(Params.QUIZ_ID_PARAM)
                                      @PathParam("id")
                                              Long id,
                                      @ApiParam("The partial patch")
                                              String jsonPatch);

    @ApiOperation("Delete a quiz with the given id")
    @DELETE
    @Path("/quizzes/id/{id}")
    void deleteQuiz(
            @ApiParam(Params.QUIZ_ID_PARAM)
            @PathParam("id")
                    Long id);
    //endregion

    //region Custom requests

    @ApiOperation("Get all subcategories of the category specified by id")
    @ApiResponses({
            @ApiResponse(code = 301, message = "Deprecated URI. Moved permanently.")
    })
    @GET
    @Path("/categories/{id}/subcategories")
    @Deprecated
    ListDTO<SubCategoryDTO> getAllSubCategoriesForRootCategory(@ApiParam(Params.ROOT_ID_PARAM)
                                                                     @PathParam("id")
                                                                             Long id,
                                                               @ApiParam("Offset in the list of news")
                                                               @QueryParam("offset")
                                                               @DefaultValue("0")
                                                                       Integer offset,
                                                               @ApiParam("Limit of news in a single retrieved page")
                                                               @QueryParam("limit")
                                                               @DefaultValue("10")
                                                                       Integer limit);

    @ApiOperation("Get all specifying categories of the subcategory specified by id")
    @GET
    @Path("/subcategories/{id}/specifying-categories")
    ListDTO<SpecifyingCategoryDTO> getAllSpecifyingCategoriesForSubCategory(@ApiParam(Params.SUB_ID_PARAM)
                                                            @PathParam("id")
                                                                    Long id,
                                                            @ApiParam("Offset in the list of news")
                                                            @QueryParam("offset")
                                                            @DefaultValue("0")
                                                                    Integer offset,
                                                            @ApiParam("Limit of news in a single retrieved page")
                                                            @QueryParam("limit")
                                                            @DefaultValue("10")
                                                                    Integer limit);

    @ApiOperation("Get all quizzes for parent (root/sub/specifying) category specified by id")
    @GET
    @Path("/quizzes/parent/{id}")
    @Produces(Formats.HAL_V1)
    ListDTO<QuizDTO> getAllQuizzesForParent(@ApiParam("Offset in the list of news")
                                            @QueryParam("offset")
                                            @DefaultValue("0")
                                                    Integer offset,
                                            @ApiParam("Limit of news in a single retrieved page")
                                            @QueryParam("limit")
                                            @DefaultValue("10")
                                                    Integer limit,
                                            @ApiParam(Params.GENERAL_ID_PARAM)
                                            @PathParam("id")
                                                    Long id);

    @ApiOperation("Get a random quiz. Return a random quiz for concrete root, sub- or specifying category" +
            "depending on filter")
    @ApiResponses({
            @ApiResponse(code = 307, message = "Temporary redirect."),
            @ApiResponse(code = 404, message = "There are no quizzes yet.")
    })
    @GET // replace parameters with only one filter
    @Path(("/randomQuiz"))
    Response getRandomQuiz(
            @ApiParam("Filter: x_id where x can be \"r\" for root category, " +
                    "\"s\" for subcategory and \"sp\" for specifying category")
            @QueryParam("filter")
                    String filter
    );

    @ApiOperation("Get a random quiz. Return a random quiz for concrete root, sub- or specifying category" +
            "depending on filter")
    @POST
    @Path(("/randomQuizzes"))
    IdsDTO getRandomQuizzes(
            @ApiParam("Number of quizzes")
            @QueryParam("limit")
                    String limit,
            @ApiParam("Filter: x_id where x can be \"r\" for root category, " +
                    "\"s\" for subcategory and \"sp\" for specifying category")
            @QueryParam("filter")
                    String filter
    );
    //endregion

    //region Deprecated methods
    @ApiOperation("Get a single root category specified by id")
    @ApiResponses({
            @ApiResponse(code = 301, message = "Deprecated URI. Moved permanently.")
    })
    @GET
    @Path("categories/id/{id}")
    @Deprecated
    Response deprecatedGetRootCategoryById(
            @ApiParam(Params.ROOT_ID_PARAM)
            @PathParam("id")
                    Long id);


    @ApiOperation("Get a single subcategory specified by id")
    @ApiResponses({
            @ApiResponse(code = 301, message = "Deprecated URI. Moved permanently.")
    })
    @GET
    @Path("subcategories/id/{id}")
    @Deprecated
    Response deprecatedGetSubCategoryById(
            @ApiParam(Params.SUB_ID_PARAM)
            @PathParam("id")
                    Long id);

    @ApiOperation("Get a single specifying specified by id")
    @ApiResponses({
            @ApiResponse(code = 301, message = "Deprecated URI. Moved permanently.")
    })
    @GET
    @Path("specifying-categories/id/{id}")
    @Deprecated
    Response deprecatedGetSpecifyingCategoryById(
            @ApiParam(Params.SPEC_ID_PARAM)
            @PathParam("id")
                    Long id);

    @ApiOperation("Get a single quiz by id")
    @ApiResponses({
            @ApiResponse(code = 301, message = "Deprecated URI. Moved permanently.")
    })
    @GET
    @Path("quizzes/id/{id}")
    @Deprecated
    Response deprecatedGetQuizById(
            @ApiParam(Params.QUIZ_ID_PARAM)
            @PathParam("id")
                    Long id);

    @ApiOperation("Get all root categories with at least one quiz")
    @ApiResponses({
            @ApiResponse(code = 301, message = "Deprecated URI. Moved permanently.")
    })
    @GET
    @Path("/categories/withQuizzes")
    @Deprecated
    Response deprecatedGetAllRootCategoriesWithAtLeastOneQuiz();

    @ApiOperation("Get all specifying categories with at least one quiz")
    @ApiResponses({
            @ApiResponse(code = 301, message = "Deprecated URI. Moved permanently.")
    })
    @GET
    @Path("/categories/withQuizzes/specifying-categories")
    @Deprecated
    Response deprecatedGetAllSpecifyingCategoriesWithAtLeastOneQuiz();


    @ApiOperation("Get all subcategories of the category specified by id")
    @ApiResponses({
            @ApiResponse(code = 301, message = "Deprecated URI. Moved permanently.")
    })
    @GET
    @Path("/categories/id/{id}/subcategories")
    @Deprecated
    Response deprecatedGetAllSubCategoriesForRootCategory(@ApiParam(Params.ROOT_ID_PARAM)
                                                            @PathParam("id")
                                                                    Long id);

    @ApiOperation("Get all subcategories with the given parent specified by id")
    @ApiResponses({
            @ApiResponse(code = 301, message = "Deprecated URI. Moved permanently.")
    })
    @GET
    @Path("/subcategories/parent/{id}")
    @Deprecated
    Response deprecatedGetAllSubCategoriesForParent(@ApiParam(Params.ROOT_ID_PARAM)
                                                      @PathParam("id")
                                                              Long id);

    @ApiOperation("Get all specifying categories of the subcategory specified by id")
    @ApiResponses({
            @ApiResponse(code = 301, message = "Deprecated URI. Moved permanently.")
    })
    @GET
    @Path("/subcategories/id/{id}/specifying-categories")
    @Deprecated
    Response deprecatedGetAllSpecifyingCategoriesForSubCategory(@ApiParam(Params.SUB_ID_PARAM)
                                                                         @PathParam("id")
                                                                                 Long id);

    @ApiOperation("Get all specifying categories with the given subcategory parent specified by id")
    @ApiResponses({
            @ApiResponse(code = 301, message = "Deprecated URI. Moved permanently.")
    })
    @GET
    @Path("/specifying-categories/parent/{id}")
    @Deprecated
    Response deprecatedGetAllSpecifyingCategoriesForParent(@ApiParam(Params.SUB_ID_PARAM)
                                                                    @PathParam("id")
                                                                            Long id);
    //endregion
}
