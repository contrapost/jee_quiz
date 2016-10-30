package me.contrapost.restApi.api;

import io.restassured.http.ContentType;
import me.contrapost.jee_quiz.entity.SubCategory;
import me.contrapost.restApi.dto.RootCategoryDTO;
import org.junit.Ignore;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static org.hamcrest.core.Is.is;

/**
 * Created by alexandershipunov on 30/10/2016.
 * Test suite for QuizRest API
 */
public class QuizRestIT extends QuizRestTestBase {

    @Test
    public void testCleanDB() {

        get().then()
                .statusCode(200)
                .body("size()", is(0));
    }

    @Ignore
    @Test
    public void testCreateAndGetRoot() {

        String title = "Title";
        Map<Long, SubCategory> subCategories = new HashMap<>();

        RootCategoryDTO dto = new RootCategoryDTO("123", title, subCategories);

        get().then().statusCode(200).body("size()", is(0));

        String id = given().contentType(ContentType.JSON)
                .body(dto)
                .post()
                .then()
                .statusCode(200)
                .extract().asString();

        get().then().statusCode(200).body("size()", is(1));

        given().pathParam("id", id)
                .get("/root/{id}")
                .then()
                .statusCode(200)
                .body("id", is(id))
                .body("title", is(title));
    }
}
