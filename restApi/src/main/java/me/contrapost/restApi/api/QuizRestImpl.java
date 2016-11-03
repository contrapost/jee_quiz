package me.contrapost.restApi.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Throwables;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.jaxrs.PATCH;
import me.contrapost.jee_quiz.ejb.CategoryEJB;
import me.contrapost.jee_quiz.ejb.QuizEJB;
import me.contrapost.jee_quiz.entity.SubCategory;
import me.contrapost.restApi.dto.RootCategoryConverter;
import me.contrapost.restApi.dto.RootCategoryDTO;
import me.contrapost.restApi.dto.SubCategoryConverter;
import me.contrapost.restApi.dto.SubCategoryDTO;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by alexandershipunov on 30/10/2016.
 * Implementation of quiz API
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class QuizRestImpl implements QuizRestApi {

    @EJB
    private CategoryEJB categoryEJB;

    @EJB
    private QuizEJB quizEJB;


    @Override
    public List<RootCategoryDTO> getAllRootCategories() {
        return RootCategoryConverter.transform(categoryEJB.getAllRootCategories());
    }

    @Override
    public RootCategoryDTO getRootCategoryById(@ApiParam("The numeric id of the root category") Long id) {
        return RootCategoryConverter.transform(categoryEJB.getRootCategory(id));
    }

    @Override
    public Long createRootCategory(@ApiParam("Title of a new root category. Should not specify id.") RootCategoryDTO dto) {
        /*
            Error code 400:
            the user had done something wrong, eg sent invalid input configurations
         */

        if (dto.id != null) {
            throw new WebApplicationException("Cannot specify id for a newly generated root category", 400);
        }

        Long id;
        try {
            id = categoryEJB.createRootCategory(dto.title);
        } catch (Exception e) {
            /*
                note: this work just because NOT_SUPPORTED,
                otherwise a rolledback transaction would propagate to the
                caller of this method
             */
            throw wrapException(e);
        }

        return id;
    }

    @Override
    public void updateRootCategoryTitle(@ApiParam("The numeric id of the root category") Long id,
                                        @ApiParam("The new title which will replace the old one") String title) {
        if(categoryEJB.getRootCategory(id) == null){
            throw new WebApplicationException("Cannot find root category with id: "+id, 404);
        }

        updateTitle(id, title);
    }

    @Override
    public void mergePatchRootCategory(@ApiParam("The unique id of the root category") Long id,
                                       @ApiParam("The partial patch") String jsonPatch) {
        RootCategoryDTO dto = RootCategoryConverter.transform(categoryEJB.getRootCategory(id));
        if (dto == null) {
            throw new WebApplicationException("Cannot find root category with id " + id, 404);
        }

        ObjectMapper jackson = new ObjectMapper();

        JsonNode jsonNode;
        try {
            jsonNode = jackson.readValue(jsonPatch, JsonNode.class);
        } catch (Exception e) {
            throw new WebApplicationException("Invalid JSON data as input: " + e.getMessage(), 400);
        }

        if (jsonNode.has("id")) {
            throw new WebApplicationException(
                    "Cannot modify the root category id from " + id + " to " + jsonNode.get("id"), 409);
        }

        String newTitle = dto.title;

        if (jsonNode.has("title")) {
            JsonNode nameNode = jsonNode.get("title");
            if (nameNode.isNull()) {
                newTitle = null;
            } else if (nameNode.isTextual()) {
                newTitle = nameNode.asText();
            } else {
                throw new WebApplicationException("Invalid JSON. Non-string title", 400);
            }
        }

        dto.title = newTitle;
    }

    @Override
    public void deleteRootCategory(@ApiParam("The numeric id of the root category") Long id) {
        categoryEJB.deleteRootCategory(id);
    }

    @Override
    public List<SubCategoryDTO> getAllSubCategories() {
        return SubCategoryConverter.transform(categoryEJB.getAllSubCategories());
    }

    @Override
    public SubCategoryDTO getSubCategoryById(@ApiParam("The numeric id of the root category") Long id) {
        return SubCategoryConverter.transform(categoryEJB.getSubCategory(id));
    }

    @Override
    public Long createSubCategory(@ApiParam("Title of a new subcategory. Should not specify id.") SubCategoryDTO dto) {
        /*
            Error code 400:
            the user had done something wrong, eg sent invalid input configurations
         */

        if (dto.id != null) {
            throw new WebApplicationException("Cannot specify id for a newly generated subcategory", 400);
        }

        Long rootCategoryId = null;
        try {
            rootCategoryId = Long.parseLong(dto.rootCategoryId);
        } catch (NumberFormatException e) {
            throw new WebApplicationException("Id of the root category is not numeric", 400);
        }

        Long id;

        try {
            id = categoryEJB.createSubCategory(dto.title, rootCategoryId);
        } catch (Exception e) {
            /*
                note: this work just because NOT_SUPPORTED,
                otherwise a rolledback transaction would propagate to the
                caller of this method
             */
            throw wrapException(e);
        }

        return id;
    }

    @Override
    public void updateSubCategoryTitle(@ApiParam("The numeric id of the subcategory") Long id,
                                       @ApiParam("The new title which will replace the old one") String title) {
        if(categoryEJB.getSubCategory(id) == null){
            throw new WebApplicationException("Cannot find subcategory with id: " + id, 404);
        }

        updateTitle(id, title);
    }



    @Override
    public void mergePatchSubCategory(@ApiParam("The unique id of the subcategory") Long id,
                                      @ApiParam("The partial patch") String jsonPatch) {
        SubCategoryDTO dto = SubCategoryConverter.transform(categoryEJB.getSubCategory(id));
        if (dto == null) {
            throw new WebApplicationException("Cannot find subcategory with id " + id, 404);
        }

        ObjectMapper jackson = new ObjectMapper();

        JsonNode jsonNode;
        try {
            jsonNode = jackson.readValue(jsonPatch, JsonNode.class);
        } catch (Exception e) {
            throw new WebApplicationException("Invalid JSON data as input: " + e.getMessage(), 400);
        }

        if (jsonNode.has("id")) {
            throw new WebApplicationException(
                    "Cannot modify the subcategory id from " + id + " to " + jsonNode.get("id"), 409);
        }

        String newTitle = dto.title;
        String newRootCategoryId = dto.rootCategoryId;

        if (jsonNode.has("title")) {
            JsonNode nameNode = jsonNode.get("title");
            if (nameNode.isNull()) {
                newTitle = dto.title;
            } else if (nameNode.isTextual()) {
                newTitle = nameNode.asText();
            } else {
                throw new WebApplicationException("Invalid JSON. Non-string title", 400);
            }
        }

        if (jsonNode.has("rootCategoryId")) {
            JsonNode nameNode = jsonNode.get("rootCategoryId");
            if (nameNode.isNull()) {
                newRootCategoryId = dto.rootCategoryId;
            } else if (nameNode.isTextual()) {
                newRootCategoryId = nameNode.asText();
            } else {
                throw new WebApplicationException("Invalid JSON. Non-string root category id", 400);
            }
        }

        dto.title = newTitle;
        dto.rootCategoryId = newRootCategoryId;
    }

    @Override
    public void deleteSubCategory(@ApiParam("The numeric id of the subcategory") Long id) {
        categoryEJB.deleteSubCategory(id);
    }

    //----------------------------------------------------------

    private WebApplicationException wrapException(Exception e) throws WebApplicationException {
        Throwable cause = Throwables.getRootCause(e);
        if (cause instanceof ConstraintViolationException || cause instanceof SQLException) {
            return new WebApplicationException("Invalid constraints on input: " + cause.getMessage(), 400);
        } else {
            return new WebApplicationException("Internal error", 500);
        }
    }

    private void updateTitle(Long id, String title) {
        try {
            categoryEJB.updateCategoryTitle(id, title);
        } catch (Exception e){
            throw wrapException(e);
        }
    }
}