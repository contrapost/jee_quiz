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
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Alexander Shipunov on 24.10.16.
 * Subcategory of a root category
 */
@NamedQueries(
    @NamedQuery(name = SubCategory.GET_ALL_SUBCATEGORIES, query = "select s from SubCategory s")
)
@Entity
public class SubCategory extends Category{

    public static final String GET_ALL_SUBCATEGORIES = "GET_ALL_SUBCATEGORIES";
    @ManyToOne
    private RootCategory rootCategory;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "subCategory")
    private Map<Long, SpecifyingCategory> specifyingCategories;

    public SubCategory() {
    }

    public RootCategory getRootCategory() {
        return rootCategory;
    }

    public void setRootCategory(RootCategory rootCategory) {
        this.rootCategory = rootCategory;
    }

    public Map<Long, SpecifyingCategory> getSpecifyingCategories() {
        if (specifyingCategories == null) specifyingCategories = new HashMap<>();
        return specifyingCategories;
    }

    @SuppressWarnings("unused")
    public void setSpecifyingCategories(Map<Long, SpecifyingCategory> specifyingCategories) {
        this.specifyingCategories = specifyingCategories;
    }

    @Override
    public List<Quiz> getListOfAllQuizzes() {
        List<Quiz> quizzes = new ArrayList<>();
        for(SpecifyingCategory sc : getSpecifyingCategories().values()) {
            quizzes = Stream.concat(quizzes.stream(), sc.getQuizzes().values().stream()).collect(Collectors.toList());
        }
        return quizzes;
    }
}
