package me.contrapost.jee_quiz.entity;

import org.hibernate.annotations.*;
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
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private Map<Long, Quiz> quizes;

    @ManyToOne
    private SubCategory subCategory;

    public SpecifyingCategory() {
    }

    public Map<Long, Quiz> getQuizes() {
        if(quizes == null) quizes = new HashMap<>();
        return quizes;
    }

    public void setQuizes(Map<Long, Quiz> quizes) {
        this.quizes = quizes;
    }

    public SubCategory getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(SubCategory subCategory) {
        this.subCategory = subCategory;
    }

    @Override
    public List<Quiz> getListOfAllQuizes() {
        return new ArrayList<>(getQuizes().values());
    }
}
