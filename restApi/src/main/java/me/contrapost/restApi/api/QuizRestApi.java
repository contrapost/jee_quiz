package me.contrapost.restApi.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.jaxrs.PATCH;
import me.contrapost.restApi.dto.RootCategoryDTO;
import me.contrapost.restApi.dto.SubCategoryDTO;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created by alexandershipunov on 30/10/2016.
 * Quiz API interface
 */
@Api(value = "/quiz" , description = "Handling of creating and retrieving root, sub- and specifying " +
        "categories and quizes")
@Path("/quiz")
@Produces(MediaType.APPLICATION_JSON)
public interface QuizRestApi {


    // ================== Dealing with subcategory =======================

    @ApiOperation("Get all root categories")
    @GET
    @Path("/categories")
    List<RootCategoryDTO> getAllRootCategories();

    @ApiOperation("Get a single root category specified by id")
    @GET
    @Path("categories/id/{id}")
    RootCategoryDTO getRootCategoryById(
            @ApiParam("The numeric id of the subcategory")
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
            @ApiParam("The numeric id of the root category")
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
            @ApiParam("The numeric id of the root category")
            @PathParam("id")
                    Long id);

    // ================== Dealing with subcategory =======================

    @ApiOperation("Get all subcategories")
    @GET
    @Path("/subcategories")
    List<SubCategoryDTO> getAllSubCategories();

    @ApiOperation("Get a single subcategory specified by id")
    @GET
    @Path("/subcategories/id/{id}")
    SubCategoryDTO getSubCategoryById(
            @ApiParam("The numeric id of the root category")
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
            @ApiParam("The numeric id of the subcategory")
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
            @ApiParam("The numeric id of the subcategory")
            @PathParam("id")
                    Long id);
}