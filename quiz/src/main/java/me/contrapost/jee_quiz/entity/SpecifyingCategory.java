package me.contrapost.jee_quiz.entity;

import javax.mail.MethodNotSupportedException;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
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
        if(quizes == null) quizes = new ArrayList<>();
        return quizes;
    }

    public void setQuizes(List<Quiz> quizes) {
        this.quizes = quizes;
    }

    @Override
    public List<SubCategory> getSubCategories() {
        return null;
    }

    @Override
    public void setSubCategories(List<SubCategory> subCategories) {
        try {
            throw new MethodNotSupportedException("It's not possible to add subcaregory to the Specifying category");
        } catch (MethodNotSupportedException e) {
            e.printStackTrace();
        }
    }
}
