package me.contrapost.jee_quiz.entity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.List;

/**
 * Created by Alexander Shipunov on 24.10.16.
 * This entity specifies a sub category.
 */
@Entity
public class SpecifyingCategory extends SubCategory {

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Quiz> quizes;

    public SpecifyingCategory() {
    }

    public List<Quiz> getQuizes() {
        return quizes;
    }

    public void setQuizes(List<Quiz> quizes) {
        this.quizes = quizes;
    }
}
