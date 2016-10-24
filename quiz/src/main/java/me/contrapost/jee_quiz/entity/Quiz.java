package me.contrapost.jee_quiz.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Map;

/**
 * Created by Alexander Shipunov on 24.10.16.
 * Quiz entity
 */
@Entity
public class Quiz {

    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    @Size(max = 100)
    private String question;

    @ElementCollection(fetch = FetchType.EAGER)
    private Map<String, Boolean> answerMap;

    public Quiz() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public Map<String, Boolean> getAnswerMap() {
        return answerMap;
    }

    public void setAnswerMap(Map<String, Boolean> answerMap) {
        this.answerMap = answerMap;
    }
}
