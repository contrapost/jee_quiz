package me.contrapost.restApi.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Throwables;
import io.swagger.annotations.ApiParam;
import me.contrapost.jee_quiz.ejb.CategoryEJB;
import me.contrapost.jee_quiz.ejb.QuizEJB;
import me.contrapost.restApi.dto.*;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    //region implementation of REST API for root categories
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
            JsonNode titleNode = jsonNode.get("title");
            if (titleNode.isNull()) {
                newTitle = null;
            } else if (titleNode.isTextual()) {
                newTitle = titleNode.asText();
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

    //endregion

    //region implementation of REST API for subcategories
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

    //endregion

    //region implementation of Rest API for specifying categories
    @Override
    public List<SpecifyingCategoryDTO> getAllSpecifyingCategories() {
        return SpecifyingCategoryConverter.transform(categoryEJB.getAllSpecifyingCategories());
    }

    @Override
    public SpecifyingCategoryDTO getSpecifyingCategoryById(@ApiParam("The numeric id of the specifying category") Long id) {
        return SpecifyingCategoryConverter.transform(categoryEJB.getSpecifyingCategory(id));
    }

    @Override
    public Long createSpecifyingCategory(@ApiParam("Title of a new subcategory. Should not specify id.") SpecifyingCategoryDTO dto) {
        /*
            Error code 400:
            the user had done something wrong, eg sent invalid input configurations
         */

        if (dto.id != null) {
            throw new WebApplicationException("Cannot specify id for a newly generated specifying category", 400);
        }

        Long subCategoryId;
        try {
            subCategoryId = Long.parseLong(dto.subCategoryId);
        } catch (NumberFormatException e) {
            throw new WebApplicationException("Id of the subcategory is not numeric", 400);
        }

        Long id;

        try {
            id = categoryEJB.createSpecifyingCategory(dto.title, subCategoryId);
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
    public void updateSpecifyingCategoryTitle(@ApiParam(SPEC_ID_PARAM) Long id,
                                              @ApiParam("The new title which will replace the old one") String title) {
        if(categoryEJB.getSpecifyingCategory(id) == null){
            throw new WebApplicationException("Cannot find specifying category with id: " + id, 404);
        }

        updateTitle(id, title);
    }

    @Override
    public void mergePatchSpecifyingCategory(@ApiParam("The unique id of the specifying category") Long id,
                                             @ApiParam("The partial patch") String jsonPatch) {
        SpecifyingCategoryDTO dto = SpecifyingCategoryConverter.transform(categoryEJB.getSpecifyingCategory(id));
        if (dto == null) {
            throw new WebApplicationException("Cannot find specifying category with id " + id, 404);
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
        String newSubCategoryId = dto.subCategoryId;

        //noinspection Duplicates
        if (jsonNode.has("title")) {
            JsonNode titleNode = jsonNode.get("title");
            if (titleNode.isNull()) {
                newTitle = dto.title;
            } else if (titleNode.isTextual()) {
                newTitle = titleNode.asText();
            } else {
                throw new WebApplicationException("Invalid JSON. Non-string title", 400);
            }
        }

        if (jsonNode.has("subCategoryId")) {
            JsonNode nameNode = jsonNode.get("subCategoryId");
            if (nameNode.isNull()) {
                newSubCategoryId = dto.subCategoryId;
            } else if (nameNode.isTextual()) {
                newSubCategoryId = nameNode.asText();
            } else {
                throw new WebApplicationException("Invalid JSON. Non-string root category id", 400);
            }
        }

        dto.title = newTitle;
        dto.subCategoryId = newSubCategoryId;
    }

    @Override
    public void deleteSpecifyingCategory(@ApiParam("The numeric id of the specifying category") Long id) {
        categoryEJB.deleteSpecifyingCategory(id);
    }
    //endregion

    //region implementation of REST API for quizes
    @Override
    public List<QuizDTO> getAllQuizes() {
        return QuizConverter.transform(quizEJB.getAllQuizes());
    }

    @Override
    public QuizDTO getQuizById(@ApiParam(QUIZ_ID_PARAM) Long id) {
        return QuizConverter.transform(quizEJB.getQuiz(id));
    }

    @Override
    public Long createQuiz(@ApiParam("Question, set of answers as a Map<String, boolean> and " +
            "id of specifying category the quiz belongs to. Should not specify id.") QuizDTO dto) {
        /*
            Error code 400:
            the user had done something wrong, eg sent invalid input configurations
         */

        if (dto.id != null) {
            throw new WebApplicationException("Cannot specify id for a newly generated quiz", 400);
        }

        Long specifyingCategoryId = null;
        try {
            specifyingCategoryId = Long.parseLong(dto.specifyingCategoryId);
        } catch (NumberFormatException e) {
            throw new WebApplicationException("Id of the specifying category is not numeric", 400);
        }

        Long id;

        try {
            id = quizEJB.createQuiz(dto.question, dto.answerMap, specifyingCategoryId);
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
    public void updateQuestionQuiz(@ApiParam(QUIZ_ID_PARAM) Long id,
                                   @ApiParam("The new question which will replace the old one") String question) {
        if(quizEJB.getQuiz(id) == null){
            throw new WebApplicationException("Cannot find quiz with id: " + id, 404);
        }

        try {
            quizEJB.updateQuizQuestion(id, question);
        } catch (Exception e){
            throw wrapException(e);
        }
    }

    @Override
    public void mergePatchQuiz(@ApiParam("The unique id of the quiz") Long id,
                               @ApiParam("The partial patch") String jsonPatch) {
        QuizDTO dto = QuizConverter.transform(quizEJB.getQuiz(id));
        if (dto == null) {
            throw new WebApplicationException("Cannot find quiz with id " + id, 404);
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
                    "Cannot modify the quiz id from " + id + " to " + jsonNode.get("id"), 409);
        }

        String newQuestion = dto.question;
        Map<String, Boolean> newAnswerMap = dto.answerMap;
        String newSpecCategoryId = dto.specifyingCategoryId;

        if (jsonNode.has("question")) {
            JsonNode questionNode = jsonNode.get("question");
            if (questionNode.isNull()) {
                newQuestion = dto.question;
            } else if (questionNode.isTextual()) {
                newQuestion = questionNode.asText();
            } else {
                throw new WebApplicationException("Invalid JSON. Non-string title", 400);
            }
        }

        if (jsonNode.has("specifyingCategoryId")) {
            JsonNode specifyingCategoryIdNode = jsonNode.get("specifyingCategoryId");
            if (specifyingCategoryIdNode.isNull()) {
                newSpecCategoryId = dto.specifyingCategoryId;
            } else if (specifyingCategoryIdNode.isTextual()) {
                newSpecCategoryId = specifyingCategoryIdNode.asText();
            } else {
                throw new WebApplicationException("Invalid JSON. Non-string root category id", 400);
            }
        }

        if (jsonNode.has("answerMap")) {
            JsonNode answerMapNode = jsonNode.get("answerMap");
            if (answerMapNode.isNull()) {
                newAnswerMap = dto.answerMap;
            } else if (answerMapNode.isObject()) {
                newAnswerMap = jackson.convertValue(answerMapNode, Map.class);
            } else {
                throw new WebApplicationException("Invalid JSON. Non-string root category id", 400);
            }
        }

        dto.question = newQuestion;
        dto.specifyingCategoryId = newSpecCategoryId;
        dto.answerMap = newAnswerMap;
    }

    @Override
    public void deleteQuiz(@ApiParam(QUIZ_ID_PARAM) Long id) {
        quizEJB.deleteQuiz(id);
    }

    //endregion

    //region implementation of REST API for custom requests
    @Override
    public List<RootCategoryDTO> getAllRootCategoriesWithAtLeastOneQuiz() {
        return RootCategoryConverter.transform(new ArrayList<>(categoryEJB.getAllRootCategoriesWithAtLeastOneQuiz()));
    }

    @Override
    public List<SpecifyingCategoryDTO> getAllSpecifyingCategoriesWithAtLeastOneQuiz() {
        return SpecifyingCategoryConverter
                .transform(new ArrayList<>(categoryEJB.getAllSpecifyingCategoriesWithAtLeastOneQuiz()));
    }

    @Override
    public List<SubCategoryDTO> getAllSubCategoriesForRootCategory(@ApiParam(ROOT_ID_PARAM) Long id) {
        return SubCategoryConverter
                .transform(categoryEJB.getAllSubCategoriesForRootCategory(id));
    }

    @Override
    public List<SubCategoryDTO> getAllSubCategoriesForParent(@ApiParam(ROOT_ID_PARAM) Long id) {
        return SubCategoryConverter
                .transform(categoryEJB.getAllSubCategoriesForRootCategory(id));
    }

    @Override
    public List<SpecifyingCategoryDTO> getAllSpecifyingCategoriesForSubCategory(@ApiParam(ROOT_ID_PARAM) Long id) {
        return SpecifyingCategoryConverter
                .transform(categoryEJB.getAllSpecifyingCategoriesForSubCategory(id));
    }

    @Override
    public List<SpecifyingCategoryDTO> getAllSpecifyingCategoriesForParent(@ApiParam(ROOT_ID_PARAM) Long id) {
        return SpecifyingCategoryConverter
                .transform(categoryEJB.getAllSpecifyingCategoriesForSubCategory(id));
    }

    //endregion

    //region Util methods

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

    //endregion
}
