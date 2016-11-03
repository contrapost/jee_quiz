package me.contrapost.restApi.api;

import io.restassured.http.ContentType;
import me.contrapost.restApi.dto.RootCategoryDTO;
import me.contrapost.restApi.dto.SubCategoryDTO;
import org.junit.Test;

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

    // ================= Testing root category ===========================


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

    @Test
    public void testPatchMergeRootCategory(){

        String title = "RootCategory";

        String id = createRootCategory(title);

        String newTitle = "RootCategory v.2";

        //just change the value
        patchWithMergeJSon(id, "{\"title\": \"" +  newTitle +"\"}", 204);

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

    private void patchWithMergeJSon(String id, String jsonBody, int statusCode) {
        given().port(8080)
                .baseUri("http://localhost")
                .contentType("application/merge-patch+json")
                .body(jsonBody)
                .patch("/categories/id/" + id)
                .then()
                .statusCode(statusCode);
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

    // ==================== Testing subcategory =========================

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

    // ===================== Util methods ============================

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
}
