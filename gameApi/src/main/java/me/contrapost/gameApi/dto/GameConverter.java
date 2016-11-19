package me.contrapost.gameApi.dto;

import me.contrapost.gameApi.db.GameEntity;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by alexandershipunov on 19/11/2016.
 * Game converter
 */
public class GameConverter {

    private GameConverter(){}

    public static GameDTO transform(GameEntity entity){
        Objects.requireNonNull(entity);

        String uri = "URI TO QUIZ"; // TODO

        GameDTO dto = new GameDTO();
        dto.id = String.valueOf(entity.getId());

        dto.answersCounter = entity.getAnswersCounter();
        dto.numberOfQuestions = entity.getAnswersCounter() + entity.getQuizzes().size();
        if(entity.getQuizzes().isEmpty()){
            dto.currentQuizURI = "not applicable";
        } else {
            dto.currentQuizURI = uri + entity.getQuizzes().get(0).getId();
        }


        return dto;
    }

    public static List<GameDTO> transform(List<GameEntity> entities){
        Objects.requireNonNull(entities);

        return entities.stream()
                .map(GameConverter::transform)
                .collect(Collectors.toList());
    }
}
