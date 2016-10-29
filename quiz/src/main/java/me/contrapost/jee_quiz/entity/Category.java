package me.contrapost.jee_quiz.entity;

import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * Created by alexandershipunov on 28/10/2016.
 * abstract class for all categories
 */
@Entity
public abstract class Category {

    @Id
    @GeneratedValue
    private Long id;

    @NotBlank
    @Column(unique = true)
    @Size(max = 100)
    private String title;

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

    public abstract List<Quiz> getListOfAllQuizes();
}
