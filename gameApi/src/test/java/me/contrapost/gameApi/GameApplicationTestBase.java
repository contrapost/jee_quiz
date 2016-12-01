package me.contrapost.gameApi;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.common.ConsoleNotifier;
import io.restassured.RestAssured;
import me.contrapost.gameApi.api.Formats;
import me.contrapost.gameApi.dto.AnswerCheckDTO;
import me.contrapost.gameApi.dto.collection.ListDTO;
import org.junit.*;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static io.restassured.RestAssured.*;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.core.Is.is;

/**
 * Created by alexandershipunov on 21/11/2016.
 *
 */
public class GameApplicationTestBase {

    private static WireMockServer wireMockServer;

    static {
        System.setProperty("quizApiAddress", "localhost:8099");
    }

    @BeforeClass
    public static void initRestAssured() {

        // RestAssured configs shared by all the tests
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8080;
        RestAssured.basePath = "/game/api/games";
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

        wireMockServer = new WireMockServer(
                wireMockConfig().port(8099).notifier(new ConsoleNotifier(true))
        );
        wireMockServer.start();
    }

    @Before
    @After
    public void clean() {

        int total = Integer.MAX_VALUE;

        while (total > 0) {

            ListDTO<?> listDto = given()
                    .queryParam("limit", Integer.MAX_VALUE)
                    .get()
                    .then()
                    .statusCode(200)
                    .extract()
                    .as(ListDTO.class);

            listDto.list.stream()
                    .map(n -> ((Map) n).get("id"))
                    .forEach(id ->
                            given().delete("/" + id)
                                    .then()
                                    .statusCode(204)
                    );

            total = listDto.totalSize - listDto.list.size();
        }
    }

    @AfterClass
    public static void tearDown() {
        wireMockServer.stop();
    }

    @Test
    public void testCreateAndGetGame() throws UnsupportedEncodingException {

        String jsonWithQuizIds = "{\"ids\":[1, 2, 3, 4, 5]}";
        int limit = 5;

        given().contentType(Formats.JSON_V1)
                .get()
                .then()
                .statusCode(200)
                .body("list.size", is(0));

        String id = createGame(jsonWithQuizIds, limit);

        given().contentType(Formats.JSON_V1)
                .get("/" + id)
                .then()
                .statusCode(200)
                .body("id", is(id))
                .body("numberOfQuestions", is(limit));
    }

    @Test
    public void testGetAllActiveGames() throws UnsupportedEncodingException {
        String jsonWithQuizIds = "{\"ids\":[1, 2, 3, 4, 5]}";
        int limit = 5;

        createGame(jsonWithQuizIds, limit);
        createGame(jsonWithQuizIds, limit);
        createGame(jsonWithQuizIds, limit);
        createGame(jsonWithQuizIds, limit);
        createGame(jsonWithQuizIds, limit);

        given().contentType(Formats.JSON_V1)
                .get()
                .then()
                .statusCode(200)
                .body("list.size", is(5));
    }

    @Test
    public void testDeleteAGame() throws UnsupportedEncodingException {
        String jsonWithQuizIds = "{\"ids\":[1, 2, 3, 4, 5]}";
        int limit = 5;

        get().then().statusCode(200).body("list.size", is(0));

        String id = createGame(jsonWithQuizIds, limit);

        given().contentType(Formats.JSON_V1)
                .get("/" + id)
                .then()
                .statusCode(200)
                .body("id", is(id))
                .body("numberOfQuestions", is(limit));

        delete("/" + id);

        get().then().statusCode(200).body("list.size", is(0));
    }

    @Test
    public void testAnswerQuestion() throws UnsupportedEncodingException {
        String jsonWithQuizIds = "{\"ids\":[1, 2, 3, 4, 5]}";
        int limit = 5;

        stubCorrectAnswer();

        String gameId = createGame(jsonWithQuizIds, limit);

        get().then().statusCode(200).body("list.size", is(1));

        AnswerCheckDTO answer = given().contentType(Formats.JSON_V1).queryParam("answer", "correct_answer")
                .post("/" + gameId)
                .then()
                .statusCode(200).extract().as(AnswerCheckDTO.class);

        assertTrue(answer.isCorrect);

        get("/" + gameId).then()
                .statusCode(200)
                .body("id", is(gameId))
                .body("numberOfQuestions", is(limit))
                .body("answersCounter", is(1));

        stubWrongAnswer();

        AnswerCheckDTO answer2 = given().contentType(Formats.JSON_V1).queryParam("answer", "correct_answer")
                .post("/" + gameId)
                .then()
                .statusCode(200).extract().as(AnswerCheckDTO.class);

        assertFalse(answer2.isCorrect);

        get().then().statusCode(200).body("list.size", is(0));
    }

    @Test
    public void testWinGame() throws UnsupportedEncodingException {
        String jsonWithQuizIds = "{\"ids\":[1, 2, 3, 4, 5]}";
        int limit = 5;
        String gameId = createGame(jsonWithQuizIds, limit);

        stubCorrectAnswer();

        get("/" + gameId).then()
                .statusCode(200)
                .body("id", is(gameId))
                .body("numberOfQuestions", is(limit))
                .body("answersCounter", is(0));

        for(int i = 1; i < limit; i++) {
            given().contentType(Formats.JSON_V1).queryParam("answer", "some_answer")
                    .post("/" + gameId)
                    .then()
                    .statusCode(200).extract().as(AnswerCheckDTO.class);
        }

        get("/" + gameId).then()
                .statusCode(200)
                .body("id", is(gameId))
                .body("numberOfQuestions", is(limit))
                .body("answersCounter", is(4));

        given().contentType(Formats.JSON_V1).queryParam("answer", "some_answer")
                .post("/" + gameId)
                .then()
                .statusCode(200).extract().as(AnswerCheckDTO.class);

        get().then().statusCode(200).body("list.size", is(0));
    }

    @Test
    public void testReturnNullIfWaitingTooLong() {
        stubGameWithDelay(2500);

        try {
            given().post()
                    .then()
                    .statusCode(200);
        } catch (Exception ex) {
            assertTrue(ex instanceof NullPointerException);
        }
    }

    private String createGame(String jsonWithQuizIds, int limit) throws UnsupportedEncodingException {

        wireMockServer.stubFor(
                WireMock.post(
                        urlMatching(".*randomQuizzes.*"))
                        .withQueryParam("limit", WireMock.matching("\\d+"))
                        .withQueryParam("filter", WireMock.matching("sp_" + "\\d+"))
                        .willReturn(WireMock.aResponse()
                                .withHeader("Content-Type", "application/json; charset=utf-8")
                                .withHeader("Content-Length", "" + jsonWithQuizIds.getBytes("utf-8").length)
                                .withBody(jsonWithQuizIds)));

        return given().queryParam("limit", limit)
                .post()
                .then()
                .statusCode(200).extract().asString();
    }

    private void stubWrongAnswer() throws UnsupportedEncodingException {
        stubAnswer("{ \"isCorrect\": false}");
    }

    private void stubCorrectAnswer() throws UnsupportedEncodingException {
        stubAnswer("{ \"isCorrect\": true}");
    }

    private void stubAnswer(String answer) throws UnsupportedEncodingException {
        wireMockServer.stubFor(
                WireMock.get(
                        urlMatching(".*quiz/answer-check.*"))
                        .withQueryParam("id", WireMock.matching("\\d+"))
                        .withQueryParam("answer", WireMock.matching(".+"))
                        .willReturn(WireMock.aResponse()
                                .withHeader("Content-Type", "application/json; charset=utf-8")
                                .withHeader("Content-Length", "" + answer.getBytes("utf-8").length)
                                .withBody(answer)));
    }

    @SuppressWarnings("SameParameterValue")
    private void stubGameWithDelay(int delay) {
        WireMock.post(
                urlMatching(".*randomQuizzes.*"))
                .withQueryParam("limit", WireMock.matching("\\d+"))
                .withQueryParam("filter", WireMock.matching("sp_" + "\\d+"))
                .willReturn(WireMock.aResponse()
                        .withFixedDelay(delay));
    }
}
