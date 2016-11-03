package me.contrapost.restApi.api;

import io.restassured.http.ContentType;
import me.contrapost.restApi.dto.RootCategoryDTO;
import org.junit.Test;

import java.time.ZonedDateTime;

import static io.restassured.RestAssured.delete;
import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.core.Is.is;

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
    public void testDelete() {

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
    public void testUpdate() throws Exception {

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
    public void testCreateWithSameTitle() {
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
}
