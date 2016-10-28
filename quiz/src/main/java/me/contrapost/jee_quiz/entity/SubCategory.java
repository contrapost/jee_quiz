package me.contrapost.jee_quiz.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alexander Shipunov on 24.10.16.
 * Subcategory of a root category
 */
@Entity
public class SubCategory {

    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    @Size(max = 100)
    private String title;

    @ManyToOne
    private RootCategory rootCategory;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "subCategory")
    private List<SpecifyingCategory> specifyingCategories;

    public SubCategory() {
    }

    public RootCategory getRootCategory() {
        return rootCategory;
    }

    public void setRootCategory(RootCategory rootCategory) {
        this.rootCategory = rootCategory;
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

    public List<SpecifyingCategory> getSpecifyingCategories() {
        if (specifyingCategories == null) specifyingCategories = new ArrayList<>();
        return specifyingCategories;
    }

    public void setSpecifyingCategories(List<SpecifyingCategory> specifyingCategories) {
        this.specifyingCategories = specifyingCategories;
    }
}
