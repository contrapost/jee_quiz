package me.contrapost.gameApi.dto;

import me.contrapost.gameApi.api.URIs;
import me.contrapost.gameApi.dto.collection.ListDTO;
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

    public static ListDTO<GameDTO> transform(List<GameEntity> games, int offset,
                                             int limit){
        List<GameDTO> dtoList = null;
        if(games != null){
            dtoList = games.stream()
                    .skip(offset) // this is a good example of how streams simplify coding
                    .limit(limit)
                    .map(GameConverter::transform)
                    .collect(Collectors.toList());
        }

        ListDTO<GameDTO> dto = new ListDTO<>();
        dto.list = dtoList;
        dto._links = new ListDTO.ListLinks();
        dto.rangeMin = offset;
        assert dtoList != null;
        dto.rangeMax = dto.rangeMin + dtoList.size() - 1;
        dto.totalSize = games.size();

        return dto;
    }
}
