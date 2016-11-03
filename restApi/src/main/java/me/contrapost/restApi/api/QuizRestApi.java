package me.contrapost.restApi.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.jaxrs.PATCH;
import me.contrapost.restApi.dto.RootCategoryDTO;

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

    @ApiOperation("Get all root categories")
    @GET
    @Path("/categories")
    List<RootCategoryDTO> getAllRootCategories();

    @ApiOperation("Get a single root category specified by id")
    @GET
    @Path("categories/id/{id}")
    RootCategoryDTO getById(
            @ApiParam("The numeric id of the root category")
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
    void updateTitle(
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
    void mergePatch(@ApiParam("The unique id of the root category")
                    @PathParam("id")
                            Long id,
                    @ApiParam("The partial patch")
                            String jsonPatch);

    @ApiOperation("Delete a root category with the given id")
    @DELETE
    @Path("/categories/id/{id}")
    void delete(
            @ApiParam("The numeric id of the root category")
            @PathParam("id")
                    Long id);
}
