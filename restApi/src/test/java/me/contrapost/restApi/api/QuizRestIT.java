package me.contrapost.restApi.api;

import io.restassured.http.ContentType;
import me.contrapost.restApi.dto.RootCategoryDTO;
import me.contrapost.restApi.dto.SpecifyingCategoryDTO;
import me.contrapost.restApi.dto.SubCategoryDTO;
import org.junit.Ignore;
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

    //region Testing specifying categories
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

    //endregion
}
