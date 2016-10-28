package me.contrapost.jee_quiz.ejb;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.TestCase.*;

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

        assertNotNull(categoryEJB.getSubCategory(subCatId));
        assertNotNull(categoryEJB.getSpecifyingCategory(specCatId));
    }

    @Test
    public void testDeleteRootCategory() {

        long rootCategoryId = createRootCategory("Root");
        categoryEJB.deleteRootCategory(rootCategoryId);

        assertNull(categoryEJB.getRootCategory(rootCategoryId));
    }

    @Test
    public void testCascadeDeleteOfAllSubCategoriesAndQuizesWhenRootIsDeleted() {
        long rootCategoryId2 = createRootCategory("Root");
        long subCatId = createSubCategory("Sub", rootCategoryId2);
        long specCatId = createSpecifyingCategory("Spec", subCatId);
        long quizId = createQuiz("Quiz", specCatId);

        categoryEJB.deleteRootCategory(rootCategoryId2);

        assertNull(categoryEJB.getSubCategory(subCatId));
        assertNull(categoryEJB.getSpecifyingCategory(specCatId));
        assertNull(quizEJB.getQuiz(quizId));
    }

    @Test
    public void testDeleteSubCategory() {
        long rootCatId = createRootCategory("Root");
        long subCatId = createSubCategory("Sub", rootCatId);
        long specCatId = createSpecifyingCategory("Spec", subCatId);
        long quizId = createQuiz("Quiz", specCatId);

        assertTrue(categoryEJB.deleteSubCategory(subCatId));
        assertNull(categoryEJB.getSubCategory(subCatId));
        assertNull(categoryEJB.getSpecifyingCategory(specCatId));
        assertNull(quizEJB.getQuiz(quizId));
    }

    @Test
    public void testDeleteSpecifyingCategory() {
        long rootCatId = createRootCategory("Root");
        long subCatId = createSubCategory("Sub", rootCatId);
        long specCatId = createSpecifyingCategory("Spec", subCatId);
        long quizId = createQuiz("Quiz", specCatId);

        assertTrue(categoryEJB.deleteSpecifyingCategory(specCatId));
        assertNull(categoryEJB.getSpecifyingCategory(specCatId));
        assertNull(quizEJB.getQuiz(quizId));
    }
}
