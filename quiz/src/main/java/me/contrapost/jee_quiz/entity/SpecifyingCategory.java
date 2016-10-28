package me.contrapost.jee_quiz.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Alexander Shipunov on 24.10.16.
 * This entity specifies a sub category.
 */
@Entity
public class SpecifyingCategory {


    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    @Size(max = 100)
    private String title;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public SubCategory getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(SubCategory subCategory) {
        this.subCategory = subCategory;
    }
}
