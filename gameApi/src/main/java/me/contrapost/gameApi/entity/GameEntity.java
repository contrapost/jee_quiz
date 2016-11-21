package me.contrapost.gameApi.entity;

import me.contrapost.jee_quiz.entity.Quiz;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by alexandershipunov on 19/11/2016.
 *
 */
@Entity
public class GameEntity {

    @Id
    @GeneratedValue
    private Long id;

    @OneToMany(fetch = FetchType.EAGER)
    private List<Quiz> quizzes;

    private int answersCounter;

    private boolean isActive;

    public GameEntity() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Quiz> getQuizzes() {
        if(quizzes == null) quizzes = new ArrayList<>();
        return quizzes;
    }

    public void setQuizzes(List<Quiz> quizzes) {
        this.quizzes = quizzes;
    }

    public int getAnswersCounter() {
        return answersCounter;
    }

    public void setAnswersCounter(int answersCounter) {
        this.answersCounter = answersCounter;
    }

    public boolean isActive() {
        setActive(quizzes.size() == answersCounter);
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
