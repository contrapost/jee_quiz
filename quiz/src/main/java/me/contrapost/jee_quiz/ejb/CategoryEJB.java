package me.contrapost.jee_quiz.ejb;

import me.contrapost.jee_quiz.entity.*;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Alexander Shipunov on 24.10.16.
 * CategoryEJB
 */
@SuppressWarnings("unchecked")
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

    public RootCategory getRootCategory(@NotNull long id, boolean expand){
        RootCategory rootCategory = getRootCategory(id);
        if(expand){
            rootCategory.getSubCategories().values()
                    .forEach(subCategory -> subCategory.getSpecifyingCategories().size());
        }
        return rootCategory;
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

    @SuppressWarnings("WeakerAccess")
    public List<Quiz> getAllQuizzesForCategory(long categoryId) {
        Category category = em.find(Category.class, categoryId);
        if (category == null) throw new IllegalArgumentException("There is no category with id: " + categoryId);

        return category.getListOfAllQuizzes();
    }

    public List<Quiz> getAllQuizzesForCategory(long categoryId, int max) {
        Category category = em.find(Category.class, categoryId);
        if (category == null) throw new IllegalArgumentException("There is no category with id: " + categoryId);

        int sizeOfList = category.getListOfAllQuizzes().size();

        return category.getListOfAllQuizzes().subList(0, max > sizeOfList ? sizeOfList : max);
    }

    public List<RootCategory> getAllRootCategories(int limit, boolean expand) {
        List<RootCategory> list =
                em.createNamedQuery(RootCategory.GET_ALL_ROOT_CATEGORIES).setMaxResults(limit).getResultList();

        if(expand) {
            list.forEach(rootCategory -> rootCategory.getSubCategories().size());

            List<SubCategory> subCategories = new ArrayList<>();
            list.forEach(rootCategory -> subCategories.addAll(rootCategory.getSubCategories().values()));

            subCategories.forEach(subCategory -> subCategory.getSpecifyingCategories().size());
        }

        return list;
    }

    @SuppressWarnings("unused")
    public List<SubCategory> getAllSubCategories() {
        return em.createNamedQuery(SubCategory.GET_ALL_SUBCATEGORIES).getResultList();
    }

    public List<SpecifyingCategory> getAllSpecifyingCategories(int limit) {
        return em.createNamedQuery(SpecifyingCategory.GET_ALL_SPECIFYING_CATEGORIES)
                .setMaxResults(limit)
                .getResultList();
    }

    public List<RootCategory> getAllRootCategoriesWithAtLeastOneQuiz(int limit) {

        List<Quiz> quizzes = em.createNamedQuery(Quiz.GET_ALL_QUIZZES).getResultList();
        Set<RootCategory> rootCategories = quizzes.stream().map(Quiz::getSpecifyingCategory).collect(Collectors.toSet())
                .stream().map(SpecifyingCategory::getSubCategory).collect(Collectors.toSet())
                .stream().map(SubCategory::getRootCategory).collect(Collectors.toSet());

        int size = rootCategories.size();

        return new ArrayList<>(rootCategories).subList(0, limit > size ? size : limit);
    }

    public List<SpecifyingCategory> getAllSpecifyingCategoriesWithAtLeastOneQuiz(int limit) {
        return em.createQuery("select s from SpecifyingCategory s where s.quizzes.size > 0")
                .setMaxResults(limit)
                .getResultList();

        /*List<Quiz> quizzes = em.createNamedQuery(Quiz.GET_ALL_QUIZZES).getResultList();
        List<SpecifyingCategory> specifyingCategories =
                quizzes.stream().map(Quiz::getSpecifyingCategory).collect(Collectors.toList());

        int size = specifyingCategories.size();

        return new ArrayList<>(specifyingCategories).subList(0, limit > size ? size : limit);*/
    }

    public List<SubCategory> getAllSubCategoriesForRootCategory(long categoryId, int max) {
        return em.createQuery("select s from SubCategory s where s.rootCategory.id = :id")
                .setParameter("id", categoryId)
                .setMaxResults(max)
                .getResultList();
    }

    public List<SpecifyingCategory> getAllSpecifyingCategoriesForSubCategory(Long id, int limit) {
        return em.createQuery("select sp from SpecifyingCategory sp where sp.subCategory.id = :id")
                .setParameter("id", id)
                .setMaxResults(limit)
                .getResultList();
    }

    public List<Long> getRandomQuizzesFromRootCategory(Long rootCategoryId, int numberOfQuizzes) {
        List<Quiz> quizzes = em.find(RootCategory.class, rootCategoryId).getListOfAllQuizzes();
        List<Long> ids = new ArrayList<>();
        while(ids.size() != numberOfQuizzes && quizzes.size() != 0) {
            ids.add(quizzes.remove(new Random().nextInt(quizzes.size())).getId());
        }
        return ids;
    }

    public List<Long> getRandomQuizzesFromSubCategory(Long subcategoryId, int numberOfQuizzes) {
        List<Quiz> quizzes = em.find(SubCategory.class, subcategoryId).getListOfAllQuizzes();
        List<Long> ids = new ArrayList<>();
        while(ids.size() != numberOfQuizzes && quizzes.size() != 0) {
            ids.add(quizzes.remove(new Random().nextInt(quizzes.size())).getId());
        }
        return ids;
    }

    public List<Long> getRandomQuizzesFromSpecifyingCategory(Long specifyingCategoryId, int numberOfQuizzes) {
        List<Quiz> quizzes = em.find(SpecifyingCategory.class, specifyingCategoryId).getListOfAllQuizzes();
        List<Long> ids = new ArrayList<>();
        while(ids.size() != numberOfQuizzes && quizzes.size() != 0) {
            ids.add(quizzes.remove(new Random().nextInt(quizzes.size())).getId());
        }
        return ids;
    }

    public List<SubCategory> getSubCategoryList(int limit) {
        return em.createNamedQuery(SubCategory.GET_ALL_SUBCATEGORIES).setMaxResults(limit).getResultList();
    }
}
