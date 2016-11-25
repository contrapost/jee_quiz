package me.contrapost.quizApi.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import io.swagger.annotations.ApiParam;
import me.contrapost.jee_quiz.ejb.CategoryEJB;
import me.contrapost.jee_quiz.ejb.QuizEJB;
import me.contrapost.quizApi.dto.*;
import me.contrapost.quizApi.dto.collection.ListDTO;
import org.apache.commons.lang3.StringUtils;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
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
    public List<RootCategoryDTO> getAllRootCategories(String withQuizzes) {
        if(withQuizzes != null && (withQuizzes.isEmpty() || withQuizzes.equals("true"))) {
            return RootCategoryConverter.transform(new ArrayList<>(categoryEJB.getAllRootCategoriesWithAtLeastOneQuiz()));
        }
        return RootCategoryConverter.transform(categoryEJB.getAllRootCategories());
    }

    @Override
    public RootCategoryDTO getRootCategoryById(@ApiParam(Params.ROOT_ID_PARAM) Long id) {
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
    public void updateRootCategoryTitle(@ApiParam(Params.ROOT_ID_PARAM) Long id,
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

        categoryEJB.updateCategoryTitle(id, newTitle);
       // dto.title = newTitle;
    }

    @Override
    public void deleteRootCategory(@ApiParam(Params.ROOT_ID_PARAM) Long id) {
        categoryEJB.deleteRootCategory(id);
    }

    //endregion

    //region implementation of REST API for subcategories
    @Override
    public List<SubCategoryDTO> getAllSubCategories() {
        return SubCategoryConverter.transform(categoryEJB.getAllSubCategories());
    }

    @Override
    public SubCategoryDTO getSubCategoryById(@ApiParam(Params.SUB_ID_PARAM) Long id) {
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

        Long rootCategoryId;
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
    public void updateSubCategoryTitle(@ApiParam(Params.SUB_ID_PARAM) Long id,
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

        if (jsonNode.has("rootCategoryId")) {
            throw new WebApplicationException(
                    "Cannot modify the subcategory's root category id", 409);
        }

        String newTitle = dto.title;

        //noinspection Duplicates
        if (jsonNode.has("title")) {
            JsonNode nameNode = jsonNode.get("title");
            if (nameNode.isNull()) {
                newTitle = dto.title;
            } else
                if (nameNode.isTextual()) {
                newTitle = nameNode.asText();
            } else {
                throw new WebApplicationException("Invalid JSON. Non-string title", 400);
            }
        }

        updateTitle(id, newTitle);
    }

    @Override
    public void deleteSubCategory(@ApiParam(Params.SUB_ID_PARAM) Long id) {
        categoryEJB.deleteSubCategory(id);
    }

    //endregion

    //region implementation of Rest API for specifying categories
    @Override
    public List<SpecifyingCategoryDTO> getAllSpecifyingCategories(String withQuizzes) {
        if(withQuizzes != null && (withQuizzes.isEmpty() || withQuizzes.equals("true"))) {
            return SpecifyingCategoryConverter.transform(new ArrayList<>(categoryEJB.getAllSpecifyingCategoriesWithAtLeastOneQuiz()));
        }
        return SpecifyingCategoryConverter.transform(categoryEJB.getAllSpecifyingCategories());
    }

    @Override
    public SpecifyingCategoryDTO getSpecifyingCategoryById(@ApiParam(Params.SPEC_ID_PARAM) Long id) {
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
    public void updateSpecifyingCategoryTitle(@ApiParam(Params.SPEC_ID_PARAM) Long id,
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

        if (jsonNode.has("subCategoryId")) {
            throw new WebApplicationException(
                    "Cannot modify the id of subcategory's root category", 400);
        }

        String newTitle = dto.title;

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

        updateTitle(id, newTitle);
    }

    @Override
    public void deleteSpecifyingCategory(@ApiParam(Params.SPEC_ID_PARAM) Long id) {
        categoryEJB.deleteSpecifyingCategory(id);
    }
    //endregion

    //region implementation of REST API for quizzes
    @Override
    public ListDTO<QuizDTO> getAllQuizzes(Integer offset, Integer limit) {
        return QuizConverter.transform(quizEJB.getAllQuizzes(), offset, limit);
    }

    @Override
    public QuizDTO getQuizById(@ApiParam(Params.QUIZ_ID_PARAM) Long id) {
        return QuizConverter.transform(quizEJB.getQuiz(id));
    }

    @Override
    public boolean checkAnswer(@ApiParam(Params.QUIZ_ID_PARAM) Long id, @ApiParam("Answer to check") String answer) {
        return quizEJB.getQuiz(id).getAnswerMap().get(answer);
    }

    @Override
    public Long createQuiz(@ApiParam("Question, set of answers as a Map<String, boolean> and " +
            "id of specifying category the quiz belongs to. Should not specify id.") QuizWithCorrectAnswerDTO dto) {
        /*
            Error code 400:
            the user had done something wrong, eg sent invalid input configurations
         */

        if (dto.id != null) {
            throw new WebApplicationException("Cannot specify id for a newly generated quiz", 400);
        }

        Long specifyingCategoryId;
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
    public void updateQuestionQuiz(@ApiParam(Params.QUIZ_ID_PARAM) Long id,
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
        QuizWithCorrectAnswerDTO dto = QuizWithCorrectAnswerConverter.transform(quizEJB.getQuiz(id));
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

        if (jsonNode.has("specifyingCategoryId")) {
            throw new WebApplicationException(
                    "Cannot modify the id of quiz's specifying category", 400);
        }

        String newQuestion = dto.question;
        Map<String, Boolean> newAnswerMap = dto.answerMap;

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

        if (jsonNode.has("answerMap")) {
            JsonNode answerMapNode = jsonNode.get("answerMap");
            if (answerMapNode.isNull()) {
                newAnswerMap = dto.answerMap;
            } else if (answerMapNode.isObject()) {
                //noinspection unchecked
                newAnswerMap = jackson.convertValue(answerMapNode, Map.class);
            } else {
                throw new WebApplicationException("Invalid JSON. Non-string root category id", 400);
            }
        }

        quizEJB.updateQuiz(id, newQuestion, newAnswerMap);
    }

    @Override
    public void deleteQuiz(@ApiParam(Params.QUIZ_ID_PARAM) Long id) {
        quizEJB.deleteQuiz(id);
    }



    //endregion

    //region implementation of REST API for custom requests

    @Override
    public List<SubCategoryDTO> getAllSubCategoriesForRootCategory(@ApiParam(Params.ROOT_ID_PARAM) Long id) {
        return SubCategoryConverter
                .transform(categoryEJB.getAllSubCategoriesForRootCategory(id));
    }

    @Override
    public List<SpecifyingCategoryDTO> getAllSpecifyingCategoriesForSubCategory(@ApiParam(Params.ROOT_ID_PARAM) Long id) {
        return SpecifyingCategoryConverter
                .transform(categoryEJB.getAllSpecifyingCategoriesForSubCategory(id));
    }

    @Override
    public ListDTO<QuizDTO> getAllQuizzesForParent(Integer offset, Integer limit, Long id) {
        return QuizConverter.transform(categoryEJB.getAllQuizzesForCategory(id), offset, limit);
    }

    @Override
    public Response getRandomQuiz(@ApiParam("Filter: x_id where x can be \"r\" for root category, " +
                                            "\"s\" for subcategory and \"sp\" for specifying category")
                                              String filter) {

        if(quizEJB.getAllQuizzes().isEmpty()) {
            return Response.status(404).build();
        }

        String[] parts;
        Long quizId;

        if (!Strings.isNullOrEmpty(filter)) {
            parts = filter.split("_");
            if(parts.length != 2)
                throw new WebApplicationException("Filter value has incorrect format", 404);

            if(!StringUtils.isNumeric(parts[1])){
                throw new WebApplicationException("Filter value has incorrect format", 404);
            }

            switch (parts[0]){
                case "r":
                    Long rootCategoryId = getAsNumber(parts[1]);

                    quizId = categoryEJB.getRandomQuizzesFromRootCategory(rootCategoryId, 1).get(0);

                    if(quizId == null) {
                        return Response.status(404).build();
                    }

                    return Response.status(307)
                            .location(URI.create("quiz/quizzes/" + quizId))
                            .build();
                case "s":
                    Long subcategoryId = getAsNumber(parts[1]);

                    quizId = categoryEJB.getRandomQuizzesFromSubCategory(subcategoryId, 1).get(0);

                    if(quizId == null) {
                        return Response.status(404).build();
                    }

                    return Response.status(307)
                            .location(URI.create("quiz/quizzes/" + quizId))
                            .build();
                case "sp":
                    Long specifyingCategoryId = getAsNumber(parts[1]);

                    quizId = categoryEJB.getRandomQuizzesFromSpecifyingCategory(specifyingCategoryId, 1).get(0);

                    if(quizId == null) {
                        return Response.status(404).build();
                    }

                    return Response.status(307)
                            .location(URI.create("quiz/quizzes/" + quizId))
                            .build();
                default:
                    throw new WebApplicationException("Filter value has incorrect format", 404);
            }
        }

        return Response.status(307)
                .location(URI.create("quiz/quizzes/" + quizEJB.getRandomQuizzes(1).get(0)))
                .build();
    }

    @Override
    public List<Long> getRandomQuizzes(@ApiParam("Number of quizzes")
                                                   String limit,
                                     @ApiParam("Filter: x_id where x can be \"r\" for root category, " +
                                     "\"s\" for subcategory and \"sp\" for specifying category")
                                                   String filter) {

        if(quizEJB.getAllQuizzes().isEmpty()) {
            throw new WebApplicationException("There are no quizzes yet.", 404);
        }

        int numberOfQuizzes;
        if(!Strings.isNullOrEmpty(limit)) {
            try {
                numberOfQuizzes = Integer.parseInt(limit);
            } catch (NumberFormatException e) {
                throw new WebApplicationException("Id of the root category is not numeric", 400);
            }
        } else {
            numberOfQuizzes = 5;
        }

        String[] parts;

        List<Long> quizIds;

        if (!Strings.isNullOrEmpty(filter)) {
            parts = filter.split("_");
            if(parts.length != 2)
                throw new WebApplicationException("Filter value has incorrect format", 404);

            if(!StringUtils.isNumeric(parts[1])){
                throw new WebApplicationException("Filter value has incorrect format", 404);
            }

            switch (parts[0]){
                case "r":
                    Long rootCategoryId = getAsNumber(parts[1]);

                    quizIds = categoryEJB.getRandomQuizzesFromRootCategory(rootCategoryId, numberOfQuizzes);

                    if(quizIds.size() < numberOfQuizzes) {
                        throw new WebApplicationException("There are not enough quizzes.", 404);
                    }

                    return quizIds;
                case "s":
                    Long subcategoryId = getAsNumber(parts[1]);

                    quizIds = categoryEJB.getRandomQuizzesFromSubCategory(subcategoryId, numberOfQuizzes);

                    if(quizIds.size() < numberOfQuizzes) {
                        throw new WebApplicationException("There are not enough quizzes.", 404);
                    }

                    return quizIds;
                case "sp":
                    Long specifyingCategoryId = getAsNumber(parts[1]);

                    quizIds = categoryEJB.getRandomQuizzesFromSpecifyingCategory(specifyingCategoryId, numberOfQuizzes);

                    if(quizIds.size() < numberOfQuizzes) {
                        throw new WebApplicationException("There are not enough quizzes.", 404);
                    }

                    return quizIds;
                default:
                    throw new WebApplicationException("Filter value has incorrect format", 404);
            }
        }

        quizIds = quizEJB.getRandomQuizzes(numberOfQuizzes);

        if(quizIds.size() < numberOfQuizzes) {
            throw new WebApplicationException("There are not enough quizzes.", 404);
        }

        return quizIds;
    }


    //endregion

    //region deprecated methods
    @Override
    public Response deprecatedGetRootCategoryById(@ApiParam(Params.ROOT_ID_PARAM) Long id) {
        return Response.status(301)
                .location(UriBuilder.fromUri("quiz/categories/" + id)
                        .build())
                .build();
    }

    @Override
    public Response deprecatedGetSubCategoryById(@ApiParam(Params.SUB_ID_PARAM) Long id) {
        return Response.status(301)
                .location(UriBuilder.fromUri("quiz/subcategories/" + id)
                        .build())
                .build();
    }

    @Override
    public Response deprecatedGetSpecifyingCategoryById(@ApiParam(Params.SPEC_ID_PARAM) Long id) {
        return Response.status(301)
                .location(UriBuilder.fromUri("quiz/specifying-categories/" + id)
                        .build())
                .build();
    }

    @Override
    public Response deprecatedGetQuizById(@ApiParam(Params.QUIZ_ID_PARAM) Long id) {
        return Response.status(301)
                .location(UriBuilder.fromUri("quiz/quizzes/" + id)
                        .build())
                .build();
    }

    @Override
    public Response deprecatedGetAllRootCategoriesWithAtLeastOneQuiz() {
        return Response.status(301)
                .location(UriBuilder.fromUri("quiz/categories").queryParam("withQuizzes", "")
                        .build())
                .build();
    }



    @Override
    public Response deprecatedGetAllSpecifyingCategoriesWithAtLeastOneQuiz() {
        return Response.status(301)
                .location(UriBuilder.fromUri("quiz/specifying-categories").queryParam("withQuizzes", "")
                        .build())
                .build();
    }

    @Override
    public Response deprecatedGetAllSubCategoriesForRootCategory(@ApiParam(Params.ROOT_ID_PARAM) Long id) {
        return Response.status(301)
                .location(UriBuilder.fromUri("quiz/categories/" + id + "/subcategories")
                        .build())
                .build();
    }


    @Override
    public Response deprecatedGetAllSubCategoriesForParent(@ApiParam(Params.ROOT_ID_PARAM) Long id) {
        return Response.status(301)
                .location(UriBuilder.fromUri("quiz/categories/" + id + "/subcategories")
                        .build())
                .build();
    }

    @Override
    public Response deprecatedGetAllSpecifyingCategoriesForSubCategory(@ApiParam(Params.SUB_ID_PARAM) Long id) {
        return Response.status(301)
                .location(UriBuilder.fromUri("quiz/subcategories/" + id + "/specifying-categories")
                        .build())
                .build();
    }

    @Override
    public Response deprecatedGetAllSpecifyingCategoriesForParent(@ApiParam(Params.ROOT_ID_PARAM) Long id) {
        return Response.status(301)
                .location(UriBuilder.fromUri("quiz/subcategories/" + id + "/specifying-categories")
                        .build())
                .build();
    }
    //endregion

    //region util methods

    private WebApplicationException wrapException(Exception e) throws WebApplicationException {
        @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
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

    private Long getAsNumber(String numberAsString) {
        Long number;
        try {
            number = Long.parseLong(numberAsString);
        } catch (NumberFormatException e) {
            throw new WebApplicationException("Id of the root category is not numeric", 400);
        }
        return number;
    }

    //endregion
}
