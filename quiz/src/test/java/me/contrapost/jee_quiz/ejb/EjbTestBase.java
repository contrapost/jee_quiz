package me.contrapost.jee_quiz.ejb;

import me.contrapost.jee_quiz.entity.RootCategory;
import me.contrapost.jee_quiz.entity.SpecifyingCategory;
import me.contrapost.jee_quiz.entity.SubCategory;
import me.contrapost.jee_quiz.util.DeleterEJB;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.After;
import org.junit.Before;

import javax.ejb.EJB;
import java.util.HashMap;
import java.util.Map;


public abstract class EjbTestBase {

    private Map<String, Boolean> answers;

    @Deployment
    public static JavaArchive createDeployment() {

        return ShrinkWrap.create(JavaArchive.class)
                .addPackages(true, "me.contrapost.jee_quiz")
                .addClass(DeleterEJB.class)
                .addPackages(true, "org.apache.commons.codec")
                .addAsResource("META-INF/persistence.xml");
    }

    @EJB
    protected QuizEJB quizEJB;

    @EJB
    protected CategoryEJB categoryEJB;

    @EJB
    protected DeleterEJB deleterEJB;


    @Before
    @After
    public void emptyDatabase(){
//        quizEJB.getAllQuizes().forEach(q ->
//                deleterEJB.deleteEntityById(Quiz.class, q.getId()));
        deleterEJB.deleteQuizes();
        deleterEJB.deleteEntities(SpecifyingCategory.class);

        deleterEJB.deleteEntities(SubCategory.class);
        deleterEJB.deleteEntities(RootCategory.class);
    }

    protected long createRootCategory(String title) {
        return categoryEJB.createRootCategory(title);
    }

    protected long createSubCategory(String title, long rootCategoryId) {
        return categoryEJB.createSubCategory(title, rootCategoryId);
    }

    protected long createSpecifyingCategory(String title, long subcategoryId) {
        return categoryEJB.createSpecifyingCategory(title, subcategoryId);
    }

    protected long createQuiz(String rootCategoryTitle, String subCategoryTitle, String specifyingCategoryTitle,
                              String question, Map<String, Boolean> answers) {
        long specifyingCategoryId = createSpecifyingCategory(specifyingCategoryTitle, createSubCategory(subCategoryTitle,
                createRootCategory(rootCategoryTitle)));
        return quizEJB.createQuiz(question, answers, specifyingCategoryId);
    }

    protected long createQuiz(String rootCategoryTitle, String subCategoryTitle, String specifyingCategoryTitle,
                              String question) {

        return createQuiz(rootCategoryTitle, subCategoryTitle, specifyingCategoryTitle, question, getAnswers());
    }

    protected long createQuiz(String question) {

        return createQuiz("RootCategory", "SubCategory", "SpecifyingCategory", question);
    }

    protected long createQuiz(String question, long specifyingCategoryId) {
        return quizEJB.createQuiz(question, getAnswers(), specifyingCategoryId);
    }

    public Map<String,Boolean> getAnswers() {
        Map<String, Boolean> answers = new HashMap<>();
        answers.put("Right", true);
        for (int i = 0; i < 3; i++) {
            answers.put("Wrong", false);
        }

        return answers;
    }
}
