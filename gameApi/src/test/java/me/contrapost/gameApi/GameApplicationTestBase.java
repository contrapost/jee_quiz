package me.contrapost.gameApi;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.common.ConsoleNotifier;
import io.restassured.RestAssured;
import me.contrapost.gameApi.api.Formats;
import me.contrapost.gameApi.dto.collection.ListDTO;
import org.junit.*;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static io.restassured.RestAssured.given;
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

        /*
            as the REST API does not return the whole state of the database (even,
            if I use an infinite "limit") I need to keep doing queries until the totalSize is 0
         */

        while (total > 0) {

            //seems there are some limitations when handling generics
            ListDTO<?> listDto = given()
                    .queryParam("limit", Integer.MAX_VALUE)
                    .get()
                    .then()
                    .statusCode(200)
                    .extract()
                    .as(ListDTO.class);

            listDto.list.stream()
                    //the "NewsDto" get unmarshalled into a map of fields
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

        String json = "{\"ids\":[1, 2, 3, 4, 5]}";

        wireMockServer.stubFor( //prepare a stubbed response for the given request
                WireMock.post(
                        urlMatching(".*quiz/randomQuizzes.*"))
                        .withQueryParam("limit", WireMock.matching("5"))
                        .withQueryParam("filter", WireMock.matching("sp_1"))
                        // define the mocked response of the GET
                        .willReturn(WireMock.aResponse()
                                .withHeader("Content-Type", "application/json; charset=utf-8")
                                .withHeader("Content-Length", "" + json.getBytes("utf-8").length)
                                .withBody(json)));

        given().contentType(Formats.JSON_V1)
                .get()
                .then()
                .statusCode(200)
                .body("list.size", is(0));

        given().queryParam("limit", 5)
                .post()
                .then()
                .statusCode(200);
    }

    @Test
    public void testGetAllActiveGames() {
        given().contentType(Formats.JSON_V1)
                .get()
                .then()
                .statusCode(200)
                .body("list.size", is(0));
    }
}
