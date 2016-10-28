package me.contrapost.jee_quiz.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Alexander Shipunov on 24.10.16.
 * Root category of the quiz
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class RootCategory {

    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    @Size(max = 100)
    private String title;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "rootCategory")
    private Map<Long, SubCategory> subCategories;

    public RootCategory() {
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

    public Map<Long, SubCategory> getSubCategories() {
        if(subCategories == null) subCategories = new HashMap<>();
        return subCategories;
    }

    public void setSubCategories(Map<Long, SubCategory> subCategories) {
        this.subCategories = subCategories;
    }
}
