package me.contrapost.jee_quiz.ejb;

import me.contrapost.jee_quiz.entity.SpecifyingCategory;
import me.contrapost.jee_quiz.entity.SubCategory;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertNull;
import static junit.framework.TestCase.assertTrue;

/**
 * Created by Alexander Shipunov on 27.10.16.
 * CategoryEJB test suite
 */
@RunWith(Arquillian.class)
public class CategoryEJBTest extends EjbTestBase {

    @Test
    public void testCreateCategory() {
        long subCatId = createSubCategory("Sub", createRootCategory("Root"));
        long specCatId = createSpecifyingCategory("Spec", subCatId);

        assertNotNull(categoryEJB.getCategoryAsRoot(subCatId));
        assertNotNull(categoryEJB.getCategoryAsRoot(specCatId));
        assertTrue(categoryEJB.getCategoryAsRoot(subCatId) instanceof SubCategory);
        assertTrue(categoryEJB.getCategoryAsRoot(specCatId) instanceof SpecifyingCategory);
    }

    @Test
    public void testDeleteRootCategory() {

        long rootCategoryId = createRootCategory("Root");
        categoryEJB.deleteRootCategory(rootCategoryId);

        assertNull(categoryEJB.getCategoryAsRoot(rootCategoryId));
    }

    @Test
    public void testCascadeDeleteOfAllSubCategoriesAndQuizesWhenRootIsDeleted() {
        long rootCategoryId2 = createRootCategory("Root");
        long subCatId = createSubCategory("Sub", rootCategoryId2);
        long specCatId = createSpecifyingCategory("Spec", subCatId);
        long quizId = createQuiz("Quiz", specCatId);

        categoryEJB.deleteRootCategory(rootCategoryId2);

        assertNull(categoryEJB.getCategoryAsRoot(subCatId));
        assertNull(categoryEJB.getCategoryAsRoot(specCatId));
        assertNull(quizEJB.getQuiz(quizId));
    }

    @Test
    public void testDeleteSubCategory() {
        long rootCatId = createRootCategory("Root");
        long subCatId = createSubCategory("Sub", rootCatId);

        assertTrue(categoryEJB.deleteSubCategory(subCatId));
        assertNull(categoryEJB.getCategoryAsRoot(subCatId));
    }

    @Test
    public void testDeleteSpecifyingCategory() {
        long rootCatId = createRootCategory("Root");
        long subCatId = createSubCategory("Sub", rootCatId);
        long specCatId = createSpecifyingCategory("Spec", subCatId);
        long quizId = createQuiz("Quiz", specCatId);

        assertTrue(categoryEJB.deleteSubCategory(specCatId));
        assertNull(categoryEJB.getCategoryAsRoot(specCatId));
        assertNull(quizEJB.getQuiz(quizId));
    }
}
