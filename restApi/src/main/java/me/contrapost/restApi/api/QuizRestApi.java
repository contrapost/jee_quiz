package me.contrapost.restApi.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import me.contrapost.restApi.dto.RootCategoryDTO;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created by alexandershipunov on 30/10/2016.
 * Quiz API interface
 */
@Api(value = "/root" , description = "Handling of creating and retrieving quizes")
@Path("/root")
@Produces(MediaType.APPLICATION_JSON)
public interface QuizRestApi {

    @ApiOperation("Get all root categories")
    @GET
    List<RootCategoryDTO> getRoot();

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    Long createRoot(RootCategoryDTO dto);

    @GET
    @Path("/root/{id}")
    RootCategoryDTO getRootById(@PathParam("id") Long id);

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    void updateRoot(RootCategoryDTO dto);

    @DELETE
    @Path("/root/{id}")
    void deleteRoot(@PathParam("id") Long id);
}
