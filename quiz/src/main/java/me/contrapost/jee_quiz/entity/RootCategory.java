package me.contrapost.jee_quiz.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
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
@Entity
public class RootCategory extends Category{

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
