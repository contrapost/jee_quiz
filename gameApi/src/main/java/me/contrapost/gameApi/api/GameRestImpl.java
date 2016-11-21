package me.contrapost.gameApi.api;

import io.swagger.annotations.ApiParam;
import me.contrapost.gameApi.dto.AnswerCheckDTO;
import me.contrapost.gameApi.dto.GameConverter;
import me.contrapost.gameApi.dto.GameDTO;
import me.contrapost.gameApi.entity.GameEntity;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by alexandershipunov on 19/11/2016.
 * Implementation of game API
 */
public class GameRestImpl implements GameRestApi {

    private final String webAddress;

    private EntityManagerFactory factory = Persistence.createEntityManagerFactory("GAME_DB");

    private EntityManager em = factory.createEntityManager();

    public GameRestImpl() {

        /*
             Important that we use a system property (which can be set with
             -D from command line).
             Reason? we ll change the server when running tests, as to be
             able to mock responses deterministically
         */
        webAddress = System.getProperty("quizApiAddress", URIs.QUIZ_ROOT_URI);
    }

    @Override
    public synchronized List<GameDTO> getAllActiveGames() {
        //noinspection unchecked
        return GameConverter.transform(em.createNamedQuery(GameEntity.GET_ALL_ACTIVE_GAMES).getResultList());
    }

    @SuppressWarnings("Duplicates")
    @Override
    public synchronized Response createGame(
            @ApiParam("Optional parameter specifying number of quizzes in the game. " +
                    "Default value is 5 if absent")
                    String limit) {

        long specifyingCategoryId = 1; //TODO

        URI specCategoryURI = UriBuilder
                .fromUri(webAddress + "/randomQuizzes")
                .queryParam("limit", limit)
                .queryParam("filter", "sp_" + specifyingCategoryId)
                .build();

        Client client = ClientBuilder.newClient();
        Response response = client.target(specCategoryURI).request("application/json").get();

        String[] result = response.readEntity(String[].class);

        List<Long> quizzesIds = new ArrayList<>();
        for (String aResultAsString : result) {
            quizzesIds.add(Long.parseLong(aResultAsString));
        }

        GameEntity ge = new GameEntity();
        ge.setQuizzesIds(quizzesIds);
        ge.setActive(true);

        em.getTransaction().begin();
        em.persist(ge);
        em.getTransaction().commit();

        return Response.status(200)
                .entity(ge.getId())
                .location(UriBuilder
                        .fromUri("games/" + ge.getId())
                        .build())
                .build();
    }

    @Override
    public synchronized GameDTO getGameById(@ApiParam("Unique id of the game") Long id) {
        return GameConverter.transform(em.find(GameEntity.class, id));
    }

    @Override
    public synchronized AnswerCheckDTO answerQuiz(@ApiParam("Unique id of the game") Long id,
                                                  @ApiParam("Answer")
                                                          String answer) {

        URI uri = UriBuilder
                .fromUri(webAddress + "/answer-check")
                .queryParam("id", id)
                .queryParam("answer", answer)
                .build();

        Client client = ClientBuilder.newClient();
        Response response = client.target(uri).request("application/json").get();

        String result = response.readEntity(String.class);

        updateGameStatus(id, Boolean.parseBoolean(result));

        return new AnswerCheckDTO(result);
    }

    private void updateGameStatus(Long id, boolean isCorrect) {
        GameEntity ge = em.find(GameEntity.class, id);
        em.getTransaction().begin();
        if(isCorrect){
            ge.setAnswersCounter(ge.getAnswersCounter() + 1);
            ge.isActive();
        } else {
            em.remove(ge);
        }
        em.getTransaction().commit();
    }

    @Override
    public synchronized void quitGame(@ApiParam("Unique id of the game")
                                        Long id) {

        GameEntity ge = em.find(GameEntity.class, id);
        em.getTransaction().begin();
        em.remove(ge);
        em.getTransaction().commit();
    }
}
