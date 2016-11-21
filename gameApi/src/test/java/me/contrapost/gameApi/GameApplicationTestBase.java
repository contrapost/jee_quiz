package me.contrapost.gameApi;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.common.ConsoleNotifier;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import me.contrapost.gameApi.api.Formats;
import me.contrapost.gameApi.dto.GameDTO;
import org.junit.*;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static org.hamcrest.core.Is.is;

/**
 * Created by alexandershipunov on 21/11/2016.
 *
 */
public class GameApplicationTestBase {

    private static WireMockServer wireMockServer;

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

        List<GameDTO> list = Arrays.asList(given().accept(ContentType.JSON).get()
                .then()
                .statusCode(200)
                .extract().as(GameDTO[].class));

        list.forEach(dto ->
                given().pathParam("id", dto.id)
                        .delete("/{id}")
                        .then().statusCode(204));

        get().then().statusCode(200).body("size()", is(0));
    }

    @AfterClass
    public static void tearDown() {
        wireMockServer.stop();
    }

    @Test
    public void testCreateAndGetGame() throws UnsupportedEncodingException {
        String json = "[1, 2, 3, 4, 5]";

        wireMockServer.stubFor( //prepare a stubbed response for the given request
                WireMock.post(
                        urlMatching("/randomQuizzes"))
                        .withQueryParam("limit", WireMock.matching("\\D+"))
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
                .body("size", is(0));


        /*given().queryParam("limit", 5)
                .post()
                .then()
                .statusCode(200);*/
    }

    @Test
    public void testGetAllActiveGames() {
        given().contentType(Formats.JSON_V1)
                .get()
                .then()
                .statusCode(200)
                .body("size", is(0));
    }
}
