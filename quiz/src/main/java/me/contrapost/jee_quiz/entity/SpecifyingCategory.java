package me.contrapost.jee_quiz.entity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Alexander Shipunov on 24.10.16.
 * This entity specifies a sub category.
 */
@Entity
public class SpecifyingCategory extends Category {

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
