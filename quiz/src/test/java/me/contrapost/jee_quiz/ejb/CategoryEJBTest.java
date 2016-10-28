package me.contrapost.jee_quiz.ejb;

import me.contrapost.jee_quiz.entity.Category;
import me.contrapost.jee_quiz.entity.Quiz;
import me.contrapost.jee_quiz.entity.RootCategory;
import me.contrapost.jee_quiz.entity.SpecifyingCategory;
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

    @Test
    public void testUpdateCategoryTitle() {
        long rootCatId = createRootCategory("Root");
        long subCatId = createSubCategory("Sub", rootCatId);
        long specCatId = createSpecifyingCategory("Spec", subCatId);
        long quizId = createQuiz("Quiz", specCatId);

        String newRootName = "New root name";
        assertTrue(categoryEJB.updateCategoryTitle(rootCatId, newRootName));
        assertEquals(newRootName, categoryEJB.getRootCategory(rootCatId).getTitle());
        assertEquals(newRootName, quizEJB.getQuiz(quizId).getSpecifyingCategory().
                getSubCategory().getRootCategory().getTitle());
    }

    @Test
    public void testGetAllQuizesForCategory() {
        // Two root categories
        long rootCat1Id = createRootCategory("Root1");
        long rootCat2Id = createRootCategory("Root2");

        // Two sub categories belong to root category 1
        long subCat1Id = createSubCategory("Sub1", rootCat1Id);
        long subCat2Id = createSubCategory("Sub2", rootCat1Id);

        // Three subcategories belong to root category 2
        long subCat3Id = createSubCategory("Sub3", rootCat2Id);
        long subCat4Id = createSubCategory("Sub4", rootCat2Id);
        long subCat5Id = createSubCategory("Sub5", rootCat2Id);

        // Each subcategory has 3 specifying categories
        // Sub category 1
        long specCat1Id = createSpecifyingCategory("Spec1", subCat1Id);
        long specCat2Id = createSpecifyingCategory("Spec2", subCat1Id);
        long specCat3Id = createSpecifyingCategory("Spec3", subCat1Id);

        // Sub category 2
        long specCat4Id = createSpecifyingCategory("Spec4", subCat2Id);
        long specCat5Id = createSpecifyingCategory("Spec5", subCat2Id);
        long specCat6Id = createSpecifyingCategory("Spec6", subCat2Id);

        // Sub category 3
        long specCat7Id = createSpecifyingCategory("Spec7", subCat3Id);
        long specCat8Id = createSpecifyingCategory("Spec8", subCat3Id);
        long specCat9Id = createSpecifyingCategory("Spec9", subCat3Id);

        // Sub category 4
        long specCat10Id = createSpecifyingCategory("Spec10", subCat4Id);
        long specCat11Id = createSpecifyingCategory("Spec11", subCat4Id);
        long specCat12Id = createSpecifyingCategory("Spec12", subCat4Id);

        // Sub category 5
        long specCat13Id = createSpecifyingCategory("Spec13", subCat5Id);
        long specCat14Id = createSpecifyingCategory("Spec14", subCat5Id);
        long specCat15Id = createSpecifyingCategory("Spec15", subCat5Id);

        // Each specifying category has 3 quizes
        // Spec category 1
        long quiz1Id = createQuiz("Quiz1", specCat1Id);
        long quiz2Id = createQuiz("Quiz2", specCat1Id);
        long quiz3Id = createQuiz("Quiz3", specCat1Id);

        // Spec category 2
        long quiz4Id = createQuiz("Quiz4", specCat2Id);
        long quiz5Id = createQuiz("Quiz5", specCat2Id);
        long quiz6Id = createQuiz("Quiz6", specCat2Id);

        // Spec category 3
        long quiz7Id = createQuiz("Quiz7", specCat3Id);
        long quiz8Id = createQuiz("Quiz8", specCat3Id);
        long quiz9Id = createQuiz("Quiz9", specCat3Id);

        // Spec category 4
        long quiz10Id = createQuiz("Quiz10", specCat4Id);
        long quiz11Id = createQuiz("Quiz11", specCat4Id);
        long quiz12Id = createQuiz("Quiz12", specCat4Id);

        // Spec category 5
        long quiz13Id = createQuiz("Quiz13", specCat5Id);
        long quiz14Id = createQuiz("Quiz14", specCat5Id);
        long quiz15Id = createQuiz("Quiz15", specCat5Id);

        // Spec category 6
        long quiz16Id = createQuiz("Quiz16", specCat6Id);
        long quiz17Id = createQuiz("Quiz17", specCat6Id);
        long quiz18Id = createQuiz("Quiz18", specCat6Id);

        // Spec category 7
        long quiz19Id = createQuiz("Quiz19", specCat7Id);
        long quiz20Id = createQuiz("Quiz20", specCat7Id);
        long quiz21Id = createQuiz("Quiz21", specCat7Id);

        // Spec category 8
        long quiz22Id = createQuiz("Quiz22", specCat8Id);
        long quiz23Id = createQuiz("Quiz23", specCat8Id);
        long quiz24Id = createQuiz("Quiz24", specCat8Id);

        // Spec category 9
        long quiz25Id = createQuiz("Quiz25", specCat9Id);
        long quiz26Id = createQuiz("Quiz26", specCat9Id);
        long quiz27Id = createQuiz("Quiz27", specCat9Id);

        // Spec category 10
        long quiz28Id = createQuiz("Quiz28", specCat10Id);
        long quiz29Id = createQuiz("Quiz29", specCat10Id);
        long quiz30Id = createQuiz("Quiz30", specCat10Id);

        // Spec category 11
        long quiz31Id = createQuiz("Quiz31", specCat11Id);
        long quiz32Id = createQuiz("Quiz32", specCat11Id);
        long quiz33Id = createQuiz("Quiz33", specCat11Id);

        // Spec category 12
        long quiz34Id = createQuiz("Quiz34", specCat12Id);
        long quiz35Id = createQuiz("Quiz35", specCat12Id);
        long quiz36Id = createQuiz("Quiz36", specCat12Id);

        // Spec category 13
        long quiz37Id = createQuiz("Quiz37", specCat13Id);
        long quiz38Id = createQuiz("Quiz38", specCat13Id);
        long quiz39Id = createQuiz("Quiz39", specCat13Id);

        // Spec category 14
        long quiz40Id = createQuiz("Quiz40", specCat14Id);
        long quiz41Id = createQuiz("Quiz41", specCat14Id);
        long quiz42Id = createQuiz("Quiz42", specCat14Id);

        // Spec category 15
        long quiz43Id = createQuiz("Quiz43", specCat15Id);
        long quiz44Id = createQuiz("Quiz44", specCat15Id);
        long quiz45Id = createQuiz("Quiz45", specCat15Id);

        // Root 1 has 2 subcategories, 6 specifying categories and 18 quizes
        // Root 2 has 3 subcategories, 9 specifying categories and 27 quizes
        // Each subcategory has 9 quizes

        assertEquals(18, categoryEJB.getAllQuizesForCategory(rootCat1Id).size());
        assertEquals(27, categoryEJB.getAllQuizesForCategory(rootCat2Id).size());
        assertTrue(categoryEJB
                        .getAllQuizesForCategory(specCat15Id) // Spec category 15 has quizes 43, 44, 45
                        .stream()
                        .map(Quiz::getId)
                        .anyMatch(id -> id == quiz45Id));
        assertTrue(categoryEJB
                        .getAllQuizesForCategory(subCat2Id)  // Subcategory 2 has quizes 10 - 18
                        .stream()
                        .map(Quiz::getId)
                        .anyMatch(id -> id == quiz14Id));
        assertTrue(categoryEJB
                        .getAllQuizesForCategory(rootCat1Id) // Root category 1 has quizes 1 - 18
                        .stream()
                        .map(Quiz::getId)
                        .anyMatch(id -> id == quiz16Id));
    }
}
