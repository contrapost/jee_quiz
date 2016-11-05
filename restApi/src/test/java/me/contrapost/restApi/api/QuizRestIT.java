package me.contrapost.restApi.api;

import io.restassured.http.ContentType;
import me.contrapost.restApi.dto.QuizDTO;
import me.contrapost.restApi.dto.RootCategoryDTO;
import me.contrapost.restApi.dto.SpecifyingCategoryDTO;
import me.contrapost.restApi.dto.SubCategoryDTO;
import org.junit.Ignore;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.*;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;

/**
 * Created by alexandershipunov on 30/10/2016.
 * Test suite for QuizRest API
 */
public class QuizRestIT extends QuizRestTestBase {

    @Test
    public void testCleanDB() {

        get("/categories").then()
                .statusCode(200)
                .body("size()", is(0));
    }

    //region Testing root category

    @Test
    public void testCreateAndGetRoot() {

        String title = "Title";

        RootCategoryDTO dto = new RootCategoryDTO(null, title);

        get("/categories").then().statusCode(200).body("size()", is(0));

        String id = given().contentType(ContentType.JSON)
                .body(dto)
                .post("/categories")
                .then()
                .statusCode(200)
                .extract().asString();

        get("/categories").then().statusCode(200).body("size()", is(1));

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

        get("/categories").then().statusCode(200).body("size()", is(3));

        given().get("/categories")
                .then()
                .statusCode(200)
                .body("id", hasItems(rootId1, rootId2, rootId3))
                .body("title", hasItems("Root #1", "Root #2", "Root #3"));
    }

    @Test
    public void testDeleteRootCategory() {

        String id = given().contentType(ContentType.JSON)
                .body(new RootCategoryDTO(null, "Root"))
                .post("/categories")
                .then()
                .statusCode(200)
                .extract().asString();

        get("/categories").then().body("id", contains(id));

        delete("/categories/id/" + id);

        get("/categories").then().body("id", not(contains(id)));
    }

    @Test
    public void testUpdateRootCategory() throws Exception {

        String title = "Title";

        //first create with a POST
        String id = given().contentType(ContentType.JSON)
                .body(new RootCategoryDTO(null, title))
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

    @Ignore
    @Test
    public void testPatchMergeRootCategory() {

        String title = "RootCategory";

        String id = createRootCategory(title);

        String newTitle = "TEST";

        given().port(8080)
                .baseUri("http://localhost")
                .contentType("application/merge-patch+json")
                .body("{\"title\": \"" + newTitle + "\"}")
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
                .body(new RootCategoryDTO(null, title))
                .post("/categories")
                .then()
                .statusCode(200);

        get("/categories").then().statusCode(200).body("size()", is(1));

        given().contentType(ContentType.JSON)
                .body(new RootCategoryDTO(null, title))
                .post("/categories")
                .then()
                .statusCode(400);

        get("/categories").then().statusCode(200).body("size()", is(1));
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

    //endregion

    //region Testing quiz
    @Test
    public void testCreateAndGetQuiz() {
        String rootCategoryId = createRootCategory("Root Category");
        String subCategoryId = createSubCategory(rootCategoryId, "Subcategory");
        String specifyingCategoryId = createSpecifyingCategory(subCategoryId, "SpecifyingCategory");

        String quizQuestion = "Quiz question";

        Map<String, Boolean> answerMap = getAnswerMap();

        QuizDTO dto = new QuizDTO(null, quizQuestion, specifyingCategoryId, answerMap);

        get("/quizes").then().statusCode(200).body("size()", is(0));

        String id = given().contentType(ContentType.JSON)
                .body(dto)
                .post("/quizes")
                .then()
                .statusCode(200)
                .extract().asString();

        get("/quizes").then().statusCode(200).body("size()", is(1));

        given().pathParam("id", id)
                .get("quizes/id/{id}")
                .then()
                .statusCode(200)
                .body("id", is(id))
                .body("specifyingCategoryId", is(specifyingCategoryId))
                .body("answerMap", is(answerMap))
                .body("question", is(quizQuestion));
    }

    @Test
    public void testGetAllQuizes() {
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

        get("/quizes").then().statusCode(200).body("size()", is(9));

        given().get("/quizes")
                .then()
                .statusCode(200)
                .body("id", hasItems(quizId1,
                        quizId2,
                        quizId3,
                        quizId4,
                        quizId5,
                        quizId6,
                        quizId7,
                        quizId8,
                        quizId9))
                .body("question", hasItems("Question #1 from specifying category #1",
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
                .body(new QuizDTO(null,
                        "question",
                        createSpecifyingCategory(createSubCategory(createRootCategory("Root"), "Sub"), "Spec"),
                        getAnswerMap()))
                .post("/quizes")
                .then()
                .statusCode(200)
                .extract().asString();

        get("/quizes").then().body("id", contains(id));

        delete("/quizes/id/" + id);

        get("/quizes").then().body("id", not(contains(id)));
    }

    @Test
    public void testUpdateQuizQuestion() throws Exception {

        String question = "Original question";

        //first create with a POST
        String id = given().contentType(ContentType.JSON)
                .body(new QuizDTO(null,
                        question,
                        createSpecifyingCategory(createSubCategory(createRootCategory("Root"), "Sub"), "Spec"),
                        getAnswerMap()))
                .post("/quizes")
                .then()
                .statusCode(200)
                .extract().asString();

        //check if POST was fine
        get("/quizes/id/" + id).then().body("question", is(question));


        //now change just the title
        String updatedQuestion = "Updated question";

        given().contentType(ContentType.TEXT)
                .body(updatedQuestion)
                .pathParam("id", id)
                .put("/quizes/id/{id}/question")
                .then()
                .statusCode(204);

        get("/quizes/id/" + id).then().body("question", is(updatedQuestion));
    }

    @Test
    public void testCreateQuizesWithSameTitle() {
        String uniqueQuestion = "Unique question";
        String rootId = createRootCategory("Root");
        String subId = createSubCategory(rootId, "Subcategory");
        String specId = createSpecifyingCategory(subId, "Spec");
        createQuiz(specId, uniqueQuestion);

        get("/quizes").then().statusCode(200).body("size()", is(1));

        given().contentType(ContentType.JSON)
                .body(new QuizDTO(null,
                        uniqueQuestion,
                        specId,
                        getAnswerMap()))
                .post("/quizes")
                .then()
                .statusCode(400);

        get("/quizes").then().statusCode(200).body("size()", is(1));
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

        // Spec #4 has 3 quizes, spec #1 and #6 has 2 quizes, spec #3 and #5 has 1 quiz, spec #2, 7, 8 and 9 has no quizes
        createQuiz(specCatId1, "Question #1 from specifying category #1");
        createQuiz(specCatId1, "Question #2 from specifying category #1");
        createQuiz(specCatId3, "Question #1 from specifying category #3");
        createQuiz(specCatId4, "Question #1 from specifying category #4");
        createQuiz(specCatId4, "Question #2 from specifying category #4");
        createQuiz(specCatId4, "Question #3 from specifying category #4");
        createQuiz(specCatId5, "Question #1 from specifying category #5");
        createQuiz(specCatId6, "Question #1 from specifying category #6");
        createQuiz(specCatId6, "Question #2 from specifying category #6");

        // Thus, root #1 has 3 quizes, root #2 has 6 quizes, root #3 has no quizes
        get("/categories/withQuizzes").then().statusCode(200).body("size()", is(2));

        given().get("/categories/withQuizzes")
                .then()
                .statusCode(200)
                .body("id", hasItems(rootId1, rootId2))
                .body("title", hasItems("Root #1", "Root #2"))
                .body("title", not("Root #3"));
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

        // Spec #4 has 3 quizes, spec #1 and #6 has 2 quizes, spec #3 and #5 has 1 quiz, spec #2, 7, 8 and 9 has no quizes
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

    //endregion

    //region Util methods
    private String createRootCategory(String title) {
        return given().contentType(ContentType.JSON)
                .body(new RootCategoryDTO(null, title))
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

    private String createQuiz(String specCatId, String question) {
        return given().contentType(ContentType.JSON)
                .body(new QuizDTO(null, question, specCatId, getAnswerMap()))
                .post("/quizes")
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
