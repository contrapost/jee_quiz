package me.contrapost.quizApi.api;

import io.restassured.http.ContentType;
import me.contrapost.quizApi.dto.*;
import me.contrapost.quizApi.dto.collection.ListDTO;
import org.junit.Test;

import java.util.*;

import static io.restassured.RestAssured.*;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

/**
 * Created by alexandershipunov on 30/10/2016.
 * Test suite for QuizRest API
 */
@SuppressWarnings("unused")
public class QuizRestIT extends QuizRestTestBase {

    @Test
    public void testCleanDB() {

        get("/categories").then()
                .statusCode(200)
                .body("list.size()", is(0));
    }

    //region Testing root category

    @Test
    public void testCreateAndGetRoot() {

        String title = "Title";

        RootCategoryDTO dto = new RootCategoryDTO(null, title, null, null);

        get("/categories").then().statusCode(200).body("list.size()", is(0));

        String id = given().contentType(ContentType.JSON)
                .body(dto)
                .post("/categories")
                .then()
                .statusCode(200)
                .extract().asString();

        get("/categories").then().statusCode(200).body("list.size()", is(1));

        given().pathParam("id", id)
                .get("categories/id/{id}")
                .then()
                .statusCode(200)
                .body("id", is(id))
                .body("title", is(title));
    }

    @Test
    public void testGetAllRootCategories() {
        String rootId1 = createRootCategory("Root #1");
        String rootId2 = createRootCategory("Root #2");
        String rootId3 = createRootCategory("Root #3");

        get("/categories").then().statusCode(200).body("list.size()", is(3));

        given().get("/categories")
                .then()
                .statusCode(200)
                .body("list.id", hasItems(rootId1, rootId2, rootId3))
                .body("list.title", hasItems("Root #1", "Root #2", "Root #3"));
    }

    @Test
    public void testDeleteRootCategory() {

        String id = given().contentType(ContentType.JSON)
                .body(new RootCategoryDTO(null, "Root", null, null))
                .post("/categories")
                .then()
                .statusCode(200)
                .extract().asString();

        get("/categories").then().body("list.id", contains(id));

        delete("/categories/id/" + id);

        get("/categories").then().body("id", not(contains(id)));
    }

    @Test
    public void testUpdateRootCategory() throws Exception {

        String title = "Title";

        //first create with a POST
        String id = given().contentType(ContentType.JSON)
                .body(new RootCategoryDTO(null, title, null, null))
                .post("/categories")
                .then()
                .statusCode(200)
                .extract().asString();

        //check if POST was fine
        get("/categories/id/" + id).then().body("title", is(title));


        //now change just the title
        String anotherTitle = "another title";

        given().contentType(ContentType.TEXT)
                .body(anotherTitle)
                .pathParam("id", id)
                .put("/categories/id/{id}/title")
                .then()
                .statusCode(204);

        get("/categories/id/" + id).then().body("title", is(anotherTitle));
    }

    @Test
    public void testPatchMergeRootCategory() {

        String title = "RootCategory";

        String id = createRootCategory(title);

        String newTitle = "RootCategory v.2";

        given().contentType("application/merge-patch+json")
                .body("{\"title\":\"" + newTitle + "\"}")
                .patch("/categories/id/" + id)
                .then()
                .statusCode(204);

        RootCategoryDTO readBack = given().port(8080)
                .baseUri("http://localhost")
                .accept(ContentType.JSON)
                .get("/categories/id/" + id)
                .then()
                .statusCode(200)
                .extract()
                .as(RootCategoryDTO.class);

        assertEquals(newTitle, readBack.title);
        assertEquals(id, readBack.id); // should had stayed the same
    }

    @Test
    public void testRootCreateWithSameTitle() {
        String title = "Title";

        given().contentType(ContentType.JSON)
                .body(new RootCategoryDTO(null, title, null, null))
                .post("/categories")
                .then()
                .statusCode(200);

        get("/categories").then().statusCode(200).body("list.size()", is(1));

        given().contentType(ContentType.JSON)
                .body(new RootCategoryDTO(null, title, null, null))
                .post("/categories")
                .then()
                .statusCode(400);

        get("/categories").then().statusCode(200).body("list.size()", is(1));
    }

    //endregion

    //region Testing subcategory
    @Test
    public void testCreateAndGetSubCategory() {
        String rootCategoryId = createRootCategory();
        String title = "SubCategory";

        SubCategoryDTO dto = new SubCategoryDTO(null, title, rootCategoryId);

        get("/subcategories").then().statusCode(200).body("size()", is(0));

        String id = given().contentType(ContentType.JSON)
                .body(dto)
                .post("/subcategories")
                .then()
                .statusCode(200)
                .extract().asString();

        get("/subcategories").then().statusCode(200).body("size()", is(1));

        given().pathParam("id", id)
                .get("subcategories/id/{id}")
                .then()
                .statusCode(200)
                .body("id", is(id))
                .body("title", is(title));
    }

    @Test
    public void testGetAllSubCategories() {
        String rootId = createRootCategory("Root #1");

        String subCategoryId1 = createSubCategory(rootId, "SubCategory #1");
        String subCategoryId2 = createSubCategory(rootId, "SubCategory #2");
        String subCategoryId3 = createSubCategory(rootId, "SubCategory #3");

        get("/subcategories").then().statusCode(200).body("size()", is(3));

        given().get("/subcategories")
                .then()
                .statusCode(200)
                .body("id", hasItems(subCategoryId1, subCategoryId2, subCategoryId3))
                .body("title", hasItems("SubCategory #1", "SubCategory #2", "SubCategory #3"));
    }

    @Test
    public void testDeleteSubCategory() {

        String id = given().contentType(ContentType.JSON)
                .body(new SubCategoryDTO(null, "Subcategory", createRootCategory()))
                .post("/subcategories")
                .then()
                .statusCode(200)
                .extract().asString();

        get("/subcategories").then().body("id", contains(id));

        delete("/subcategories/id/" + id);

        get("/subcategories").then().body("id", not(contains(id)));
    }

    @Test
    public void testUpdateSubCategory() throws Exception {

        String title = "Title";

        //first create with a POST
        String id = given().contentType(ContentType.JSON)
                .body(new SubCategoryDTO(null, title, createRootCategory()))
                .post("/subcategories")
                .then()
                .statusCode(200)
                .extract().asString();

        //check if POST was fine
        get("/subcategories/id/" + id).then().body("title", is(title));


        //now change just the title
        String anotherTitle = "another title";

        given().contentType(ContentType.TEXT)
                .body(anotherTitle)
                .pathParam("id", id)
                .put("/subcategories/id/{id}/title")
                .then()
                .statusCode(204);

        get("/subcategories/id/" + id).then().body("title", is(anotherTitle));
    }

    @Test
    public void testCreateSubcategoryWithSameTitle() {
        String rootId = createRootCategory();
        String title = "Title";

        given().contentType(ContentType.JSON)
                .body(new SubCategoryDTO(null, title, rootId))
                .post("/subcategories")
                .then()
                .statusCode(200);

        get("/subcategories").then().statusCode(200).body("size()", is(1));

        given().contentType(ContentType.JSON)
                .body(new SubCategoryDTO(null, title, rootId))
                .post("/subcategories")
                .then()
                .statusCode(400);

        get("/subcategories").then().statusCode(200).body("size()", is(1));
    }

    @Test
    public void testPatchMergeSubCategory() {

        String title = "SubCategory";

        String id = createSubCategory(createRootCategory(), title);

        String newTitle = "SubCategory v.2";

        given().contentType("application/merge-patch+json")
                .body("{\"title\":\"" + newTitle + "\"}")
                .patch("/subcategories/id/" + id)
                .then()
                .statusCode(204);

        SubCategoryDTO readBack = given()
                .accept(ContentType.JSON)
                .get("/subcategories/id/" + id)
                .then()
                .statusCode(200)
                .extract()
                .as(SubCategoryDTO.class);

        assertEquals(newTitle, readBack.title);
        assertEquals(id, readBack.id); // should had stayed the same
    }

    //endregion

    //region Testing specifying category
    @Test
    public void testCreateAndGetSpecifyingCategory() {
        String rootCategoryId = createRootCategory("Root Category");
        String subCategoryId = createSubCategory(rootCategoryId, "Subcategory");

        String title = "Specifying category";

        SpecifyingCategoryDTO dto = new SpecifyingCategoryDTO(null, title, subCategoryId);

        get("/specifying-categories").then().statusCode(200).body("size()", is(0));

        String id = given().contentType(ContentType.JSON)
                .body(dto)
                .post("/specifying-categories")
                .then()
                .statusCode(200)
                .extract().asString();

        get("/specifying-categories").then().statusCode(200).body("size()", is(1));

        given().pathParam("id", id)
                .get("specifying-categories/id/{id}")
                .then()
                .statusCode(200)
                .body("id", is(id))
                .body("title", is(title));
    }

    @Test
    public void testGetAllSpecifyingCategories() {
        String rootId = createRootCategory("Root #1");

        String subCategoryId1 = createSubCategory(rootId, "SubCategory #1");
        String subCategoryId2 = createSubCategory(rootId, "SubCategory #2");
        String subCategoryId3 = createSubCategory(rootId, "SubCategory #3");

        String specCatId1 = createSpecifyingCategory(subCategoryId1, "Specifying category #1 for Subcategory #1");
        String specCatId2 = createSpecifyingCategory(subCategoryId1, "Specifying category #2 for Subcategory #1");
        String specCatId3 = createSpecifyingCategory(subCategoryId1, "Specifying category #3 for Subcategory #1");
        String specCatId4 = createSpecifyingCategory(subCategoryId2, "Specifying category #1 for Subcategory #2");
        String specCatId5 = createSpecifyingCategory(subCategoryId2, "Specifying category #2 for Subcategory #2");
        String specCatId6 = createSpecifyingCategory(subCategoryId2, "Specifying category #3 for Subcategory #2");
        String specCatId7 = createSpecifyingCategory(subCategoryId3, "Specifying category #1 for Subcategory #3");
        String specCatId8 = createSpecifyingCategory(subCategoryId3, "Specifying category #2 for Subcategory #3");
        String specCatId9 = createSpecifyingCategory(subCategoryId3, "Specifying category #3 for Subcategory #3");

        get("/specifying-categories").then().statusCode(200).body("size()", is(9));

        given().get("/specifying-categories")
                .then()
                .statusCode(200)
                .body("id", hasItems(specCatId1, specCatId2, specCatId3, specCatId4, specCatId5,
                        specCatId6, specCatId7, specCatId8, specCatId9))
                .body("title", hasItems("Specifying category #1 for Subcategory #1",
                        "Specifying category #2 for Subcategory #1",
                        "Specifying category #3 for Subcategory #1",
                        "Specifying category #1 for Subcategory #2",
                        "Specifying category #2 for Subcategory #2",
                        "Specifying category #3 for Subcategory #2",
                        "Specifying category #1 for Subcategory #3",
                        "Specifying category #2 for Subcategory #3",
                        "Specifying category #3 for Subcategory #3"));
    }

    @Test
    public void testDeleteSpecifyingCategory() {

        String id = given().contentType(ContentType.JSON)
                .body(new SpecifyingCategoryDTO(null, "Specifying category",
                        createSubCategory(createRootCategory(), "Subcategory")))
                .post("/specifying-categories")
                .then()
                .statusCode(200)
                .extract().asString();

        get("/specifying-categories").then().body("id", contains(id));

        delete("/specifying-categories/id/" + id);

        get("/specifying-categories").then().body("id", not(contains(id)));
    }

    @Test
    public void testUpdateSpecifyingCategory() throws Exception {

        String title = "Specifying category title";

        //first create with a POST
        String id = given().contentType(ContentType.JSON)
                .body(new SpecifyingCategoryDTO(null, title, createSubCategory(createRootCategory("Root category"), "Subcategory")))
                .post("/specifying-categories")
                .then()
                .statusCode(200)
                .extract().asString();

        //check if POST was fine
        get("/specifying-categories/id/" + id).then().body("title", is(title));


        //now change just the title
        String anotherTitle = "another title";

        given().contentType(ContentType.TEXT)
                .body(anotherTitle)
                .pathParam("id", id)
                .put("/specifying-categories/id/{id}/title")
                .then()
                .statusCode(204);

        get("/specifying-categories/id/" + id).then().body("title", is(anotherTitle));
    }

    @Test
    public void testCreateSpecifyingCategoryWithSameTitle() {
        String title = "Unique title";
        String rootId = createRootCategory("Root");
        String subId = createSubCategory(rootId, "Subcategory");
        createSpecifyingCategory(subId, title);

        get("/specifying-categories").then().statusCode(200).body("size()", is(1));

        given().contentType(ContentType.JSON)
                .body(new SpecifyingCategoryDTO(null, title,
                        subId))
                .post("/specifying-categories")
                .then()
                .statusCode(400);

        get("/specifying-categories").then().statusCode(200).body("size()", is(1));
    }

    @Test
    public void testPatchMergeSpecifyingCategory() {

        String title = "SpecifyingCategory";

        String id = createSpecifyingCategory(createSubCategory(createRootCategory(), "Sub"), title);

        String newTitle = "SpecifyingCategory v.2";

        given().contentType("application/merge-patch+json")
                .body("{\"title\":\"" + newTitle + "\"}")
                .patch("/specifying-categories/id/" + id)
                .then()
                .statusCode(204);

        SpecifyingCategoryDTO readBack = given()
                .accept(ContentType.JSON)
                .get("/specifying-categories/id/" + id)
                .then()
                .statusCode(200)
                .extract()
                .as(SpecifyingCategoryDTO.class);

        assertEquals(newTitle, readBack.title);
        assertEquals(id, readBack.id); // should had stayed the same
    }

    //endregion

    //region Testing quiz
    @Test
    public void testCreateAndGetQuiz() {
        String rootCategoryId = createRootCategory("Root Category");
        String subCategoryId = createSubCategory(rootCategoryId, "Subcategory");
        String specifyingCategoryId = createSpecifyingCategory(subCategoryId, "SpecifyingCategory");

        String quizQuestion = "Quiz question";

        Map<String, Boolean> answerMap = getAnswerMap();

        QuizWithCorrectAnswerDTO dto = new QuizWithCorrectAnswerDTO(null, quizQuestion, specifyingCategoryId, answerMap);

        get("/quizzes").then().statusCode(200).body("list.size()", is(0));

        String id = given().contentType(ContentType.JSON)
                .body(dto)
                .post("/quizzes")
                .then()
                .statusCode(200)
                .extract().asString();

        get("/quizzes").then().statusCode(200).body("list.size()", is(1));

        given().pathParam("id", id)
                .get("quizzes/id/{id}")
                .then()
                .statusCode(200)
                .body("id", is(id))
                .body("specifyingCategoryId", is(specifyingCategoryId))
                .body("answerList", is(new ArrayList<>(answerMap.keySet())))
                .body("question", is(quizQuestion));
    }

    @Test
    public void testLinks() {
        for (int i = 0; i < 15; i++) createQuizWithDifferentCategories("" + i, i);

        get("/quizzes").then().statusCode(200).body("list.size()", is(10));

        ListDTO<?> dto = get("/quizzes")
                .then()
                .statusCode(200)
                .extract()
                .as(ListDTO.class);

        assertEquals(15, dto.totalSize.intValue());
        assertNotNull(dto._links.next.href);

        ListDTO<?> nextDto = get(dto._links.next.href)
                .then()
                .statusCode(200)
                .extract()
                .as(ListDTO.class);

        assertEquals(5, nextDto.list.size());
        assertEquals(15, nextDto.totalSize.intValue());
        assertNotNull(nextDto._links.previous.href);

        get(nextDto._links.previous.href).then().statusCode(200).body("list.size()", is(10));
    }


    @Test
    public void testCheckCorrectAnswer() {
        String question = "Quiz question";

        Map<String, Boolean> answerMap = new HashMap<>();
        for (int i = 1; i < 4; i++) {
            answerMap.put("Wrong answer #" + i, false);
        }
        answerMap.put("Correct answer", true);

        String id = createQuiz(createSpecifyingCategory(createSubCategory(createRootCategory(), "Sub"), "Spec"), question, answerMap);

        String isCorrect = given().queryParam("id", id)
                .and()
                .queryParam("answer", "Correct answer")
                .get("answer-check")
                .then()
                .statusCode(200)
                .extract()
                .asString();

        assertEquals("true", isCorrect);

        String isWrong = given().queryParam("id", id)
                .and()
                .queryParam("answer", "Wrong answer #1")
                .get("answer-check")
                .then()
                .statusCode(200)
                .extract()
                .asString();

        assertEquals(isWrong, "false");
    }

    @Test
    public void testGetAllQuizzes() {
        String rootId = createRootCategory("Root #1");

        String subCategoryId1 = createSubCategory(rootId, "SubCategory #1");
        String subCategoryId2 = createSubCategory(rootId, "SubCategory #2");
        String subCategoryId3 = createSubCategory(rootId, "SubCategory #3");

        String specCatId1 = createSpecifyingCategory(subCategoryId1, "Specifying category #1 for Subcategory #1");
        String specCatId2 = createSpecifyingCategory(subCategoryId1, "Specifying category #2 for Subcategory #1");
        String specCatId3 = createSpecifyingCategory(subCategoryId1, "Specifying category #3 for Subcategory #1");
        String specCatId4 = createSpecifyingCategory(subCategoryId2, "Specifying category #1 for Subcategory #2");
        String specCatId5 = createSpecifyingCategory(subCategoryId2, "Specifying category #2 for Subcategory #2");
        String specCatId6 = createSpecifyingCategory(subCategoryId2, "Specifying category #3 for Subcategory #2");
        String specCatId7 = createSpecifyingCategory(subCategoryId3, "Specifying category #1 for Subcategory #3");
        String specCatId8 = createSpecifyingCategory(subCategoryId3, "Specifying category #2 for Subcategory #3");
        String specCatId9 = createSpecifyingCategory(subCategoryId3, "Specifying category #3 for Subcategory #3");


        String quizId1 = createQuiz(specCatId1, "Question #1 from specifying category #1");
        String quizId2 = createQuiz(specCatId2, "Question #1 from specifying category #2");
        String quizId3 = createQuiz(specCatId3, "Question #1 from specifying category #3");
        String quizId4 = createQuiz(specCatId4, "Question #1 from specifying category #4");
        String quizId5 = createQuiz(specCatId5, "Question #1 from specifying category #5");
        String quizId6 = createQuiz(specCatId6, "Question #1 from specifying category #6");
        String quizId7 = createQuiz(specCatId7, "Question #1 from specifying category #7");
        String quizId8 = createQuiz(specCatId8, "Question #1 from specifying category #8");
        String quizId9 = createQuiz(specCatId9, "Question #1 from specifying category #9");

        get("/quizzes").then().statusCode(200).body("list.size()", is(9));

        given().get("/quizzes")
                .then()
                .statusCode(200)
                .body("list.id", hasItems(quizId1,
                        quizId2,
                        quizId3,
                        quizId4,
                        quizId5,
                        quizId6,
                        quizId7,
                        quizId8,
                        quizId9))
                .body("list.question", hasItems("Question #1 from specifying category #1",
                        "Question #1 from specifying category #2",
                        "Question #1 from specifying category #3",
                        "Question #1 from specifying category #4",
                        "Question #1 from specifying category #5",
                        "Question #1 from specifying category #6",
                        "Question #1 from specifying category #7",
                        "Question #1 from specifying category #8",
                        "Question #1 from specifying category #9"));
    }

    @Test
    public void testDeleteQuiz() {

        String id = given().contentType(ContentType.JSON)
                .body(new QuizWithCorrectAnswerDTO(null,
                        "question",
                        createSpecifyingCategory(createSubCategory(createRootCategory("Root"), "Sub"), "Spec"),
                        getAnswerMap()))
                .post("/quizzes")
                .then()
                .statusCode(200)
                .extract().asString();

        get("/quizzes").then().body("list.id", contains(id));

        delete("/quizzes/id/" + id);

        get("/quizzes").then().body("id", not(contains(id)));
    }

    @Test
    public void testUpdateQuizQuestion() throws Exception {

        String question = "Original question";

        //first create with a POST
        String id = given().contentType(ContentType.JSON)
                .body(new QuizWithCorrectAnswerDTO(null,
                        question,
                        createSpecifyingCategory(createSubCategory(createRootCategory("Root"), "Sub"), "Spec"),
                        getAnswerMap()))
                .post("/quizzes")
                .then()
                .statusCode(200)
                .extract().asString();

        //check if POST was fine
        get("/quizzes/id/" + id).then().body("question", is(question));


        //now change just the title
        String updatedQuestion = "Updated question";

        given().contentType(ContentType.TEXT)
                .body(updatedQuestion)
                .pathParam("id", id)
                .put("/quizzes/id/{id}/question")
                .then()
                .statusCode(204);

        get("/quizzes/id/" + id).then().body("question", is(updatedQuestion));
    }

    @Test
    public void testCreateQuizzesWithSameTitle() {
        String uniqueQuestion = "Unique question";
        String rootId = createRootCategory("Root");
        String subId = createSubCategory(rootId, "Subcategory");
        String specId = createSpecifyingCategory(subId, "Spec");
        createQuiz(specId, uniqueQuestion);

        get("/quizzes").then().statusCode(200).body("list.size()", is(1));

        given().contentType(ContentType.JSON)
                .body(new QuizWithCorrectAnswerDTO(null,
                        uniqueQuestion,
                        specId,
                        getAnswerMap()))
                .post("/quizzes")
                .then()
                .statusCode(400);

        get("/quizzes").then().statusCode(200).body("list.size()", is(1));
    }

    @Test
    public void testPatchMergeQuiz() {

        String question = "Quiz question";

        Map<String, Boolean> answerMap = new HashMap<>();
        for (int i = 0; i < 3; i++) {
            answerMap.put("Wrong answer #" + i + 1, false);
        }
        answerMap.put("Correct answer", true);

        String id = createQuiz(createSpecifyingCategory(createSubCategory(createRootCategory(), "Sub"), "Spec"), question, answerMap);

        QuizDTO originalQuiz = given()
                .accept(ContentType.JSON)
                .get("/quizzes/id/" + id)
                .then()
                .statusCode(200)
                .extract()
                .as(QuizDTO.class);

        assertTrue(originalQuiz.answerList.stream().anyMatch(answer -> answer.equals("Correct answer")));

        String newQuestion = "Quiz question v.2";
        String newAnswerMap = "\"answerMap\":{\"answer 1\": true, \"answer 2\": false, \"answer 3\": false, \"answer 4\": false}";
        given().contentType("application/merge-patch+json")
                .body("{\"question\":\"" + newQuestion + "\", " + newAnswerMap + "}")
                .patch("/quizzes/id/" + id)
                .then()
                .statusCode(204);

        QuizDTO readBack = given()
                .accept(ContentType.JSON)
                .get("/quizzes/id/" + id)
                .then()
                .statusCode(200)
                .extract()
                .as(QuizDTO.class);

        assertEquals(newQuestion, readBack.question);
        assertTrue(readBack.answerList.stream().anyMatch(answer -> answer.equals("answer 1")));
        assertEquals(id, readBack.id); // should had stayed the same
    }

    //endregion

    //region Testing custom requests
    @Test
    public void getAllRootCategoriesWithAtLeastOneQuiz() {
        String rootId1 = createRootCategory("Root #1");
        String rootId2 = createRootCategory("Root #2");
        String rootId3 = createRootCategory("Root #3");

        // Each root category has one subcategory
        String subCategoryId1 = createSubCategory(rootId1, "SubCategory #1");
        String subCategoryId2 = createSubCategory(rootId2, "SubCategory #2");
        String subCategoryId3 = createSubCategory(rootId3, "SubCategory #3");

        // Sub #1 (root #1) has spec 1-3, sub #2 (root #2) has spec 4-6 and sub #3 (root #3) has spec 7-9
        String specCatId1 = createSpecifyingCategory(subCategoryId1, "Specifying category #1 for Subcategory #1");
        String specCatId2 = createSpecifyingCategory(subCategoryId1, "Specifying category #2 for Subcategory #1");
        String specCatId3 = createSpecifyingCategory(subCategoryId1, "Specifying category #3 for Subcategory #1");
        String specCatId4 = createSpecifyingCategory(subCategoryId2, "Specifying category #1 for Subcategory #2");
        String specCatId5 = createSpecifyingCategory(subCategoryId2, "Specifying category #2 for Subcategory #2");
        String specCatId6 = createSpecifyingCategory(subCategoryId2, "Specifying category #3 for Subcategory #2");
        String specCatId7 = createSpecifyingCategory(subCategoryId3, "Specifying category #1 for Subcategory #3");
        String specCatId8 = createSpecifyingCategory(subCategoryId3, "Specifying category #2 for Subcategory #3");
        String specCatId9 = createSpecifyingCategory(subCategoryId3, "Specifying category #3 for Subcategory #3");

        // Spec #4 has 3 quizzes, spec #1 and #6 has 2 quizzes, spec #3 and #5 has 1 quiz, spec #2, 7, 8 and 9 has no quizzes
        createQuiz(specCatId1, "Question #1 from specifying category #1");
        createQuiz(specCatId1, "Question #2 from specifying category #1");
        createQuiz(specCatId3, "Question #1 from specifying category #3");
        createQuiz(specCatId4, "Question #1 from specifying category #4");
        createQuiz(specCatId4, "Question #2 from specifying category #4");
        createQuiz(specCatId4, "Question #3 from specifying category #4");
        createQuiz(specCatId5, "Question #1 from specifying category #5");
        createQuiz(specCatId6, "Question #1 from specifying category #6");
        createQuiz(specCatId6, "Question #2 from specifying category #6");

        // Thus, root #1 has 3 quizzes, root #2 has 6 quizzes, root #3 has no quizzes
        get("/categories/withQuizzes").then().statusCode(200).body("list.size()", is(2));

        given().get("/categories/withQuizzes")
                .then()
                .statusCode(200)
                .body("list.id", hasItems(rootId1, rootId2))
                .body("list.title", hasItems("Root #1", "Root #2"))
                .body("list.title", not("Root #3"));
    }

    @Test
    public void getAllSpecifyingCategoriesWithAtLeastOneQuiz() {
        // Each root category has one subcategory
        String subCategoryId1 = createSubCategory(createRootCategory("Root #1"), "SubCategory #1");
        String subCategoryId2 = createSubCategory(createRootCategory("Root #2"), "SubCategory #2");
        String subCategoryId3 = createSubCategory(createRootCategory("Root #3"), "SubCategory #3");

        // Sub #1 (root #1) has spec 1-3, sub #2 (root #2) has spec 4-6 and sub #3 (root #3) has spec 7-9
        String specCatId1 = createSpecifyingCategory(subCategoryId1, "Specifying category #1 for Subcategory #1");
        String specCatId2 = createSpecifyingCategory(subCategoryId1, "Specifying category #2 for Subcategory #1");
        String specCatId3 = createSpecifyingCategory(subCategoryId1, "Specifying category #3 for Subcategory #1");
        String specCatId4 = createSpecifyingCategory(subCategoryId2, "Specifying category #1 for Subcategory #2");
        String specCatId5 = createSpecifyingCategory(subCategoryId2, "Specifying category #2 for Subcategory #2");
        String specCatId6 = createSpecifyingCategory(subCategoryId2, "Specifying category #3 for Subcategory #2");
        String specCatId7 = createSpecifyingCategory(subCategoryId3, "Specifying category #1 for Subcategory #3");
        String specCatId8 = createSpecifyingCategory(subCategoryId3, "Specifying category #2 for Subcategory #3");
        String specCatId9 = createSpecifyingCategory(subCategoryId3, "Specifying category #3 for Subcategory #3");

        // Spec #4 has 3 quizzes, spec #1 and #6 has 2 quizzes, spec #3 and #5 has 1 quiz, spec #2, 7, 8 and 9 has no quizzes
        createQuiz(specCatId1, "Question #1 from specifying category #1");
        createQuiz(specCatId1, "Question #2 from specifying category #1");
        createQuiz(specCatId3, "Question #1 from specifying category #3");
        createQuiz(specCatId4, "Question #1 from specifying category #4");
        createQuiz(specCatId4, "Question #2 from specifying category #4");
        createQuiz(specCatId4, "Question #3 from specifying category #4");
        createQuiz(specCatId5, "Question #1 from specifying category #5");
        createQuiz(specCatId6, "Question #1 from specifying category #6");
        createQuiz(specCatId6, "Question #2 from specifying category #6");

        // Thus, there are 5 specifying categories with at least one quiz
        get("/categories/withQuizzes/specifying-categories").then().statusCode(200).body("size()", is(5));

        given().get("/categories/withQuizzes/specifying-categories")
                .then()
                .statusCode(200)
                .body("id", hasItems(specCatId1,
                        specCatId3,
                        specCatId4,
                        specCatId5,
                        specCatId6))
                .body("title", hasItems("Specifying category #1 for Subcategory #1",
                        "Specifying category #3 for Subcategory #1",
                        "Specifying category #1 for Subcategory #2",
                        "Specifying category #2 for Subcategory #2",
                        "Specifying category #3 for Subcategory #2"))
                .body("title", not("Specifying category #2 for Subcategory #1"))
                .body("title", not("Specifying category #1 for Subcategory #3"))
                .body("title", not("Specifying category #2 for Subcategory #3"))
                .body("title", not("Specifying category #3 for Subcategory #3"));
    }

    @Test
    public void testGetSubCategoriesForRootCategory() {
        testGetSubCategoriesForRootWithSpecifiedPath("/categories/id/{id}/subcategories");
    }

    @Test
    public void testGetAllSubCategoriesForParent() {
        testGetSubCategoriesForRootWithSpecifiedPath("/subcategories/parent/{id}");
    }

    @Test
    public void testGetAllSpecifyingCategoriesForSubCategory() {
        testGetSpecifyingCategoriesForSubWithSpecifiedPath("/subcategories/id/{id}/specifying-categories");
    }

    @Test
    public void testGetAllSpecifyingCategoriesForParent() {
        testGetSpecifyingCategoriesForSubWithSpecifiedPath("/specifying-categories/parent/{id}");
    }

    @Test
    public void testGetAllQuizzesForParent() {
        String rootId = createRootCategory("Root #1");

        // Each root category has one subcategory
        String subCategoryId1 = createSubCategory(rootId, "SubCategory #1");
        String subCategoryId2 = createSubCategory(rootId, "SubCategory #2");
        String subCategoryId3 = createSubCategory(rootId, "SubCategory #3");

        // Sub #1 has spec 1-3, sub #2 has spec 4-6 and sub #3  has spec 7-9
        String specCatId1 = createSpecifyingCategory(subCategoryId1, "Specifying category #1 for Subcategory #1");
        String specCatId2 = createSpecifyingCategory(subCategoryId1, "Specifying category #2 for Subcategory #1");
        String specCatId3 = createSpecifyingCategory(subCategoryId1, "Specifying category #3 for Subcategory #1");
        String specCatId4 = createSpecifyingCategory(subCategoryId2, "Specifying category #1 for Subcategory #2");
        String specCatId5 = createSpecifyingCategory(subCategoryId2, "Specifying category #2 for Subcategory #2");
        String specCatId6 = createSpecifyingCategory(subCategoryId2, "Specifying category #3 for Subcategory #2");
        String specCatId7 = createSpecifyingCategory(subCategoryId3, "Specifying category #1 for Subcategory #3");
        String specCatId8 = createSpecifyingCategory(subCategoryId3, "Specifying category #2 for Subcategory #3");
        String specCatId9 = createSpecifyingCategory(subCategoryId3, "Specifying category #3 for Subcategory #3");

        // Spec #4 has 3 quizzes, spec #1 and #6 has 2 quizzes, spec #3 and #5 has 1 quiz, spec #2, 7, 8 and 9 has no quizzes
        createQuiz(specCatId1, "Question #1 from specifying category #1");
        createQuiz(specCatId1, "Question #2 from specifying category #1");
        createQuiz(specCatId3, "Question #1 from specifying category #3");
        createQuiz(specCatId4, "Question #1 from specifying category #4");
        createQuiz(specCatId4, "Question #2 from specifying category #4");
        createQuiz(specCatId4, "Question #3 from specifying category #4");
        createQuiz(specCatId5, "Question #1 from specifying category #5");
        createQuiz(specCatId6, "Question #1 from specifying category #6");
        createQuiz(specCatId6, "Question #2 from specifying category #6");

        given().pathParam("id", rootId)
                .get("/quizzes/parent/{id}").then().statusCode(200).body("list.size()", is(9));

        given().pathParam("id", subCategoryId2)
                .get("/quizzes/parent/{id}").then().statusCode(200).body("list.size()", is(6));

        given().pathParam("id", specCatId1)
                .get("/quizzes/parent/{id}").then().statusCode(200).body("list.size()", is(2));
    }

    @Test
    public void testGetRandomQuiz() {
        String rootId = createRootCategory("Root");
        String subId = createSubCategory(rootId, "Sub");
        String specID = createSpecifyingCategory(subId, "Spec");
        String quizId = createQuiz(specID, "Question");

        get("/randomQuiz").then().statusCode(200).body("id", is(quizId));

        get("/randomQuiz?filter=r_" + rootId).then().statusCode(200).body("id", is(quizId));

        get("/randomQuiz?filter=s_" + subId).then().statusCode(200).body("id", is(quizId));

        get("/randomQuiz?filter=sp_" + specID).then().statusCode(200).body("id", is(quizId));
    }

    @Test
    public void testGetRandomQuizzes() {
        String rootId = createRootCategory("Root");
        String subId = createSubCategory(rootId, "Sub");
        String specId = createSpecifyingCategory(subId, "Spec");
        String quizId1 = createQuiz(specId, "Question 1");
        String quizId2 = createQuiz(specId, "Question 2");
        String quizId3 = createQuiz(specId, "Question 3");
        String quizId4 = createQuiz(specId, "Question 4");
        String quizId5 = createQuiz(specId, "Question 5");

        post("/randomQuizzes")
                .then()
                .statusCode(200)
                .body("size()", is(5));

        String ids[] = post("/randomQuizzes")
                .then()
                .statusCode(200)
                .extract().as(String[].class);

        List<String> quizIds = Arrays.asList(quizId1, quizId2, quizId3, quizId4, quizId5);

        assertTrue(quizIds.containsAll(Arrays.asList(ids)));

        given().queryParam("limit", 3)
                .post("/randomQuizzes")
                .then()
                .statusCode(200)
                .body("size()", is(3));

        given().queryParam("filter", "r_" + rootId)
                .post("/randomQuizzes")
                .then()
                .statusCode(200)
                .body("size()", is(5));

        ids = given().queryParam("filter", "r_" + rootId)
                .post("/randomQuizzes")
                .then()
                .statusCode(200)
                .extract().as(String[].class);

        assertTrue(quizIds.containsAll(Arrays.asList(ids)));

        given().queryParam("limit", 3)
                .and()
                .queryParam("filter", "r_" + rootId)
                .post("/randomQuizzes")
                .then()
                .statusCode(200)
                .body("size()", is(3));

        given().queryParam("limit", 2)
                .and()
                .queryParam("filter", "s_" + subId)
                .post("/randomQuizzes")
                .then()
                .statusCode(200)
                .body("size()", is(2));

        given().queryParam("limit", 1)
                .and()
                .queryParam("filter", "sp_" + specId)
                .post("/randomQuizzes")
                .then()
                .statusCode(200)
                .body("size()", is(1));
    }

    //endregion

    //region Testing new paths
    @Test
    public void testCreateAndGetRootWithNewPath() {

        String title = "Title";

        get("/categories").then().statusCode(200).body("list.size()", is(0));

        String id = createRootCategory(title);

        get("/categories").then().statusCode(200).body("list.size()", is(1));

        given().pathParam("id", id)
                .get("categories/{id}")
                .then()
                .statusCode(200)
                .body("id", is(id))
                .body("title", is(title));
    }

    @Test
    public void testCreateAndGetSubCategoryWithNewPathVersion002() {
        String title = "SubCategory";

        get("/subcategories").then().statusCode(200).body("size()", is(0));

        String id = createSubCategory(createRootCategory("Root"), title);

        get("/subcategories").then().statusCode(200).body("size()", is(1));

        given().pathParam("id", id)
                .get("subcategories/{id}")
                .then()
                .statusCode(200)
                .body("id", is(id))
                .body("title", is(title));
    }

    @Test
    public void testCreateAndGetSpecifyingCategoryWithNewPathVersion002() {
        String title = "Specifying category";

        get("/specifying-categories").then().statusCode(200).body("size()", is(0));

        String id = createSpecifyingCategory(createSubCategory(createRootCategory("Root"), "Sub"), title);

        get("/specifying-categories").then().statusCode(200).body("size()", is(1));

        given().pathParam("id", id)
                .get("specifying-categories/{id}")
                .then()
                .statusCode(200)
                .body("id", is(id))
                .body("title", is(title));
    }

    @Test
    public void testCreateAndGetQuizWithNewPathVersion002() {

        String quizQuestion = "Quiz question";

        Map<String, Boolean> answerMap = getAnswerMap();

        String specifyingCategoryId = createSpecifyingCategory(createSubCategory(createRootCategory("Root"), "Sub"),
                "Specifying category");

        get("/quizzes").then().statusCode(200).body("list.size()", is(0));

        String id = createQuiz(specifyingCategoryId, quizQuestion, answerMap);

        get("/quizzes").then().statusCode(200).body("list.size()", is(1));

        given().pathParam("id", id)
                .get("quizzes/{id}")
                .then()
                .statusCode(200)
                .body("id", is(id))
                .body("specifyingCategoryId", is(specifyingCategoryId))
                .body("answerList", is(new ArrayList<>(answerMap.keySet())))
                .body("question", is(quizQuestion));
    }

    @Test
    public void getAllSpecifyingCategoriesWithAtLeastOneQuizWithNewPathVersion002() {
        // Each root category has one subcategory
        String subCategoryId1 = createSubCategory(createRootCategory("Root #1"), "SubCategory #1");
        String subCategoryId2 = createSubCategory(createRootCategory("Root #2"), "SubCategory #2");
        String subCategoryId3 = createSubCategory(createRootCategory("Root #3"), "SubCategory #3");

        // Sub #1 (root #1) has spec 1-3, sub #2 (root #2) has spec 4-6 and sub #3 (root #3) has spec 7-9
        String specCatId1 = createSpecifyingCategory(subCategoryId1, "Specifying category #1 for Subcategory #1");
        String specCatId2 = createSpecifyingCategory(subCategoryId1, "Specifying category #2 for Subcategory #1");
        String specCatId3 = createSpecifyingCategory(subCategoryId1, "Specifying category #3 for Subcategory #1");
        String specCatId4 = createSpecifyingCategory(subCategoryId2, "Specifying category #1 for Subcategory #2");
        String specCatId5 = createSpecifyingCategory(subCategoryId2, "Specifying category #2 for Subcategory #2");
        String specCatId6 = createSpecifyingCategory(subCategoryId2, "Specifying category #3 for Subcategory #2");
        String specCatId7 = createSpecifyingCategory(subCategoryId3, "Specifying category #1 for Subcategory #3");
        String specCatId8 = createSpecifyingCategory(subCategoryId3, "Specifying category #2 for Subcategory #3");
        String specCatId9 = createSpecifyingCategory(subCategoryId3, "Specifying category #3 for Subcategory #3");

        // Spec #4 has 3 quizzes, spec #1 and #6 has 2 quizzes, spec #3 and #5 has 1 quiz, spec #2, 7, 8 and 9 has no quizzes
        createQuiz(specCatId1, "Question #1 from specifying category #1");
        createQuiz(specCatId1, "Question #2 from specifying category #1");
        createQuiz(specCatId3, "Question #1 from specifying category #3");
        createQuiz(specCatId4, "Question #1 from specifying category #4");
        createQuiz(specCatId4, "Question #2 from specifying category #4");
        createQuiz(specCatId4, "Question #3 from specifying category #4");
        createQuiz(specCatId5, "Question #1 from specifying category #5");
        createQuiz(specCatId6, "Question #1 from specifying category #6");
        createQuiz(specCatId6, "Question #2 from specifying category #6");

        // Thus, there are 5 specifying categories with at least one quiz
        get("/specifying-categories").then().statusCode(200).body("size()", is(9));

        get("/specifying-categories?withQuizzes=false").then().statusCode(200).body("size()", is(9));

        get("/specifying-categories?withQuizzes").then().statusCode(200).body("size()", is(5));

        get("/specifying-categories?withQuizzes=true").then().statusCode(200).body("size()", is(5));

        given().get("/specifying-categories?withQuizzes=true")
                .then()
                .statusCode(200)
                .body("id", hasItems(specCatId1,
                        specCatId3,
                        specCatId4,
                        specCatId5,
                        specCatId6))
                .body("title", hasItems("Specifying category #1 for Subcategory #1",
                        "Specifying category #3 for Subcategory #1",
                        "Specifying category #1 for Subcategory #2",
                        "Specifying category #2 for Subcategory #2",
                        "Specifying category #3 for Subcategory #2"))
                .body("title", not("Specifying category #2 for Subcategory #1"))
                .body("title", not("Specifying category #1 for Subcategory #3"))
                .body("title", not("Specifying category #2 for Subcategory #3"))
                .body("title", not("Specifying category #3 for Subcategory #3"));
    }

    @Test
    public void getAllRootCategoriesWithAtLeastOneQuizWithNewPathVersion002() {
        String rootId1 = createRootCategory("Root #1");
        String rootId2 = createRootCategory("Root #2");
        String rootId3 = createRootCategory("Root #3");

        // Each root category has one subcategory
        String subCategoryId1 = createSubCategory(rootId1, "SubCategory #1");
        String subCategoryId2 = createSubCategory(rootId2, "SubCategory #2");
        String subCategoryId3 = createSubCategory(rootId3, "SubCategory #3");

        // Sub #1 (root #1) has spec 1-3, sub #2 (root #2) has spec 4-6 and sub #3 (root #3) has spec 7-9
        String specCatId1 = createSpecifyingCategory(subCategoryId1, "Specifying category #1 for Subcategory #1");
        String specCatId2 = createSpecifyingCategory(subCategoryId1, "Specifying category #2 for Subcategory #1");
        String specCatId3 = createSpecifyingCategory(subCategoryId1, "Specifying category #3 for Subcategory #1");
        String specCatId4 = createSpecifyingCategory(subCategoryId2, "Specifying category #1 for Subcategory #2");
        String specCatId5 = createSpecifyingCategory(subCategoryId2, "Specifying category #2 for Subcategory #2");
        String specCatId6 = createSpecifyingCategory(subCategoryId2, "Specifying category #3 for Subcategory #2");
        String specCatId7 = createSpecifyingCategory(subCategoryId3, "Specifying category #1 for Subcategory #3");
        String specCatId8 = createSpecifyingCategory(subCategoryId3, "Specifying category #2 for Subcategory #3");
        String specCatId9 = createSpecifyingCategory(subCategoryId3, "Specifying category #3 for Subcategory #3");

        // Spec #4 has 3 quizzes, spec #1 and #6 has 2 quizzes, spec #3 and #5 has 1 quiz, spec #2, 7, 8 and 9 has no quizzes
        createQuiz(specCatId1, "Question #1 from specifying category #1");
        createQuiz(specCatId1, "Question #2 from specifying category #1");
        createQuiz(specCatId3, "Question #1 from specifying category #3");
        createQuiz(specCatId4, "Question #1 from specifying category #4");
        createQuiz(specCatId4, "Question #2 from specifying category #4");
        createQuiz(specCatId4, "Question #3 from specifying category #4");
        createQuiz(specCatId5, "Question #1 from specifying category #5");
        createQuiz(specCatId6, "Question #1 from specifying category #6");
        createQuiz(specCatId6, "Question #2 from specifying category #6");

        // Thus, root #1 has 3 quizzes, root #2 has 6 quizzes, root #3 has no quizzes
        get("/categories").then().statusCode(200).body("list.size()", is(3));

        get("/categories?withQuizzes=false").then().statusCode(200).body("list.size()", is(3));

        get("/categories?withQuizzes").then().statusCode(200).body("list.size()", is(2));

        get("/categories?withQuizzes=true").then().statusCode(200).body("list.size()", is(2));

        given().get("/categories?withQuizzes")
                .then()
                .statusCode(200)
                .body("list.id", hasItems(rootId1, rootId2))
                .body("list.title", hasItems("Root #1", "Root #2"))
                .body("list.title", not("Root #3"));
    }

    @Test
    public void testGetSubCategoriesForRootCategoryWithNewPathVersion002() {
        testGetSubCategoriesForRootWithSpecifiedPath("/categories/{id}/subcategories");
    }

    @Test
    public void testGetAllSpecifyingCategoriesForSubCategoryWithNewPathVersion002() {
        testGetSpecifyingCategoriesForSubWithSpecifiedPath("/subcategories/{id}/specifying-categories");
    }
    //endregion

    //region Util methods
    private String createRootCategory(String title) {
        return given().contentType(ContentType.JSON)
                .body(new RootCategoryDTO(null, title, null, null))
                .post("/categories")
                .then()
                .statusCode(200)
                .extract().asString();
    }

    private String createRootCategory() {
        return createRootCategory("RootCategory");
    }

    private String createSubCategory(String rootCategoryId, String title) {
        return given().contentType(ContentType.JSON)
                .body(new SubCategoryDTO(null, title, rootCategoryId))
                .post("/subcategories")
                .then()
                .statusCode(200)
                .extract().asString();
    }

    private String createSpecifyingCategory(String subCategoryId, String title) {
        return given().contentType(ContentType.JSON)
                .body(new SpecifyingCategoryDTO(null, title, subCategoryId))
                .post("/specifying-categories")
                .then()
                .statusCode(200)
                .extract().asString();
    }

    private String createQuizWithDifferentCategories(String question, int index) {
        return createQuiz(
                createSpecifyingCategory(
                createSubCategory(
                createRootCategory("Root" + index),
                "Sub" + index),
                "Spec" + index),
                question);
    }

    private String createQuiz(String specCatId, String question) {
        return given().contentType(ContentType.JSON)
                .body(new QuizWithCorrectAnswerDTO(null, question, specCatId, getAnswerMap()))
                .post("/quizzes")
                .then()
                .statusCode(200)
                .extract().asString();
    }

    private String createQuiz(String specCatId, String question, Map<String, Boolean> answerMap) {
        return given().contentType(ContentType.JSON)
                .body(new QuizWithCorrectAnswerDTO(null, question, specCatId, answerMap))
                .post("/quizzes")
                .then()
                .statusCode(200)
                .extract().asString();
    }

    private Map<String, Boolean> getAnswerMap() {
        Map<String, Boolean> answerMap = new HashMap<>();
        for (int i = 0; i < 3; i++) {
            answerMap.put("Wrong answer #" + i + 1, false);
        }
        answerMap.put("Correct answer", true);
        return answerMap;
    }

    private void testGetSubCategoriesForRootWithSpecifiedPath(String path) {
        String rootId1 = createRootCategory("Root #1");
        String rootId2 = createRootCategory("Root #2");
        String rootId3 = createRootCategory("Root #3");

        // Root #1 has sub #1-4, root #2 has sub #5-6, root #3 has sub #7
        String subCategoryId1 = createSubCategory(rootId1, "SubCategory #1.1");
        String subCategoryId2 = createSubCategory(rootId1, "SubCategory #1.2");
        String subCategoryId3 = createSubCategory(rootId1, "SubCategory #1.3");
        String subCategoryId4 = createSubCategory(rootId1, "SubCategory #1.4");
        String subCategoryId5 = createSubCategory(rootId2, "SubCategory #2.1");
        String subCategoryId6 = createSubCategory(rootId2, "SubCategory #2.2");
        String subCategoryId7 = createSubCategory(rootId3, "SubCategory #3.1");

        given().pathParam("id", rootId1)
                .get(path)
                .then()
                .statusCode(200)
                .body("id", hasItems(subCategoryId1,
                        subCategoryId2,
                        subCategoryId3,
                        subCategoryId4))
                .body("title", hasItems("SubCategory #1.1",
                        "SubCategory #1.2",
                        "SubCategory #1.3",
                        "SubCategory #1.4"));

        given().pathParam("id", rootId2)
                .get(path)
                .then()
                .statusCode(200)
                .body("id", hasItems(subCategoryId5, subCategoryId6))
                .body("title", hasItems("SubCategory #2.1", "SubCategory #2.2"));

        given().pathParam("id", rootId3)
                .get(path)
                .then()
                .statusCode(200)
                .body("id", hasItems(subCategoryId7))
                .body("title", hasItems("SubCategory #3.1"));
    }

    private void testGetSpecifyingCategoriesForSubWithSpecifiedPath(String path) {
        // Each root category has one subcategory
        String subCategoryId1 = createSubCategory(createRootCategory("Root #1"), "SubCategory #1");
        String subCategoryId2 = createSubCategory(createRootCategory("Root #2"), "SubCategory #2");
        String subCategoryId3 = createSubCategory(createRootCategory("Root #3"), "SubCategory #3");

        // Sub #1 (root #1) has spec 1-4, sub #2 (root #2) has spec 5-6 and sub #3 (root #3) has spec 7-9
        String specCatId1 = createSpecifyingCategory(subCategoryId1, "Specifying category #1 for Subcategory #1");
        String specCatId2 = createSpecifyingCategory(subCategoryId1, "Specifying category #2 for Subcategory #1");
        String specCatId3 = createSpecifyingCategory(subCategoryId1, "Specifying category #3 for Subcategory #1");
        String specCatId4 = createSpecifyingCategory(subCategoryId1, "Specifying category #4 for Subcategory #1");
        String specCatId5 = createSpecifyingCategory(subCategoryId2, "Specifying category #1 for Subcategory #2");
        String specCatId6 = createSpecifyingCategory(subCategoryId2, "Specifying category #2 for Subcategory #2");
        String specCatId7 = createSpecifyingCategory(subCategoryId3, "Specifying category #1 for Subcategory #3");
        String specCatId8 = createSpecifyingCategory(subCategoryId3, "Specifying category #2 for Subcategory #3");
        String specCatId9 = createSpecifyingCategory(subCategoryId3, "Specifying category #3 for Subcategory #3");

        given().pathParam("id", subCategoryId1)
                .get(path)
                .then()
                .statusCode(200)
                .body("id", hasItems(specCatId1,
                        specCatId2,
                        specCatId3,
                        specCatId4))
                .body("title", hasItems("Specifying category #1 for Subcategory #1",
                        "Specifying category #2 for Subcategory #1",
                        "Specifying category #3 for Subcategory #1",
                        "Specifying category #4 for Subcategory #1"));

        given().pathParam("id", subCategoryId2)
                .get(path)
                .then()
                .statusCode(200)
                .body("id", hasItems(specCatId5, specCatId6))
                .body("title", hasItems("Specifying category #1 for Subcategory #2",
                        "Specifying category #2 for Subcategory #2"));

        given().pathParam("id", subCategoryId3)
                .get(path)
                .then()
                .statusCode(200)
                .body("id", hasItems(specCatId7,
                        specCatId8,
                        specCatId9))
                .body("title", hasItems("Specifying category #1 for Subcategory #3",
                        "Specifying category #2 for Subcategory #3",
                        "Specifying category #3 for Subcategory #3"));
    }

    //endregion
}
