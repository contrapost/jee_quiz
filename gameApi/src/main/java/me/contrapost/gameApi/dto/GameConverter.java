package me.contrapost.gameApi.dto;

import me.contrapost.gameApi.api.URIs;
import me.contrapost.gameApi.entity.GameEntity;

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

        GameDTO dto = new GameDTO();
        dto.id = String.valueOf(entity.getId());

        dto.answersCounter = entity.getAnswersCounter();
        dto.numberOfQuestions = entity.getQuizzesIds().size();
        dto.isActive = entity.isActive();
        if(!entity.isActive()){
            dto.currentQuizURI = "not applicable";
        } else {
            dto.currentQuizURI = URIs.QUIZ_ROOT_URI + entity.getQuizzesIds().get(entity.getAnswersCounter());
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
