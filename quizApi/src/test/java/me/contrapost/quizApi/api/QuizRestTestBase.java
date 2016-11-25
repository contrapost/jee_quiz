package me.contrapost.quizApi.api;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import me.contrapost.quizApi.api.util.JBossUtil;
import me.contrapost.quizApi.dto.RootCategoryDTO;
import me.contrapost.quizApi.dto.collection.ListDTO;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static org.hamcrest.core.Is.is;

/**
 * Created by alexandershipunov on 30/10/2016.
 * Base for integration tests
 */
public class QuizRestTestBase {
    @BeforeClass
    public static void initClass() {
        JBossUtil.waitForJBoss(10);

        // RestAssured configs shared by all the tests
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8080;
        RestAssured.basePath = "/quiz/api/quiz";
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }


    @Before
    @After
    public void clean() {

        /*
           Recall, as Wildfly is running as a separated process, changed
           in the database will impact all the tests.
           Here, we read each resource (GET), and then delete them
           one by one (DELETE)
         */
        List<RootCategoryDTO> list = Arrays.asList(given().accept(ContentType.JSON).get("/categories")
                .then()
                .statusCode(200)
                .extract().as(RootCategoryDTO[].class));


        /*
            Code 204: "No Content". The server has successfully processed the request,
            but the return HTTP response will have no body.
         */
        list.forEach(dto ->
                given().pathParam("id", dto.id).delete("categories/id/{id}").then().statusCode(204));

        get("/categories").then().statusCode(200).body("size()", is(0));
    }
}
