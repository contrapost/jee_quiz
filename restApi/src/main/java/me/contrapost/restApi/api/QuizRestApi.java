package me.contrapost.restApi.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.jaxrs.PATCH;
import me.contrapost.restApi.dto.QuizDTO;
import me.contrapost.restApi.dto.RootCategoryDTO;
import me.contrapost.restApi.dto.SpecifyingCategoryDTO;
import me.contrapost.restApi.dto.SubCategoryDTO;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Set;

/**
 * Created by alexandershipunov on 30/10/2016.
 * Quiz API interface
 */
@Api(value = "/quiz" , description = "Handling of creating and retrieving root, sub- and specifying " +
        "categories and quizes")
@Path("/quiz")
@Produces(MediaType.APPLICATION_JSON)
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public interface QuizRestApi {

    String GENERAL_ID_PARAM = "The numeric id of a parent category (root/sub/specifying)";
    String ROOT_ID_PARAM = "The numeric id of the root category";
    String SUB_ID_PARAM = "The numeric id of the subcategory";
    String SPEC_ID_PARAM = "The numeric id of the specifying category";
    String QUIZ_ID_PARAM = "The numeric id of the quiz";

    //region Dealing with root category

    @ApiOperation("Get all root categories")
    @GET
    @Path("/categories")
    List<RootCategoryDTO> getAllRootCategories();

    @ApiOperation("Get a single root category specified by id")
    @GET
    @Path("categories/id/{id}")
    RootCategoryDTO getRootCategoryById(
            @ApiParam(ROOT_ID_PARAM)
            @PathParam("id")
                    Long id);

    @ApiOperation("Create a new root category")
    @POST
    @Path("/categories")
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiResponse(code = 200, message = "The id of newly created root category")
    Long createRootCategory(
            @ApiParam("Title of a new root category. Should not specify id.")
                    RootCategoryDTO dto);

    @ApiOperation("Update a title of a root category")
    @PUT
    @Path("/categories/id/{id}/title")
    @Consumes(MediaType.TEXT_PLAIN)
    void updateRootCategoryTitle(
            @ApiParam(ROOT_ID_PARAM)
            @PathParam("id")
                    Long id,
            //
            @ApiParam("The new title which will replace the old one")
                    String title
    );

    @ApiOperation("Modify the root category using JSON Merge Patch")
    @Path("/categories/id/{id}")
    @PATCH
    @Consumes("application/merge-patch+json")
    void mergePatchRootCategory(@ApiParam("The unique id of the root category")
                                @PathParam("id")
                                        Long id,
                                @ApiParam("The partial patch")
                                        String jsonPatch);

    @ApiOperation("Delete a root category with the given id")
    @DELETE
    @Path("/categories/id/{id}")
    void deleteRootCategory(
            @ApiParam(ROOT_ID_PARAM)
            @PathParam("id")
                    Long id);

    // endregion

    //region Dealing with subcategory

    @ApiOperation("Get all subcategories")
    @GET
    @Path("/subcategories")
    List<SubCategoryDTO> getAllSubCategories();

    @ApiOperation("Get a single subcategory specified by id")
    @GET
    @Path("/subcategories/id/{id}")
    SubCategoryDTO getSubCategoryById(
            @ApiParam(SUB_ID_PARAM)
            @PathParam("id")
                    Long id);

    @ApiOperation("Create a new subcategory")
    @POST
    @Path("/subcategories")
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiResponse(code = 200, message = "The id of newly created subcategory")
    Long createSubCategory(
            @ApiParam("Title of a new subcategory. Should not specify id.")
                    SubCategoryDTO dto);

    @ApiOperation("Update a title of a subcategory")
    @PUT
    @Path("/subcategories/id/{id}/title")
    @Consumes(MediaType.TEXT_PLAIN)
    void updateSubCategoryTitle(
            @ApiParam(SUB_ID_PARAM)
            @PathParam("id")
                    Long id,
            //
            @ApiParam("The new title which will replace the old one")
                    String title
    );

    @ApiOperation("Modify the subcategory using JSON Merge Patch")
    @Path("/subcategories/id/{id}")
    @PATCH
    @Consumes("application/merge-patch+json")
    void mergePatchSubCategory(@ApiParam("The unique id of the subcategory")
                               @PathParam("id")
                                       Long id,
                               @ApiParam("The partial patch")
                                       String jsonPatch);

    @ApiOperation("Delete a subcategory with the given id")
    @DELETE
    @Path("/subcategories/id/{id}")
    void deleteSubCategory(
            @ApiParam(SUB_ID_PARAM)
            @PathParam("id")
                    Long id);

    //endregion

    //region Dealing with specifying categories

    @ApiOperation("Get all specifying categories")
    @GET
    @Path("/specifying-categories")
    List<SpecifyingCategoryDTO> getAllSpecifyingCategories();

    @ApiOperation("Get a single specifying category specified by id")
    @GET
    @Path("/specifying-categories/id/{id}")
    SpecifyingCategoryDTO getSpecifyingCategoryById(
            @ApiParam(SPEC_ID_PARAM)
            @PathParam("id")
                    Long id);

    @ApiOperation("Create a new specifying category")
    @POST
    @Path("/specifying-categories")
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiResponse(code = 200, message = "The id of newly created specifying category")
    Long createSpecifyingCategory(
            @ApiParam("Title of a new subcategory. Should not specify id.")
                    SpecifyingCategoryDTO dto);

    @ApiOperation("Update a title of a specifying category")
    @PUT
    @Path("/specifying-categories/id/{id}/title")
    @Consumes(MediaType.TEXT_PLAIN)
    void updateSpecifyingCategoryTitle(
            @ApiParam(SPEC_ID_PARAM)
            @PathParam("id")
                    Long id,
            //
            @ApiParam("The new title which will replace the old one")
                    String title
    );

    @ApiOperation("Modify the specifying category using JSON Merge Patch")
    @Path("/specifying-categories/id/{id}")
    @PATCH
    @Consumes("application/merge-patch+json")
    void mergePatchSpecifyingCategory(@ApiParam("The unique id of the specifying category")
                               @PathParam("id")
                                       Long id,
                               @ApiParam("The partial patch")
                                       String jsonPatch);

    @ApiOperation("Delete a specifying category with the given id")
    @DELETE
    @Path("/specifying-categories/id/{id}")
    void deleteSpecifyingCategory(
            @ApiParam(SPEC_ID_PARAM)
            @PathParam("id")
                    Long id);

    //endregion

    //region Dealing with quizes
    @ApiOperation("Get all quizes")
    @GET
    @Path("/quizes")
    List<QuizDTO> getAllQuizes();

    @ApiOperation("Get a single quiz specified by id")
    @GET
    @Path("/quizes/id/{id}")
    QuizDTO getQuizById(
            @ApiParam(QUIZ_ID_PARAM)
            @PathParam("id")
                    Long id);

    @ApiOperation("Create a new quiz")
    @POST
    @Path("/quizes")
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiResponse(code = 200, message = "The id of newly created specifying category")
    Long createQuiz(
            @ApiParam("Question, set of answers as a Map<String, boolean> and " +
                    "id of specifying category the quiz belongs to. Should not specify id.")
                    QuizDTO dto);

    @ApiOperation("Update the quiz question")
    @PUT
    @Path("/quizes/id/{id}/question")
    @Consumes(MediaType.TEXT_PLAIN)
    void updateQuestionQuiz(
            @ApiParam(QUIZ_ID_PARAM)
            @PathParam("id")
                    Long id,
            //
            @ApiParam("The new question which will replace the old one")
                    String question
    );

    @ApiOperation("Modify the quiz using JSON Merge Patch")
    @Path("/quizes/id/{id}")
    @PATCH
    @Consumes("application/merge-patch+json")
    void mergePatchQuiz(@ApiParam(QUIZ_ID_PARAM)
                                      @PathParam("id")
                                              Long id,
                                      @ApiParam("The partial patch")
                                              String jsonPatch);

    @ApiOperation("Delete a quiz with the given id")
    @DELETE
    @Path("/quizes/id/{id}")
    void deleteQuiz(
            @ApiParam(QUIZ_ID_PARAM)
            @PathParam("id")
                    Long id);
    //endregion

    //region Custom requests
    @ApiOperation("Get all root categories with at least one quiz")
    @GET
    @Path("/categories/withQuizzes")
    List<RootCategoryDTO> getAllRootCategoriesWithAtLeastOneQuiz();

    @ApiOperation("Get all specifying categories with at least one quiz")
    @GET
    @Path("/categories/withQuizzes/specifying-categories")
    List<SpecifyingCategoryDTO> getAllSpecifyingCategoriesWithAtLeastOneQuiz();

    @ApiOperation("Get all subcategories of the category specified by id")
    @GET
    @Path("/categories/id/{id}/subcategories")
    List<SubCategoryDTO> getAllSubCategoriesForRootCategory(@ApiParam(ROOT_ID_PARAM)
                                                            @PathParam("id")
                                                                    Long id);

    @ApiOperation("Get all subcategories with the given parent specified by id")
    @GET
    @Path("/subcategories/parent/{id}")
    List<SubCategoryDTO> getAllSubCategoriesForParent(@ApiParam(ROOT_ID_PARAM)
                                                            @PathParam("id")
                                                                    Long id);

    @ApiOperation("Get all specifying categories of the subcategory specified by id")
    @GET
    @Path("/subcategories/id/{id}/specifying-categories")
    List<SpecifyingCategoryDTO> getAllSpecifyingCategoriesForSubCategory(@ApiParam(SUB_ID_PARAM)
                                                            @PathParam("id")
                                                                    Long id);

    @ApiOperation("Get all specifying categories with the given subcategory parent specified by id")
    @GET
    @Path("/specifying-categories/parent/{id}")
    List<SpecifyingCategoryDTO> getAllSpecifyingCategoriesForParent(@ApiParam(SUB_ID_PARAM)
                                                      @PathParam("id")
                                                              Long id);

    @ApiOperation("Get all quizes for parent (root/sub/specifying) category specified by id")
    @GET
    @Path("/quizes/parent/{id}")
    List<QuizDTO> getAllQuizesForParent(@ApiParam(GENERAL_ID_PARAM)
                                                                    @PathParam("id")
                                                                            Long id);
    //endregion
}
