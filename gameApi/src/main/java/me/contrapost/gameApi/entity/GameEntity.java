package me.contrapost.gameApi.entity;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by alexandershipunov on 19/11/2016.
 *
 */
@Entity
@NamedQueries({
        @NamedQuery(name = GameEntity.GET_ALL_ACTIVE_GAMES, query = "select g from GameEntity g where g.isActive = true"),
})
public class GameEntity {

    public static final String GET_ALL_ACTIVE_GAMES = "GET_ALL_ACTIVE_GAMES";

    @Id
    @GeneratedValue
    private Long id;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<Long> quizzesIds;

    private int answersCounter;

    private boolean isActive;

    public GameEntity() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Long> getQuizzesIds() {
        if(quizzesIds == null) quizzesIds = new ArrayList<>();
        return quizzesIds;
    }

    public void setQuizzesIds(List<Long> quizzes) {
        this.quizzesIds = quizzes;
    }

    public int getAnswersCounter() {
        return answersCounter;
    }

    @SuppressWarnings("unused")
    public void setAnswersCounter(int answersCounter) {
        this.answersCounter = answersCounter;
    }

    public boolean isActive() {
        return quizzesIds.size() == answersCounter;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
