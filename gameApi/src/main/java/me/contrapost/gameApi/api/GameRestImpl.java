package me.contrapost.gameApi.api;

import io.swagger.annotations.ApiParam;
import me.contrapost.gameApi.dto.AnswerCheckDTO;
import me.contrapost.gameApi.dto.GameConverter;
import me.contrapost.gameApi.dto.GameDTO;
import me.contrapost.gameApi.dto.IdsDTO;
import me.contrapost.gameApi.dto.collection.ListDTO;
import me.contrapost.gameApi.dto.hal.HalLink;
import me.contrapost.gameApi.entity.GameEntity;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.List;

/**
 * Created by alexandershipunov on 19/11/2016.
 * Implementation of game API
 */
public class GameRestImpl implements GameRestApi {

    @Context
    UriInfo uriInfo;

    private final String quizApiWebAddress;

    private EntityManagerFactory factory = Persistence.createEntityManagerFactory("GAME_DB");

    private EntityManager em = factory.createEntityManager();

    public GameRestImpl() {

        quizApiWebAddress = System.getProperty("quizApiAddress", URIs.QUIZ_ROOT_URI);
    }

    @Override
    public synchronized ListDTO<GameDTO> getAllActiveGames(Integer offset, Integer limit) {

        if(offset < 0){
            throw new WebApplicationException("Negative offset: "+offset, 400);
        }

        if(limit < 1){
            throw new WebApplicationException("Limit should be at least 1: "+limit, 400);
        }

        int maxFromDb = 50;

        @SuppressWarnings("unchecked")
        List<GameEntity> list = em.createNamedQuery(GameEntity.GET_ALL_ACTIVE_GAMES)
                .setMaxResults(maxFromDb)
                .getResultList();

        if(offset != 0 && offset >=  list.size()){
            throw new WebApplicationException("Offset "+ offset + " out of bound "+ list.size(), 400);
        }

        ListDTO<GameDTO> listDTO = GameConverter.transform(list, offset, limit);

        UriBuilder builder = uriInfo.getBaseUriBuilder()
                .path("/quiz/subcategories")
                .queryParam("limit", limit);

        listDTO._links.self = new HalLink(builder.clone()
                .queryParam("offset", offset)
                .build().toString()
        );

        if (!list.isEmpty() && offset > 0) {
            listDTO._links.previous = new HalLink(builder.clone()
                    .queryParam("offset", Math.max(offset - limit, 0))
                    .build().toString()
            );
        }
        if (offset + limit < list.size()) {
            listDTO._links.next = new HalLink(builder.clone()
                    .queryParam("offset", offset + limit)
                    .build().toString()
            );
        }

        return listDTO;
    }

    @SuppressWarnings("Duplicates")
    @Override
    public synchronized Response createGame(
            @ApiParam("Optional parameter specifying number of quizzes in the game. " +
                    "Default value is 5 if absent")
                    String limit) {

        long specifyingCategoryId = 1; //TODO

        URI specCategoryURI = UriBuilder
                .fromUri("http://" + quizApiWebAddress + "/quiz/randomQuizzes?limit=" +
                        limit  +"&filter=sp_" + specifyingCategoryId)
                .build();

        Client client = ClientBuilder.newClient();
        Response response = client.target(specCategoryURI).request("application/json").post(null);


        IdsDTO result = response.readEntity(IdsDTO.class);

        List<Long> quizzesIds = result.ids;

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

        GameEntity ge = em.find(GameEntity.class, id);

        Long currentQuizId = ge.getQuizzesIds().get(ge.getAnswersCounter());

        URI uri = UriBuilder
                .fromUri("http://" + quizApiWebAddress + "/quiz/answer-check/?id=" + currentQuizId + "&answer=" + answer)
                .build();

        Client client = ClientBuilder.newClient();
        Response response = client.target(uri).request("application/json").get();

        AnswerCheckDTO result = response.readEntity(AnswerCheckDTO.class);

        updateGameStatus(id, result.isCorrect);

        return result;
    }

    private void updateGameStatus(Long id, boolean isCorrect) {
        GameEntity ge = em.find(GameEntity.class, id);
        em.getTransaction().begin();
        if(isCorrect){
            ge.setAnswersCounter(ge.getAnswersCounter() + 1);
        } else {
            em.remove(ge);
        }

        if(!ge.isActive()) em.remove(ge);

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
