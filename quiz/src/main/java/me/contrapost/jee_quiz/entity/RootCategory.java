package me.contrapost.jee_quiz.entity;

import org.hibernate.annotations.NamedQueries;
import org.hibernate.annotations.NamedQuery;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Alexander Shipunov on 24.10.16.
 * Root category of the quiz
 */
@NamedQueries(
        @NamedQuery(name = RootCategory.GET_ALL_ROOT_CATEGORIES, query = "select r from RootCategory r")
)
@Entity
public class RootCategory extends Category{

    public static final String GET_ALL_ROOT_CATEGORIES = "GET_ALL_ROOT_CATEGORIES";

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "rootCategory")
    private Map<Long, SubCategory> subCategories;

    public RootCategory() {
    }

    public Map<Long, SubCategory> getSubCategories() {
        if(subCategories == null) subCategories = new HashMap<>();
        return subCategories;
    }

    public void setSubCategories(Map<Long, SubCategory> subCategories) {
        this.subCategories = subCategories;
    }

    @Override
    public List<Quiz> getListOfAllQuizes() {
        List<SpecifyingCategory> specifyingCategories = new ArrayList<>();
        for(SubCategory sc : getSubCategories().values()) {
            specifyingCategories = Stream.concat(sc.getSpecifyingCategories().values().stream(),
                    specifyingCategories.stream()).collect(Collectors.toList());
        }
        List<Quiz> quizes = new ArrayList<>();
        for (SpecifyingCategory sc : specifyingCategories) {
            quizes = Stream.concat(quizes.stream(), sc.getQuizes().values().stream()).collect(Collectors.toList());
        }
        return quizes;
    }
}
