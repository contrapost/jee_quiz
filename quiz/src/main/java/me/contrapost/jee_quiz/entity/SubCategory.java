package me.contrapost.jee_quiz.entity;

import javax.persistence.*;
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
@Entity
public class SubCategory extends Category{

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

    public void setSpecifyingCategories(Map<Long, SpecifyingCategory> specifyingCategories) {
        this.specifyingCategories = specifyingCategories;
    }

    @Override
    public List<Quiz> getListOfAllQuizes() {
        List<Quiz> quizes = new ArrayList<>();
        for(SpecifyingCategory sc : getSpecifyingCategories().values()) {
            quizes = Stream.concat(quizes.stream(), sc.getQuizes().values().stream()).collect(Collectors.toList());
        }
        return quizes;
    }
}
