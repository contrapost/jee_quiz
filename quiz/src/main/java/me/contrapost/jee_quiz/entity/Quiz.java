package me.contrapost.jee_quiz.entity;

import me.contrapost.jee_quiz.validation.AnswerMap;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Map;

/**
 * Created by Alexander Shipunov on 24.10.16.
 * Quiz entity
 */
@NamedQueries(
        @NamedQuery(name = Quiz.GET_ALL_QUIZES, query = "select q from Quiz q")
)
@Entity
public class Quiz {

    public static final String GET_ALL_QUIZES = "GET_ALL_QUIZES";

    @Id
    @GeneratedValue
    private Long id;

    @NotBlank
    @Column(unique = true)
    @Size(max = 100)
    private String question;

    @ManyToOne
    private SpecifyingCategory specifyingCategory;

    @AnswerMap
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

    public SpecifyingCategory getSpecifyingCategory() {
        return specifyingCategory;
    }

    public void setSpecifyingCategory(SpecifyingCategory specifyingCategory) {
        this.specifyingCategory = specifyingCategory;
    }
}
