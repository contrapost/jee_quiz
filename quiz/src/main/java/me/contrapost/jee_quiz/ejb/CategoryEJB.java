package me.contrapost.jee_quiz.ejb;

import me.contrapost.jee_quiz.entity.*;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created by Alexander Shipunov on 24.10.16.
 * CategoryEJB
 */
@Stateless
public class CategoryEJB {

    @PersistenceContext
    protected EntityManager em;

    public long createRootCategory(@NotNull String title) {
        RootCategory rootCategory = new RootCategory();
        rootCategory.setTitle(title);

        em.persist(rootCategory);

        return rootCategory.getId();
    }

    public long createSubCategory(@NotNull String title, @NotNull long rootCategoryId) {
        RootCategory rootCategory = em.find(RootCategory.class, rootCategoryId);
        if (rootCategory == null) throw new IllegalArgumentException("No such root category: " + rootCategoryId);

        SubCategory subCategory = new SubCategory();
        subCategory.setTitle(title);
        subCategory.setRootCategory(rootCategory);

        em.persist(subCategory);

        rootCategory.getSubCategories().put(subCategory.getId(), subCategory);

        return subCategory.getId();
    }

    public long createSpecifyingCategory(@NotNull String title, @NotNull long subCategoryId) {
        SubCategory subCategory = em.find(SubCategory.class, subCategoryId);
        if (subCategory == null) throw new IllegalArgumentException("No such subcategory category: " + subCategoryId);

        SpecifyingCategory specCategory = new SpecifyingCategory();
        specCategory.setTitle(title);
        specCategory.setSubCategory(subCategory);

        em.persist(specCategory);

        subCategory.getSpecifyingCategories().put(specCategory.getId(), specCategory);

        return specCategory.getId();
    }

    public RootCategory getRootCategory(@NotNull long id){
        return em.find(RootCategory.class, id);
    }

    public SubCategory getSubCategory(@NotNull long id){
        return em.find(SubCategory.class, id);
    }

    public SpecifyingCategory getSpecifyingCategory(@NotNull long id){
        return em.find(SpecifyingCategory.class, id);
    }

    public boolean deleteRootCategory(@NotNull long id) {
        if (em.find(RootCategory.class, id) == null) return false;
        em.remove(em.find(RootCategory.class, id));
        return true;
    }

    public boolean deleteSubCategory(@NotNull long id){
        SubCategory subCategory = em.find(SubCategory.class, id);
        if (subCategory == null) return false; // Or cast exception
        RootCategory rootCategory = em.find(RootCategory.class, subCategory.getRootCategory().getId());
        rootCategory.getSubCategories().remove(id);
        return true;
    }

    public boolean deleteSpecifyingCategory(@NotNull long id){
        SpecifyingCategory specifyingCategory = em.find(SpecifyingCategory.class, id);
        if (specifyingCategory == null) return false; // Or cast exception
        SubCategory subCategory = em.find(SubCategory.class, specifyingCategory.getSubCategory().getId());
        subCategory.getSpecifyingCategories().remove(id);
        return true;
    }

    public boolean updateCategoryTitle(@NotNull long categoryId, @NotNull String newTitle) {
        Category category = em.find(Category.class, categoryId);
        if (category == null) return false;
        category.setTitle(newTitle);
        return true;
    }

    public List<Quiz> getAllQuizesForCategory(long categoryId) {
        Category category = em.find(Category.class, categoryId);
        if (category == null) throw new IllegalArgumentException("There is no category with id: " + categoryId);

        return category.getListOfAllQuizes();
    }

    public List<RootCategory> getAllRootCategories() {
        return em.createNamedQuery(RootCategory.GET_ALL_ROOT_CATEGORIES).getResultList();
    }

    public List<SubCategory> getAllSubCategories() {
        return em.createNamedQuery(SubCategory.GET_ALL_SUBCATEGORIES).getResultList();
    }
}
