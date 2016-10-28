package me.contrapost.jee_quiz.ejb;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ejb.EJBException;
import javax.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created by Alexander Shipunov on 24.10.16.
 * Test of QuizEJB
 */
@RunWith(Arquillian.class)
public class QuizEJBTest extends EjbTestBase {

    @Test
    public void testCreateQuiz() {

        String question = "Question";

        createQuiz(question);

        assertEquals(question, quizEJB.getAllQuizes().get(0).getQuestion());
    }

    @Test
    public void testDeleteQuiz() {
        long quizId = createQuiz("Question");

        assertTrue(quizEJB.deleteQuiz(quizId));
        assertNull(quizEJB.getQuiz(quizId));
        assertTrue(quizEJB.getAllQuizes().size() == 0);
    }

    @Test
    public void testUpdateQuizQuestion() {
        long quizId = createQuiz("Original question");

        String updatedQuestion = "Updated question text";

        assertTrue(quizEJB.updateQuizQuestion(quizId, updatedQuestion));
        assertEquals(quizEJB.getQuiz(quizId).getQuestion(), updatedQuestion);
    }

    @Test
    public void testUpdateAnswerMap() {
        Map<String, Boolean> answers = new HashMap<>();
        answers.put("Right", true);
        for (int i = 0; i < 3; i++) {
            answers.put("Wrong #" + i, false);
        }
        long quizId = createQuiz("Root", "Sub", "Spec", "Question", answers);


        String updatedAnswer = "Wrong Updated";
        assertTrue(quizEJB.updateAnswersMap(quizId, "Wrong #2", updatedAnswer));
        assertTrue(quizEJB.getQuiz(quizId).getAnswerMap().keySet().contains(updatedAnswer));
        assertFalse(quizEJB.getQuiz(quizId).getAnswerMap().keySet().contains("Wrong # 2"));
    }

    @Test
    public void testAnswersMapWithInvalidNumberOfAnswers() {
        Map<String, Boolean> answers = new HashMap<>();
        answers.put("Right", true);
        answers.put("Wrong", false); // Should be 2 more wrong answers


        try {
            createQuiz("Root", "Sub", "Spec", "Question", answers);
        } catch (EJBException e) {
//            Throwable cause = com.google.common.base.Throwables.getRootCause(e);
//            assertTrue("Cause: " + cause, cause instanceof ConstraintViolationException);
        }
    }

    @Test
    public void testAnswersMapWithNoCorrectAnswer() {
        Map<String, Boolean> answers = new HashMap<>();
        for (int i = 0; i < 4; i++) {
            answers.put("Answer #" + i, false);
        }

        try {
            createQuiz("Root", "Sub", "Spec", "Question", answers);
        } catch (EJBException e) {
//            Throwable cause = com.google.common.base.Throwables.getRootCause(e);
//            assertTrue("Cause: " + cause, cause instanceof ConstraintViolationException);
        }
    }

    @Test
    public void testCreateTwoQuizesWithSameQuestion() {
        String quizName = "Super duper quiz";
        createQuiz(quizName);

        try {
            createQuiz(quizName);
        } catch (EJBException e) {
//            Throwable cause = com.google.common.base.Throwables.getRootCause(e);
//            assertTrue("Cause: " + cause, cause instanceof ConstraintViolationException);
        }
    }

    @Test
    public void testUpdateAnswerMapForNonexistentQuiz() {
        try {
            quizEJB.updateAnswersMap(0L, "New answer", "Previous answer");
        } catch (EJBException e) {
//            Throwable cause = com.google.common.base.Throwables.getRootCause(e);
//            assertTrue("Cause: " + cause, cause instanceof ConstraintViolationException);
        }
    }
}
