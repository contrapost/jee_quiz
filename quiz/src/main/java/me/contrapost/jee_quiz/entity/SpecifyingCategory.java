package me.contrapost.jee_quiz.entity;

import org.hibernate.annotations.NamedQueries;
import org.hibernate.annotations.NamedQuery;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Alexander Shipunov on 24.10.16.
 * This entity specifies a sub category.
 */
@NamedQueries(
        @NamedQuery(name = SpecifyingCategory.GET_ALL_SPECIFYING_CATEGORIES, query = "select s from SpecifyingCategory s")
)
@Entity
public class SpecifyingCategory extends Category {

    public static final String GET_ALL_SPECIFYING_CATEGORIES = "GET_ALL_SPECIFYING_CATEGORIES";

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Map<Long, Quiz> quizzes;

    @ManyToOne
    private SubCategory subCategory;

    public SpecifyingCategory() {
    }

    public Map<Long, Quiz> getQuizzes() {
        if(quizzes == null) quizzes = new HashMap<>();
        return quizzes;
    }

    public void setQuizzes(Map<Long, Quiz> quizzes) {
        this.quizzes = quizzes;
    }

    public SubCategory getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(SubCategory subCategory) {
        this.subCategory = subCategory;
    }

    @Override
    public List<Quiz> getListOfAllQuizzes() {
        return new ArrayList<>(getQuizzes().values());
    }
}
