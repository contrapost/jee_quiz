package me.contrapost.jee_quiz.ejb;

import me.contrapost.jee_quiz.entity.RootCategory;
import me.contrapost.jee_quiz.entity.SpecifyingCategory;
import me.contrapost.jee_quiz.entity.SubCategory;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.constraints.NotNull;

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
        rootCategory.getSubCategories().add(subCategory);

        em.persist(rootCategory);

        return subCategory.getId();
    }

    public long createSpecifyingCategory(@NotNull String title, @NotNull long subCategoryId) {
        SubCategory subCategory = em.find(SubCategory.class, subCategoryId);
        if (subCategory == null) throw new IllegalArgumentException("No such subcategory category: " + subCategoryId);

        SpecifyingCategory specCategory = new SpecifyingCategory();
        specCategory.setTitle(title);
        specCategory.setSubCategory(subCategory);
        subCategory.getSpecifyingCategories().add(specCategory);

        em.persist(subCategory);

        return specCategory.getId();
    }

//    public List<Quiz> getAllQuizesForCategory(@NotNull Long categoryId) {
//        RootCategory rootCategory = em.find(RootCategory.class, categoryId);
//        if (rootCategory == null) throw new IllegalArgumentException("No such category: " + categoryId);
//
//        if (rootCategory.getSubCategories() == null) { // rootCategory is a specifying category
//            return ((SpecifyingCategory) rootCategory).getQuizes();
//        } else {  // category can be both root or sub-category
//            if (rootCategory.getSubCategories().size() == 0) { // category doesn't have subcategories and therefore quizes
//                return null;
//            } else { // category is root or sub and has subcategories
//                List<Quiz> quizes = new ArrayList<>();
//                for (RootCategory r : rootCategory.getSubCategories()) { // iterate through all subcategories
//                    if (r.getSubCategories() == null) { // rootCategory is subcategory
//                        quizes = Stream.concat(quizes.stream(), ((SpecifyingCategory) r).getQuizes().stream())
//                                .collect(Collectors.toList());
//                    } else { // rootCategory is root
//                        for (RootCategory subRoot : r.getSubCategories()) {
//                            if (subRoot.getSubCategories().size() != 0) {
//                                for (RootCategory subsubRoot : subRoot.getSubCategories()) {
//                                    quizes = Stream.concat(quizes.stream(), ((SpecifyingCategory) subsubRoot).getQuizes().stream())
//                                            .collect(Collectors.toList());
//                                }
//                            }
//                        }
//                    }
//                }
//                return quizes;
//            }
//        }
//    }

    public RootCategory getRootCategory(long id){
        return em.find(RootCategory.class, id);
    }

    public SubCategory getSubCategory(long id){
        return em.find(SubCategory.class, id);
    }

    public SpecifyingCategory getSpecifyingCategory(long id){
        return em.find(SpecifyingCategory.class, id);
    }

    public boolean deleteRootCategory(long id) {
        if (em.find(RootCategory.class, id) == null) return false;
        em.remove(em.find(RootCategory.class, id));
        return true;
    }

    public boolean deleteSubCategory(long id){
        SubCategory subCategory = em.find(SubCategory.class, id);
        if (subCategory == null) return false; // Or cast exception
        RootCategory rootCategory = em.find(RootCategory.class, subCategory.getRootCategory().getId());
        rootCategory.getSubCategories().remove(subCategory);
//        em.persist(rootCategory);
        return true;
    }

    public boolean deleteSpecifyingCategory(long id){
        SpecifyingCategory specifyingCategory = em.find(SpecifyingCategory.class, id);
        if (specifyingCategory == null) return false; // Or cast exception
        SubCategory subCategory = em.find(SubCategory.class, specifyingCategory.getSubCategory().getId());
        subCategory.getSpecifyingCategories().remove(specifyingCategory);
//        em.persist(rootCategory);
        return true;
    }
}
