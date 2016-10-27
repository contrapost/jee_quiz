package me.contrapost.jee_quiz.entity;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * Created by Alexander Shipunov on 24.10.16.
 * Subcategory of a root category
 */
@Entity
public class SubCategory extends RootCategory{

    @ManyToOne
    private RootCategory rootCategory;

    public SubCategory() {
    }

    public RootCategory getRootCategory() {
        return rootCategory;
    }

    public void setRootCategory(RootCategory rootCategory) {
        this.rootCategory = rootCategory;
    }
}
